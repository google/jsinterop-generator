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

import static jsinterop.generator.model.PredefinedTypes.JS_ARRAY_LIKE;
import static jsinterop.generator.model.PredefinedTypes.JS_CONSTRUCTOR_FN;

import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.TypeReference;

/** Util class for {@link TypeReference}. */
final class TypeReferenceUtils {

  private TypeReferenceUtils() {}

  static boolean isJsArrayLikeOrJsArrayTypeReference(TypeReference typeReference) {
    if (!(typeReference instanceof ParametrizedTypeReference)) {
      return false;
    }
    return isJsArrayLikeTypeReference(typeReference) || isJsArrayTypeReference(typeReference);
  }

  static boolean isJsArrayLikeTypeReference(TypeReference typeReference) {
    return typeReference.isReferenceTo(JS_ARRAY_LIKE);
  }

  static boolean isJsArrayTypeReference(TypeReference typeReference) {
    return typeReference.getTypeDeclaration() != null
        && typeReference.getTypeDeclaration().getNativeFqn().equals("Array");
  }

  static boolean isJsObjectTypeReference(TypeReference typeReference) {
    return typeReference.getJsDocAnnotationString().equals("Object");
  }

  static boolean isJsConstructorFnTypeReference(TypeReference typeReference) {
    return typeReference.isReferenceTo(JS_CONSTRUCTOR_FN);
  }
}
