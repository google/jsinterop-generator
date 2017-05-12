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

import static com.google.common.base.Strings.isNullOrEmpty;
import static jsinterop.generator.helper.GeneratorUtils.maybeRemoveClutzNamespace;
import static jsinterop.generator.model.AnnotationType.JS_PACKAGE;

import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.AnnotationType;
import jsinterop.generator.model.TypeReference;

/** AnnotationWriter generates java code for {@link Annotation} instances. */
public class AnnotationWriter {

  public static void emit(Annotation annotation, CodeWriter codeWriter) {
    AnnotationType annotationType = annotation.getType();

    codeWriter.emit("@").emitTypeReference(annotationType.getType());

    String parameters = "";

    // We don;t use a generic way to handle attributes because the annotations we used
    // has 3 attributes max. If it's no longer the case in the future, please refactor
    // this part.
    if (annotationType.hasIsNativeAttribute() && annotation.getIsNativeAttribute()) {
      parameters += "isNative = true";
    }

    if (annotationType.hasNameAttribute() && !isNullOrEmpty(annotation.getNameAttribute())) {
      if (!isNullOrEmpty(parameters)) {
        parameters += ", ";
      }

      parameters += "name = \"" + annotation.getNameAttribute() + "\"";
    }

    if (annotationType.hasNamespaceAttribute() && annotation.getNamespaceAttribute() != null) {
      if (!isNullOrEmpty(parameters)) {
        parameters += ", ";
      }

      String cleanedNamespace = maybeRemoveClutzNamespace(annotation.getNamespaceAttribute());

      if (cleanedNamespace.length() == 0) {
        TypeReference jsPackage = JS_PACKAGE.getType();
        boolean imported = codeWriter.addImport(jsPackage);

        parameters += "namespace = ";
        parameters += imported ? jsPackage.getTypeName() : jsPackage.getImport();
        parameters += ".GLOBAL";
      } else {
        parameters += "namespace = \"" + cleanedNamespace + "\"";
      }
    }

    if (!isNullOrEmpty(parameters)) {
      codeWriter.emit("(").emit(parameters).emit(")");
    }

    codeWriter.emitNewLine();
  }

  private AnnotationWriter() {}
}
