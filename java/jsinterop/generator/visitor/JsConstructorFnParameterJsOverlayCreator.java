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

import static com.google.common.base.Preconditions.checkArgument;
import static jsinterop.generator.model.EntityKind.CONSTRUCTOR;
import static jsinterop.generator.model.PredefinedTypes.CLASS;
import static jsinterop.generator.model.PredefinedTypes.JS;
import static jsinterop.generator.model.PredefinedTypes.JS_CONSTRUCTOR_FN;

import java.util.function.Predicate;
import jsinterop.generator.helper.ModelHelper;
import jsinterop.generator.model.Expression;
import jsinterop.generator.model.LiteralExpression;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.MethodInvocation;
import jsinterop.generator.model.Parameter;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.TypeQualifier;
import jsinterop.generator.model.TypeReference;

/**
 * Creates a JsOverlay method that accepts {@code java.lang.Class} as parameter and delegate to
 * existing native method that accepts {@code JsConstructorFn} so the API becomes Java friendly.
 *
 * <pre>
 *   public native void consumeConstructor(JsConstructorFn<Foo> ctor);
 *   @JsOverlay
 *   public final void consumeConstructor(Class<Foo> clazz) {
 *     return consumeConstructor(Js.asConstructorFn(clazz));
 *   }
 * </pre>
 */
public class JsConstructorFnParameterJsOverlayCreator extends AbstractJsOverlayMethodCreator {
  @Override
  protected boolean processMethod(Method method) {
    if (method.getKind() == CONSTRUCTOR) {
      return false;
    }

    Method overlayMethod =
        ModelHelper.createDelegatingOverlayMethod(
            method,
            JsConstructorFnParameterJsOverlayCreator::toJavaLangClass,
            JsConstructorFnParameterJsOverlayCreator::callAsJsConstructorFn);

    if (overlayMethod != null) {
      method.getEnclosingType().addMethod(overlayMethod);
    }

    return false;
  }

  private static Parameter toJavaLangClass(int unusedParameterIndex, Parameter originalParameter) {
    if (isDirectJsConstructorReference(originalParameter.getType())) {
      return originalParameter.toBuilder()
          .setType(
              new ParametrizedTypeReference(
                  CLASS.getReference(),
                  ((ParametrizedTypeReference) originalParameter.getType())
                      .getActualTypeArguments()))
          .build();
    }

    return Parameter.from(originalParameter);
  }

  private static Expression callAsJsConstructorFn(
      Parameter originalParameter, Parameter overloadParameter) {
    checkArgument(isDirectJsConstructorReference(originalParameter.getType()));
    checkArgument(
        isParametrizedReferenceTo(overloadParameter.getType(), (t) -> t.isReferenceTo(CLASS)));

    // will generate: Js.asConstructorFn(parameter)
    // We need to add the local type argument to ensure to call the original method.
    return MethodInvocation.builder()
        .setInvocationTarget(new TypeQualifier(JS.getReference()))
        .setMethodName("asConstructorFn")
        .setArgumentTypes(overloadParameter.getType())
        .setArguments(new LiteralExpression(overloadParameter.getName()))
        .build();
  }

  private static boolean isDirectJsConstructorReference(TypeReference typeReference) {
    // JsConstructorFn is a generic type and all references to this type is done
    // with a ParametrizedTypeReference
    return isParametrizedReferenceTo(typeReference, (t) -> t.isReferenceTo(JS_CONSTRUCTOR_FN));
  }

  private static boolean isParametrizedReferenceTo(
      TypeReference typeReference, Predicate<TypeReference> referencePredicate) {
    return typeReference instanceof ParametrizedTypeReference
        && referencePredicate.test(((ParametrizedTypeReference) typeReference).getMainType());
  }
}
