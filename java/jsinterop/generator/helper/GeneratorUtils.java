/*
 * Copyright 2017 Google Inc.
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
package jsinterop.generator.helper;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Iterables.transform;
import static java.util.stream.Collectors.joining;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Utilities methods. */
public class GeneratorUtils {
  /**
   * An abstraction for reading files. The implementation differs based on where the generator runs
   * (JVM vs. JavaScript environment).
   */
  public interface FileReader {
    String read(String filePath) throws IOException;
  }

  // TODO(dramaix): remove this constant once clutz is no longer used.
  private static final String CLUTZ_GLOBAL_NAMESPACE = "ಠ_ಠ.clutz";
  public static String extractNamespace(String nativeFqn, String nativeTypeName) {
    if (nativeFqn.length() == nativeTypeName.length()) {
      return "";
    }
    return nativeFqn.substring(0, nativeFqn.length() - nativeTypeName.length() - 1);
  }

  public static String extractName(String nativeFqn) {
    return checkNotNull(nativeFqn).substring(nativeFqn.lastIndexOf('.') + 1);
  }

  public static String maybeAppendPrefix(String prefix, String namespace) {
    if (isNullOrEmpty(prefix)) {
      return namespace;
    }

    if (isNullOrEmpty(namespace)) {
      return prefix;
    }

    return prefix + "." + namespace;
  }

  public static String createJavaPackage(String nativeNamespace, String packagePrefix) {
    String cleanedTsNamespace = maybeRemoveClutzNamespace(nativeNamespace);

    // ensure all package are in lower case to avoid clash with type name
    cleanedTsNamespace =
        Joiner.on('.')
            .join(transform(Splitter.on('.').split(cleanedTsNamespace), String::toLowerCase));

    return maybeAppendPrefix(packagePrefix, cleanedTsNamespace);
  }

  // TODO(dramaix): remove this method once clutz is no longer used.
  public static String maybeRemoveClutzNamespace(String namespace) {
    // Clutz add its own internal namespace that acts as global namespace. Remove it if present
    if (namespace.startsWith(CLUTZ_GLOBAL_NAMESPACE)) {
      int substringIndex = CLUTZ_GLOBAL_NAMESPACE.length();

      if (namespace.length() > substringIndex) {
        // remove the trailing dot
        substringIndex++;
      }

      return namespace.substring(substringIndex);
    }

    return namespace;
  }

  /**
   * The way we build type name makes it very unlikely that we get conflict with existing types. If
   * a collision is detected, we append a number until we find a non colliding name.
   */
  public static String toSafeTypeName(String typeName, Set<String> existingNames) {
    int counter = 0;

    String prefixName = typeName;
    while (existingNames.contains(typeName)) {
      typeName = prefixName + counter++;
    }

    return typeName;
  }

  public static String toCamelUpperCase(String lowerCamelCaseString) {
    return LOWER_CAMEL.to(UPPER_CAMEL, lowerCamelCaseString);
  }

  public static String toCamelLowerCase(String upperCamelCaseString) {
    return UPPER_CAMEL.to(LOWER_CAMEL, upperCamelCaseString);
  }

  public static String addSuffix(
      String typeName, String suffix, List<String> suffixToRemoveFromOrigin) {
    String prefix = typeName;

    for (String s : suffixToRemoveFromOrigin) {
      if (typeName.toUpperCase().endsWith(s.toUpperCase())) {
        prefix = typeName.substring(0, typeName.length() - s.length());
        break;
      }
    }

    return prefix + suffix;
  }

  public static Map<String, String> readKeyValueFiles(
      List<String> filePaths, FileReader fileReader) {
    return Splitter.on('\n')
        .omitEmptyStrings()
        .withKeyValueSeparator('=')
        .split(readFiles(filePaths, fileReader));
  }

  public static List<String> readListFiles(List<String> filePaths, FileReader fileReader) {
    return Splitter.on('\n').omitEmptyStrings().splitToList(readFiles(filePaths, fileReader));
  }

  private static String readFiles(List<String> filePaths, FileReader reader) {
    return filePaths
        .stream()
        .map(
            path -> {
              try {
                return reader.read(path);
              } catch (IOException e) {
                throw new RuntimeException("Unable to read the renaming file " + path, e);
              }
            })
        .collect(joining("\n"));
  }

  private GeneratorUtils() {}
}
