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
import static jsinterop.generator.model.PredefinedTypes.JS;
import static jsinterop.generator.model.PredefinedTypes.JS_PROPERTY_MAP;
import static jsinterop.generator.model.PredefinedTypes.OBJECT;

import com.google.common.collect.ImmutableList;
import jsinterop.generator.model.AbstractVisitor;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.JavaTypeReference;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.MethodInvocation;
import jsinterop.generator.model.ModelVisitor;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.ReturnStatement;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeQualifier;
import jsinterop.generator.model.TypeReference;

/**
 * A dictionary type is an interface that contains only JsProperty methods.
 *
 * <p>When we reach this kind of type, we add a static factory in order to ease the instantiation of
 * the type and avoid that end-users need to provide an implementation.
 */
public class DictionaryTypeVisitor implements ModelVisitor {
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
      factory.addAnnotation(Annotation.builder().type(JS_OVERLAY).build());
      TypeReference returnType = new JavaTypeReference(type);
      if (!type.getTypeParameters().isEmpty()) {
        factory.setTypeParameters(ImmutableList.copyOf(type.getTypeParameters()));
        returnType = new ParametrizedTypeReference(returnType, type.getTypeParameters());
      }
      factory.setReturnType(returnType);
      // body statement: return Js.uncheckedCast(JsPropertyMap.of());
      factory.setBody(
          new ReturnStatement(
              MethodInvocation.builder()
                  .setInvocationTarget(new TypeQualifier(JS.getReference()))
                  .setMethodName("uncheckedCast")
                  .setArgumentTypes(OBJECT.getReference())
                  .setArguments(
                      MethodInvocation.builder()
                          .setInvocationTarget(new TypeQualifier(JS_PROPERTY_MAP.getReference()))
                          .setMethodName("of")
                          .build())
                  .build()));

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
