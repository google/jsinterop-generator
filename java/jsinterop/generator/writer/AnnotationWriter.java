/*
 * Copyright 2015 Google Inc.
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
package jsinterop.generator.writer;

import static jsinterop.generator.model.AnnotationType.JS_PACKAGE;

import com.google.common.base.Joiner;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.AnnotationType;
import jsinterop.generator.model.TypeReference;

/** AnnotationWriter generates java code for {@link Annotation} instances. */
public class AnnotationWriter {

  public static void emit(Annotation annotation, CodeWriter codeWriter) {
    AnnotationType annotationType = annotation.getType();

    codeWriter.emit("@").emitTypeReference(annotationType.getType());

    String annotationParameters =
        Joiner.on(",")
            .skipNulls()
            .join(
                annotation.getIsNativeAttribute() ? "isNative = true" : null,
                annotation.getNameAttribute() != null
                    ? "name = \"" + annotation.getNameAttribute() + "\""
                    : null,
                annotationType.isTypeAnnotation()
                    ? "namespace = " + getGlobalNamespace(codeWriter)
                    : null);

    if (!annotationParameters.isEmpty()) {
      codeWriter.emit("(").emit(annotationParameters).emit(")");
    }

    codeWriter.emitNewLine();
  }

  private static String getGlobalNamespace(CodeWriter codeWriter) {
    TypeReference jsPackage = JS_PACKAGE.getType();
    boolean imported = codeWriter.addImport(jsPackage);

    return (imported ? jsPackage.getTypeName() : jsPackage.getImport()) + ".GLOBAL";
  }

  private AnnotationWriter() {}
}
