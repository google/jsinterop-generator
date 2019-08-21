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
 *
 */

package jsinterop.generator.visitor;

import static jsinterop.generator.model.AnnotationType.JS_METHOD;
import static jsinterop.generator.model.AnnotationType.JS_OVERLAY;
import static jsinterop.generator.model.AnnotationType.JS_PROPERTY;
import static jsinterop.generator.model.AnnotationType.JS_TYPE;
import static jsinterop.generator.model.EntityKind.CONSTRUCTOR;
import static jsinterop.generator.model.PredefinedTypeReference.INT;
import static jsinterop.generator.model.PredefinedTypeReference.LONG;
import static jsinterop.generator.model.PredefinedTypeReference.OBJECT;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import jsinterop.generator.model.AbstractRewriter;
import jsinterop.generator.model.AnnotationType;
import jsinterop.generator.model.Entity;
import jsinterop.generator.model.Field;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Parameter;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.Type;

/** Ensure that all our java identifier are valid in Java. */
public class ValidJavaIdentifierVisitor extends AbstractModelVisitor {
  private static final ImmutableSet<String> JAVA_RESERVERD_WORDS =
      ImmutableSet.copyOf(
          Splitter.on(' ')
              .omitEmptyStrings()
              .split(
                  "abstract continue for new switch assert default goto package synchronized "
                      + "boolean do if private this break double implements protected throw byte "
                      + "else import public throws case enum instanceof return transient catch "
                      + "extends int short try char final interface static void class finally long "
                      + "strictfp volatile const float native super while null true false"));

  // TODO(b/67912344): add a logic driven by config file that remanes any entity.
  private static final ImmutableSet<String> TYPES_TO_PREFIX =
      ImmutableSet.copyOf(
          Splitter.on(' ')
              .omitEmptyStrings()
              .split(
                  "Array Boolean Byte Character Class Date Double Enum Error Float Integer Iterable"
                      + " Iterator IteratorIterable IIterableResult Long Map Math Number Object"
                      + " RegExp Set Short String StringBuffer StringBuilder WeakMap WeakSet"));

  // 1. In Javascript, static and instance method can have the same signature. This is invalid in
  // Java. To avoid to conflict with instance methods defined on java.lang.Object, we need to detect
  // static methods with the same signature and rename them.
  // 2. We rename  also instance methods that conflict with those on java.lang.Object in order to
  // to avoid confusion and potential compile errors due to override of final method.
  private static final ImmutableSet<Method> OBJECT_METHODS_TO_RENAME =
      ImmutableSet.<Method>builder()
          .addAll(methods("getClass"))
          .addAll(methods("hashCode"))
          .addAll(methods("equals", Parameter.builder().setName("o").setType(OBJECT).build()))
          .addAll(methods("toString"))
          .addAll(methods("clone"))
          .addAll(methods("notify"))
          .addAll(methods("notifyAll"))
          .addAll(methods("wait"))
          .addAll(methods("wait", Parameter.builder().setName("timeout").setType(LONG).build()))
          .addAll(
              methods(
                  "wait",
                  Parameter.builder().setName("timeout").setType(LONG).build(),
                  Parameter.builder().setName("nanos").setType(INT).build()))
          .addAll(methods("finalize"))
          .build();

  private static List<Method> methods(String name, Parameter... parameters) {
    List<Parameter> parameterList = ImmutableList.copyOf(parameters);
    return ImmutableList.of(method(name, parameterList, false), method(name, parameterList, true));
  }

  private static Method method(String name, List<Parameter> parameterList, boolean isStatic) {
    Method method = new Method();
    method.setName(name);
    for (Parameter parameter : parameterList) {
      method.addParameter(Parameter.from(parameter));
    }
    method.setStatic(isStatic);

    return method;
  }

  private static String maybeEscapeJavaClassName(String className, String nativeFqn) {
    if (TYPES_TO_PREFIX.contains(nativeFqn)) {
      return "Js" + className;
    }

    return className;
  }

  private static String toValidJavaIdentifier(String identifier) {
    if (JAVA_RESERVERD_WORDS.contains(identifier)) {
      // find maybe another way to escape reserved words
      return identifier + "_";
    }
    return identifier;
  }

  @Override
  public void applyTo(Program program) {
    program.accept(
        new AbstractRewriter() {
          @Override
          public Type rewriteType(Type type) {
            if (!type.isExtern()) {
              validEntityName(type, JS_TYPE, false);

              String originalName = type.getName();
              // in order to avoid Clash with java.lang classes.
              String validName = maybeEscapeJavaClassName(originalName, type.getNativeFqn());

              if (!validName.equals(originalName)) {
                type.setName(validName);

                addAnnotationNameAttributeIfNotEmpty(type, originalName, JS_TYPE, false);
              }
            }
            return type;
          }

          @Override
          public Field rewriteField(Field field) {
            validEntityName(field, JS_PROPERTY, true);
            return field;
          }

          @Override
          public Method rewriteMethod(Method method) {
            if (method.getKind() == CONSTRUCTOR) {
              // constructor are implemented as method without name
              return method;
            }

            AnnotationType jsInteropAnnotation;
            if (method.hasAnnotation(JS_PROPERTY)) {
              jsInteropAnnotation = JS_PROPERTY;
            } else if (method.hasAnnotation(JS_OVERLAY)) {
              jsInteropAnnotation = JS_OVERLAY;
            } else {
              jsInteropAnnotation = JS_METHOD;
            }

            validEntityName(method, jsInteropAnnotation, true);

            if (OBJECT_METHODS_TO_RENAME.contains(method)) {
              String methodName = method.getName();
              method.setName(methodName + "_");

              addAnnotationNameAttributeIfNotEmpty(method, methodName, jsInteropAnnotation, true);
            }

            return method;
          }

          @Override
          public Parameter rewriteParameter(Parameter parameter) {
            String validName = toValidJavaIdentifier(parameter.getName());
            return parameter.toBuilder().setName(validName).build();
          }
        });
  }

  private void validEntityName(
      Entity entity, AnnotationType jsInteropAnnotationType, boolean createAnnotation) {
    String originalName = entity.getName();

    String validName = toValidJavaIdentifier(originalName);

    if (!validName.equals(originalName)) {
      entity.setName(validName);

      addAnnotationNameAttributeIfNotEmpty(
          entity, originalName, jsInteropAnnotationType, createAnnotation);
    }
  }
}
