/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jsinterop.generator.closure;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.SourceFile;

/** Helper class for creating closure JsInterop Generator options. */
public final class TestUtil {
  private TestUtil() {}

  private static Options createOptions(SourceFile sourceFile) {
    return Options.builder()
        .sources(ImmutableList.of(sourceFile))
        .outputJarFile("unused")
        .outputDependencyFile("unused")
        .globalScopeClassName("unused")
        .extensionTypePrefix("unused")
        .debugEnabled(false)
        .beanConventionUsed(false)
        .strict(true)
        .dependencyMappingFiles(ImmutableList.of())
        .dependencies(ImmutableList.of())
        .nameMappingFiles(ImmutableList.of())
        .integerEntitiesFiles(ImmutableList.of())
        .wildcardTypesFiles(ImmutableList.of())
        .customPreprocessingPasses(ImmutableList.of())
        .build();
  }

  public static void runClosureJsInteropGenerator(SourceFile sourceFile) {
    new ClosureJsInteropGenerator(createOptions(sourceFile)).convert();
  }
}
