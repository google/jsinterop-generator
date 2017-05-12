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

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/** Create the source jar file */
public class JarFileCreator {
  /** Use the same fixed deterministic timestamp as JarSanitizer. */
  private static final long FIXED_TIMESTAMP =
      new GregorianCalendar(2010, 0, 1, 0, 0, 0).getTimeInMillis();

  public static void generateJarFile(String jarPath, List<JavaFile> javaFiles) throws IOException {
    try (JarOutputStream jarFile = new JarOutputStream(new FileOutputStream(jarPath))) {
      writeManifest(jarFile);

      // write java file
      for (JavaFile file : javaFiles) {
        addFile(jarFile, file);
      }

      // close the last entry
      jarFile.closeEntry();
    }
  }

  private static void writeManifest(JarOutputStream jarFile) throws IOException {
    Manifest manifest = new Manifest();
    manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
    initNextEntry(JarFile.MANIFEST_NAME, jarFile);
    manifest.write(new BufferedOutputStream(jarFile));
  }

  private static void addFile(JarOutputStream jarFile, JavaFile javaFile) throws IOException {
    initNextEntry(javaFile.getFilePath(), jarFile);
    ByteSource byteSource =
        CharSource.wrap(javaFile.getFileContent()).asByteSource(StandardCharsets.UTF_8);
    byteSource.copyTo(jarFile);
  }

  private static void initNextEntry(String filePath, JarOutputStream jarFile) throws IOException {
    // ensure to close the previous entry if it exists
    jarFile.closeEntry();

    JarEntry entry = new JarEntry(filePath);
    // ensure the output is deterministic by always setting the same timestamp for all files.
    entry.setTime(FIXED_TIMESTAMP);
    jarFile.putNextEntry(entry);
  }
}
