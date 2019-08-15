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

import static com.google.common.base.Preconditions.checkState;

import jsinterop.generator.helper.ModelHelper;
import jsinterop.generator.model.ArrayTypeReference;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Parameter;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.PredefinedTypeReference;
import jsinterop.generator.model.TypeReference;

/**
 * Creates a JsOverlay method that accepts plain Java array as parameter and delegate to existing
 * native method that accepts {@code JsArrayLike} so the API becomes Java friendly.
 */
public class JsArrayLikeParameterJsOverlayCreator extends AbstractJsOverlayMethodCreator {
  @Override
  protected boolean processMethod(Method method) {
    Method jsOverlayMethod =
        ModelHelper.createDelegatingOverlayMethod(
            method,
            JsArrayLikeParameterJsOverlayCreator::toJavaArray,
            ModelHelper::callUncheckedCast);
    if (jsOverlayMethod != null) {
      method.getEnclosingType().addMethod(jsOverlayMethod);
    }
    return false;
  }

  private static Parameter toJavaArray(int unusedParameterIndex, Parameter originalParameter) {
    if (isJsArrayLikeReference(originalParameter.getType())) {
      TypeReference arrayTypeReference = maybeConvertToArrayType(originalParameter.getType());

      return new Parameter(
          originalParameter.getName(),
          arrayTypeReference,
          originalParameter.isVarargs(),
          originalParameter.isOptional());
    }

    return Parameter.from(originalParameter);
  }

  private static TypeReference maybeConvertToArrayType(TypeReference typeReference) {
    if (isJsArrayLikeReference(typeReference)) {
      ParametrizedTypeReference jsArrayLikeReference = (ParametrizedTypeReference) typeReference;
      checkState(jsArrayLikeReference.getActualTypeArguments().size() == 1);

      return new ArrayTypeReference(
          maybeConvertToArrayType(jsArrayLikeReference.getActualTypeArguments().get(0)));
    }

    return typeReference;
  }

  private static boolean isJsArrayLikeReference(TypeReference typeReference) {
    return typeReference instanceof ParametrizedTypeReference
        && ((ParametrizedTypeReference) typeReference)
            .getMainType()
            .equals(PredefinedTypeReference.JS_ARRAY_LIKE);
  }
}
