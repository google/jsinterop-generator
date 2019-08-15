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

import static jsinterop.generator.model.AnnotationType.JS_OVERLAY;

import com.google.common.collect.ImmutableList;
import jsinterop.generator.model.AbstractVisitor;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.JavaTypeReference;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.MethodInvocation;
import jsinterop.generator.model.PredefinedTypeReference;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.ReturnStatement;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeQualifier;

/**
 * A dictionary type is an interface that contains only JsProperty methods.
 *
 * <p>When we reach this kind of type, we add a static factory in order to ease the instantiation of
 * the type and avoid that end-users need to provide an implementation.
 */
public class DictionaryTypeVisitor extends AbstractModelVisitor {
  @Override
  public void applyTo(Program program) {
    program.accept(
        new AbstractVisitor() {
          @Override
          public void exitType(Type type) {
            if (isDictionaryType(type)) {
              addFactoryMethod(type);
            }
          }
        });
  }

  private static void addFactoryMethod(Type type) {
    if (isDictionaryType(type)) {
      Method factory = new Method();
      factory.setStatic(true);
      factory.setName("create");
      factory.setReturnType(new JavaTypeReference(type));
      factory.addAnnotation(Annotation.builder().type(JS_OVERLAY).build());
      // body statement: return Js.uncheckedCast(JsPropertyMap.of());
      factory.setBody(
          new ReturnStatement(
              new MethodInvocation(
                  new TypeQualifier(PredefinedTypeReference.JS),
                  "uncheckedCast",
                  ImmutableList.of(PredefinedTypeReference.OBJECT),
                  ImmutableList.of(
                      new MethodInvocation(
                          new TypeQualifier(PredefinedTypeReference.JS_PROPERTY_MAP),
                          "of",
                          ImmutableList.of(),
                          ImmutableList.of())))));

      type.addMethod(factory);
    }
  }

  private static boolean isDictionaryType(Type type) {
    // this visitor is ran before the fields are converted to JsProperty methods.
    return type.isInterface()
        && type.isStructural()
        && !type.getFields().isEmpty()
        && type.getMethods().isEmpty();
  }
}
