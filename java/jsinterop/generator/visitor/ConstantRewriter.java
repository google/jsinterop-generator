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
package jsinterop.generator.visitor;

import static jsinterop.generator.model.AccessModifier.DEFAULT;
import static jsinterop.generator.model.AnnotationType.JS_OVERLAY;
import static jsinterop.generator.model.AnnotationType.JS_PROPERTY;
import static jsinterop.generator.model.AnnotationType.JS_TYPE;

import com.google.common.base.Preconditions;
import jsinterop.generator.model.AbstractVisitor;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.EntityKind;
import jsinterop.generator.model.Field;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.Type;

/**
 * Rewrite constants in classes and interfaces so that are converted to JsOverlay and initialized by
 * referring to a XXX_Constants class that points to native fields. By this way, we can support
 * constants on interfaces (as they can only have final fields) and also we can keep constants in
 * class final to catch usage errors.
 */
public class ConstantRewriter extends AbstractModelVisitor {
  @Override
  public void applyTo(Program program) {
    program.accept(
        new AbstractVisitor() {
          @Override
          public void exitType(Type type) {
            if (type.hasAnnotation(JS_TYPE)) {
              processConstants(type, program);
            }
          }
        });
  }

  /**
   * We convert native static fields by defining an extra native JsType that will contain private
   * static fields that target the javascript constant symbols. Then we reexpose those fields on the
   * original type by using JsOverlay static final field.
   */
  private static void processConstants(Type originalType, Program program) {
    Preconditions.checkState(
        !originalType.isInterface()
            || originalType.getFields().stream()
                .noneMatch(f -> f.isStatic() && !f.isNativeReadOnly()),
        "Interface static fields needs to be constant");

    Type constantsClass = createConstantsClass(originalType);

    originalType.getFields().stream()
        .filter(f -> f.isStatic() && f.isNativeReadOnly())
        .forEach(
            f -> {
              Field fieldInConstantsClass = Field.from(f);
              fieldInConstantsClass.setAccessModifier(DEFAULT);
              constantsClass.addField(fieldInConstantsClass);

              f.removeAnnotation(JS_PROPERTY);
              f.addAnnotation(Annotation.builder().type(JS_OVERLAY).build());
              f.setFinal(true);
              f.setInitialValue(constantsClass.getName() + "." + f.getName());
            });

    if (!constantsClass.getFields().isEmpty()) {
      program.addType(constantsClass);
    }
  }

  private static Type createConstantsClass(Type originalType) {
    Type constantWrapper = new Type(EntityKind.CLASS);
    constantWrapper.setAccessModifier(DEFAULT);
    constantWrapper.setPackageName(originalType.getPackageName());
    constantWrapper.setName(originalType.getName() + "__Constants");
    constantWrapper.setExtern(originalType.isExtern());

    Annotation jsTypeAnnotation = originalType.getAnnotation(JS_TYPE);
    if (jsTypeAnnotation.getNameAttribute() == null) {
      jsTypeAnnotation = jsTypeAnnotation.withNameAttribute(originalType.getName());
    }
    constantWrapper.addAnnotation(jsTypeAnnotation);

    return constantWrapper;
  }
}
