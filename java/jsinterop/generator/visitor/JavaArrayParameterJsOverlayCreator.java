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
import static jsinterop.generator.model.PredefinedTypes.BOOLEAN;
import static jsinterop.generator.model.PredefinedTypes.BOOLEAN_OBJECT;
import static jsinterop.generator.model.PredefinedTypes.DOUBLE;
import static jsinterop.generator.model.PredefinedTypes.DOUBLE_OBJECT;
import static jsinterop.generator.model.PredefinedTypes.JS_ARRAY_LIKE;

import jsinterop.generator.helper.ModelHelper;
import jsinterop.generator.model.ArrayTypeReference;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Parameter;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.TypeReference;

/**
 * Creates a JsOverlay method that accepts plain Java array as parameter when a native method
 * accepts either {@code JsArrayLike} or JsArray as parameter so the API becomes Java friendly.
 */
public class JavaArrayParameterJsOverlayCreator extends AbstractJsOverlayMethodCreator {
  @Override
  protected boolean processMethod(Method method) {
    Method jsOverlayMethod =
        ModelHelper.createDelegatingOverlayMethod(
            method,
            JavaArrayParameterJsOverlayCreator::toJavaArray,
            ModelHelper::callUncheckedCast);
    if (jsOverlayMethod != null) {
      method.getEnclosingType().addMethod(jsOverlayMethod);
    }
    return false;
  }

  private static Parameter toJavaArray(int unusedParameterIndex, Parameter originalParameter) {
    if (isJsArrayLikeOrJsArrayReference(originalParameter.getType())) {
      TypeReference arrayTypeReference = maybeConvertToArrayType(originalParameter.getType());

      return originalParameter.toBuilder().setType(arrayTypeReference).build();
    }

    return Parameter.from(originalParameter);
  }

  private static TypeReference maybeConvertToArrayType(TypeReference typeReference) {
    if (isJsArrayLikeOrJsArrayReference(typeReference)) {
      ParametrizedTypeReference jsArrayLikeReference = (ParametrizedTypeReference) typeReference;
      checkState(jsArrayLikeReference.getActualTypeArguments().size() == 1);

      TypeReference elementType = jsArrayLikeReference.getActualTypeArguments().get(0);
      boolean isElementTypeNullable = elementType.isNullable();

      if (elementType.isReferenceTo(DOUBLE_OBJECT) && !isElementTypeNullable) {
        elementType = DOUBLE.getReference(false);
      } else if (elementType.isReferenceTo(BOOLEAN_OBJECT) && !isElementTypeNullable) {
        elementType = BOOLEAN.getReference(false);
      } else {
        elementType = maybeConvertToArrayType(elementType);
      }
      return new ArrayTypeReference(elementType, typeReference.isNullable());
    }

    return typeReference;
  }

  private static boolean isJsArrayLikeOrJsArrayReference(TypeReference typeReference) {
    if (!(typeReference instanceof ParametrizedTypeReference)) {
      return false;
    }
    TypeReference mainType = ((ParametrizedTypeReference) typeReference).getMainType();
    return mainType.isReferenceTo(JS_ARRAY_LIKE)
        || (mainType.getTypeDeclaration() != null
            && "Array".equals(mainType.getTypeDeclaration().getNativeFqn()));
  }
}
