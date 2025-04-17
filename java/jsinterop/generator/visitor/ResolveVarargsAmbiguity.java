/*
 * Copyright 2025 Google Inc.
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

import static jsinterop.generator.visitor.TypeReferenceUtils.isJsArrayLikeOrJsArrayTypeReference;
import static jsinterop.generator.visitor.TypeReferenceUtils.isJsConstructorFnTypeReference;
import static jsinterop.generator.visitor.TypeReferenceUtils.isJsObjectTypeReference;

import com.google.common.collect.Iterables;
import jsinterop.generator.model.AbstractVisitor;
import jsinterop.generator.model.AnnotationType;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.ModelVisitor;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;
import jsinterop.generator.model.UnionTypeReference;

/**
 * Creates method overloading for empty varargs parameter only when there is an ambiguity. We create
 * a method overload for a varargs parameter with empty parameter when a method is called without
 * any argument specified, the reference to that method is ambiguous. Therefore we create an
 * overload method omitting the varargs when multiple overloads of a method differ only in the
 * varargs parameter's type. ex:
 *
 * <pre>
 * void foo(String param1,  VarargsUnionType... varargs);
 *
 * has already been decomposed to:
 * void foo(String param1,  double... varargs);
 * void foo(String param1,  String... vargars);
 *
 * to avoid ambiguity, we create an overload method without the varargs:
 *
 * void foo(String param1);
 * </pre>
 */
public class ResolveVarargsAmbiguity implements ModelVisitor {

  @Override
  public void applyTo(Program program) {
    program.accept(
        new AbstractVisitor() {
          @Override
          public boolean enterType(Type type) {
            // don't create method overloading on functional interface.
            if (type.hasAnnotation(AnnotationType.JS_FUNCTION)) {
              return false;
            }

            for (Method m : type.getMethods()) {
              if (hasAmbiguousVarargs(m)) {
                m.getEnclosingType().addMethod(createOverloadMethod(m));
              }
            }
            return false;
          }
        });
  }

  private static boolean hasAmbiguousVarargs(Method method) {
    if (!method.hasVarargsParameter()) {
      return false;
    }
    TypeReference varargsParameterType = Iterables.getLast(method.getParameters()).getType();
    return isJsArrayLikeOrJsArrayTypeReference(varargsParameterType)
        || isJsObjectTypeReference(varargsParameterType)
        || varargsParameterType instanceof UnionTypeReference
        || isJsConstructorFnTypeReference(varargsParameterType);
  }

  private static Method createOverloadMethod(Method method) {
    Method overload = Method.from(method);
    overload.setParameters(method.getParameters().subList(0, method.getParameters().size() - 1));
    return overload;
  }
}
