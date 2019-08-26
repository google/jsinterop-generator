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

import com.google.auto.value.AutoValue;
import com.google.javascript.jscomp.SourceFile;
import java.util.List;
import javax.annotation.Nullable;

/** Options for the closure jsinterop generator. */
@AutoValue
public abstract class Options {
  @AutoValue.Builder
  abstract static class Builder {
    abstract Builder outputJarFile(String outputJarFile);

    abstract Builder outputDependencyFile(String outputDependencyFile);

    abstract Builder packagePrefix(@Nullable String packagePrefix);

    abstract Builder extensionTypePrefix(String extensionTypePrefix);

    abstract Builder debugEnabled(boolean debugEnabled);

    abstract Builder beanConventionUsed(boolean beanConventionUsed);

    abstract Builder dependencyMappingFiles(List<String> dependencyFiles);

    abstract Builder dependencies(List<SourceFile> dependencyFiles);

    abstract Builder sources(List<SourceFile> sources);

    abstract Builder nameMappingFiles(List<String> nameMappingFiles);

    abstract Builder integerEntitiesFiles(List<String> integerMappingFiles);

    abstract Builder wildcardTypesFiles(List<String> wildcardTypesFiles);

    abstract Builder globalScopeClassName(String globalScopeClassName);

    abstract Builder strict(boolean strict);

    abstract Builder customPreprocessingPasses(List<String> customPreprocessingPasses);

    abstract Options build();
  }

  public abstract String getOutputJarFile();

  public abstract String getOutputDependencyFile();

  @Nullable
  public abstract String getPackagePrefix();

  public abstract String getGlobalScopeClassName();

  public abstract String getExtensionTypePrefix();

  public abstract boolean isDebugEnabled();

  public abstract boolean isBeanConventionUsed();

  public abstract boolean isStrict();

  /**
   * Returns list of the generated dependency files that contains the mapping between the native fqn
   * and the java fqn of types provided by the dependencies.
   */
  public abstract List<String> getDependencyMappingFiles();

  /** Returns list of the dependency files. */
  public abstract List<SourceFile> getDependencies();

  public abstract List<SourceFile> getSources();

  public abstract List<String> getNameMappingFiles();

  public abstract List<String> getIntegerEntitiesFiles();

  public abstract List<String> getWildcardTypesFiles();

  public abstract List<String> getCustomPreprocessingPasses();

  static Builder builder() {
    return new AutoValue_Options.Builder();
  }
}
