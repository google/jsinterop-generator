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
package jsinterop.generator.visitor;

import static jsinterop.generator.helper.GeneratorUtils.maybeRemoveClutzNamespace;
import static jsinterop.generator.model.AnnotationType.JS_TYPE;

import jsinterop.generator.model.AbstractVisitor;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.Entity;
import jsinterop.generator.model.ModelVisitor;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.Type;

/** This visitor is in charge to remove the clutz namespace if present. */
public class NamespaceAttributeRewriter implements ModelVisitor {

  @Override
  public void applyTo(Program program) {
    program.accept(
        new AbstractVisitor() {
          @Override
          public boolean enterType(Type type) {
            if (type.isExtern()) {
              // because we won't emit any code for extern type, we don't need to fix the namespace.
              return false;
            }

            if (isNativeJsType(type)) {
              maybeRewriteAnnotationAttributes(type);
            }

            // The namespace for native JsTypes is fixed at the type level and we don't set
            // namespace on theirs members. We don't need to visit them.
            // Namespaces for non native JsTypes don't need to be fixed.
            // We don't generate any non-JsType classes.
            return false;
          }
        });
  }

  private static boolean isNativeJsType(Type currentType) {
    return currentType.hasAnnotation(JS_TYPE)
        && currentType.getAnnotation(JS_TYPE).getIsNativeAttribute();
  }

  private static void maybeRewriteAnnotationAttributes(Entity owner) {
    Annotation originalAnnotation = owner.getAnnotation(JS_TYPE);

    String name = originalAnnotation.getNameAttribute();

    if (name == null) {
      return;
    }

    String cleanName = maybeRemoveClutzNamespace(name);

    if (!cleanName.equals(name)) {
      owner.removeAnnotation(JS_TYPE);
      owner.addAnnotation(originalAnnotation.withNameAttribute(cleanName));
    }
  }
}
