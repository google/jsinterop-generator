/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package jsinterop.generator.closure;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.javascript.jscomp.CompilerOptions.LanguageMode.ECMASCRIPT5;
import static java.io.File.separatorChar;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.TypedScope;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jsinterop.generator.closure.helper.ClosureTypeRegistry;
import jsinterop.generator.closure.helper.GenerationContext;
import jsinterop.generator.closure.visitor.AnonymousTypeCollector;
import jsinterop.generator.closure.visitor.InheritanceVisitor;
import jsinterop.generator.closure.visitor.MemberCollector;
import jsinterop.generator.closure.visitor.TypeCollector;
import jsinterop.generator.closure.visitor.TypeParameterCollector;
import jsinterop.generator.helper.GeneratorUtils;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.Type;
import jsinterop.generator.visitor.DependencyFileWriter;
import jsinterop.generator.visitor.VisitorHelper;
import jsinterop.generator.writer.CodeWriter;
import jsinterop.generator.writer.TypeWriter;

class ClosureJsInteropGenerator {
  private final Options options;
  private final Compiler compiler;

  public ClosureJsInteropGenerator(Options options) {
    this.options = options;
    this.compiler = new Compiler();
  }

  public void convert() {
    Program javaProgram = generateJavaProgram();

    finalizeProgram(javaProgram);

    generateJarFile(javaProgram);

    generateDependencyFile(javaProgram);
  }

  private void generateDependencyFile(Program javaProgram) {
    DependencyFileWriter dependencyFileWriter = new DependencyFileWriter();
    dependencyFileWriter.accept(javaProgram);

    try {
      Files.write(
          dependencyFileWriter.getDependencyFileContent(),
          new File(options.getOutputDependencyFile()),
          UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Unable to create dependency file", e);
    }
  }

  private void finalizeProgram(Program javaProgram) {
    new IObjectIArrayLikeCleaner().accept(javaProgram);

    VisitorHelper.finalizeJavaProgram(
        javaProgram,
        options.isBeanConventionUsed(),
        readListFiles(options.getIntegerEntitiesFiles()),
        readKeyValueFiles(options.getWildcardTypesFiles()));
  }

  private void generateJarFile(Program javaProgram) {
    try {
      JarFileCreator.generateJarFile(
          options.getOutputJarFile(),
          javaProgram
              .getAllTypes()
              .stream()
              .filter(t -> !t.isExtern())
              .map(this::createJavaFile)
              .collect(toImmutableList()));
    } catch (IOException e) {
      throw new RuntimeException("Unable to create jar file", e);
    }
  }

  private JavaFile createJavaFile(Type type) {
    String filePath = type.getJavaFqn().replace('.', separatorChar) + ".java";

    CodeWriter codeWriter = new CodeWriter(type);
    TypeWriter.emit(type, codeWriter);
    String fileContent = codeWriter.generateCode();

    return JavaFile.builder().fileContent(fileContent).filePath(filePath).build();
  }

  private Program generateJavaProgram() {
    List<SourceFile> allSources =
        ImmutableList.<SourceFile>builder()
            .addAll(options.getDependencies())
            .addAll(options.getSources())
            .build();

    compiler.compile(new ArrayList<>(), allSources, createCompilerOptions());

    GenerationContext ctx =
        GenerationContext.builder()
            .compiler(compiler)
            .sourceFiles(options.getSources())
            .externDependencyFiles(options.getDependencies())
            // TODO(b/36178451): set the map in the context directly
            .javaProgram(new Program(readKeyValueFiles(options.getDependencyMappingFiles())))
            .typeRegistry(new ClosureTypeRegistry())
            .nameMapping(readKeyValueFiles(options.getNameMappingFiles()))
            .build();

    TypedScope topScope = compiler.getTopScope();

    new TypeCollector(ctx, options.getPackagePrefix(), options.getExtensionTypePrefix())
        .accept(topScope);

    new AnonymousTypeCollector(ctx).accept(topScope);

    new MemberCollector(ctx).accept(topScope);

    new InheritanceVisitor(ctx).accept(topScope);

    new TypeParameterCollector(ctx).accept(topScope);

    return ctx.getJavaProgram();
  }

  private static Map<String, String> readKeyValueFiles(List<String> filePaths) {
    return GeneratorUtils.readKeyValueFiles(
        filePaths, p -> Files.asCharSource(new File(p), UTF_8).read());
  }

  private List<String> readListFiles(List<String> filePaths) {
    return GeneratorUtils.readListFiles(
        filePaths, p -> Files.asCharSource(new File(p), UTF_8).read());
  }

  private CompilerOptions createCompilerOptions() {
    CompilerOptions options = new CompilerOptions();

    options.setLanguageOut(ECMASCRIPT5);
    options.setChecksOnly(true);
    options.setStrictModeInput(true);
    options.setCheckTypes(true);
    options.setPreserveDetailedSourceInfo(true);

    return options;
  }
}
