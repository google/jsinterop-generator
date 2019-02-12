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

import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.Entity;
import jsinterop.generator.model.Type;

/**
 * For the time being we only generate JsType coming from extern files or d.ts file. If a type is
 * defined under a namespace, we need to put the namespace in the name attribute of the JsInterop
 * annotation in order to force J2Cl not to emit a goog.require for the type.
 *
 * <p>For example, instead of emitting: {@code @JsType(isNative = true, name = "Foo", namespace = "Bar")}
 * emit {@code @JsType(isNative = true, name = "Bar.Foo", namespace = JsPackage.GLOBAL)}</p>
 *
 * <p>This visitor is in charge to remove the clutz namespace if present.</p>
 */
public class NamespaceAttributeRewriter extends AbstractModelVisitor {

  @Override
  public boolean visit(Type type) {
    if (type.isExtern()) {
      // because we won't emit any code for extern type, we don't need to fix the namespace.
      return false;
    }

    if (isNativeJsType(type)) {
      maybeRewriteAnnotationAttributes(type, type.getPackageName());
    }

    // The namespace for native JsTypes is fixed at the type level and we don't set namespace on
    // theirs members. We don't need to visit them
    // Namespaces for non native JsTypes don't need to be fixed.
    // We don't generate any non-JsType classes.
    return false;
  }

  private boolean isNativeJsType(Type currentType) {
    return currentType.hasAnnotation(JS_TYPE)
        && currentType.getAnnotation(JS_TYPE).getIsNativeAttribute();
  }

  private void maybeRewriteAnnotationAttributes(Entity owner, String defaultOwnerNamespace) {
    Annotation originalAnnotation = owner.getAnnotation(JS_TYPE);

    String namespace = originalAnnotation.getNamespaceAttribute();

    if (namespace == null) {
      // if namespace is null, it means that the real namespace is the package name of the JsType.
      namespace = defaultOwnerNamespace;
    }

    String cleanedNamespace = maybeRemoveClutzNamespace(namespace);

    Annotation newAnnotation = null;
    if (!cleanedNamespace.isEmpty()) {
      String name = originalAnnotation.getNameAttribute();
      if (name == null) {
        name = owner.getName();
      }

      // prefix the name with the namespace and change namespace to global namespace (modeled in
      // our java model by an empty string).
      newAnnotation =
          originalAnnotation
              .withNameAttribute(cleanedNamespace + "." + name)
              .withNamespaceAttribute("");

    } else if (!namespace.isEmpty()) {
      // namespace is the clutz global namespace. Change it to the global namespace.
      newAnnotation = originalAnnotation.withNamespaceAttribute("");
    }

    if (newAnnotation != null) {
      owner.removeAnnotation(JS_TYPE);
      owner.addAnnotation(newAnnotation);
    }
  }
}
