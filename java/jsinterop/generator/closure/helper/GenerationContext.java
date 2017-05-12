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
 *
 */

package jsinterop.generator.closure.helper;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.rhino.StaticSourceFile;
import java.util.Collection;
import java.util.Map;
import jsinterop.generator.model.Program;

/** Keep contextual information on the current generation. */
@AutoValue
public abstract class GenerationContext {

  /** Builder for {@link GenerationContext} */
  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder compiler(Compiler compiler);

    public abstract Builder javaProgram(Program javaProgram);

    public abstract Builder typeRegistry(ClosureTypeRegistry typeRegistry);

    public abstract Builder sourceFiles(Collection<? extends StaticSourceFile> externFiles);

    public abstract Builder externDependencyFiles(
        Collection<? extends StaticSourceFile> externFiles);

    public abstract Builder nameMapping(Map<String, String> nameMapping);

    public abstract GenerationContext build();
  }

  public abstract Compiler getCompiler();

  public abstract ImmutableSet<StaticSourceFile> getSourceFiles();

  public abstract ImmutableSet<StaticSourceFile> getExternDependencyFiles();

  public abstract Program getJavaProgram();

  public abstract ClosureTypeRegistry getTypeRegistry();

  public abstract ImmutableMap<String, String> getNameMapping();

  public static Builder builder() {
    return new AutoValue_GenerationContext.Builder();
  }
}
