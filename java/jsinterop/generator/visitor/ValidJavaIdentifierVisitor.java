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

import static com.google.common.collect.Lists.newArrayList;
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
import com.google.common.collect.Lists;
import java.util.List;
import jsinterop.generator.model.AnnotationType;
import jsinterop.generator.model.Entity;
import jsinterop.generator.model.Field;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Method.Parameter;
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
          .addAll(methods("getClass", Lists.<Method.Parameter>newArrayList()))
          .addAll(methods("hashCode", Lists.<Method.Parameter>newArrayList()))
          .addAll(methods("equals", newArrayList(new Method.Parameter("o", OBJECT, false, false))))
          .addAll(methods("toString", Lists.<Method.Parameter>newArrayList()))
          .addAll(methods("clone", Lists.<Method.Parameter>newArrayList()))
          .addAll(methods("notify", Lists.<Method.Parameter>newArrayList()))
          .addAll(methods("notifyAll", Lists.<Method.Parameter>newArrayList()))
          .addAll(methods("wait", Lists.<Method.Parameter>newArrayList()))
          .addAll(
              methods("wait", newArrayList(new Method.Parameter("timeout", LONG, false, false))))
          .addAll(
              methods(
                  "wait",
                  newArrayList(
                      new Method.Parameter("timeout", LONG, false, false),
                      new Method.Parameter("nanos", INT, false, false))))
          .addAll(methods("finalize", Lists.newArrayList()))
          .build();

  private static List<Method> methods(String name, List<Method.Parameter> parameterList) {
    return ImmutableList.of(method(name, parameterList, false), method(name, parameterList, true));
  }

  private static Method method(
      String name, List<Method.Parameter> parameterList, boolean isStatic) {
    Method method = new Method();
    method.setName(name);
    for (Method.Parameter parameter : parameterList) {
      method.addParameter(Parameter.from(parameter));
    }
    method.setStatic(isStatic);

    return method;
  }

  @Override
  public boolean visit(Type type) {
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
    return true;
  }

  @Override
  public boolean visit(Field field) {
    validEntityName(field, JS_PROPERTY, true);
    return true;
  }

  @Override
  public boolean visit(Method method) {
    if (method.getKind() == CONSTRUCTOR) {
      // constructor are implemented as method without name
      return true;
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

    return true;
  }

  @Override
  public boolean visit(Parameter parameter) {
    String validName = toValidJavaIdentifier(parameter.getName());
    parameter.setName(validName);

    return true;
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

  private String maybeEscapeJavaClassName(String className, String nativeFqn) {
    if (TYPES_TO_PREFIX.contains(nativeFqn)) {
      return "Js" + className;
    }

    return className;
  }

  private String toValidJavaIdentifier(String identifier) {
    if (JAVA_RESERVERD_WORDS.contains(identifier)) {
      // find maybe another way to escape reserved words
      return identifier + "_";
    }
    return identifier;
  }
}
