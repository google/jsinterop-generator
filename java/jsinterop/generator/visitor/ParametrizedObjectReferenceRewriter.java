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

import static com.google.common.base.Preconditions.checkState;
import static jsinterop.generator.model.PredefinedTypes.DOUBLE_OBJECT;
import static jsinterop.generator.model.PredefinedTypes.JS_PROPERTY_MAP;
import static jsinterop.generator.model.PredefinedTypes.OBJECT;
import static jsinterop.generator.model.PredefinedTypes.STRING;

import java.util.List;
import jsinterop.generator.model.AbstractRewriter;
import jsinterop.generator.model.ModelVisitor;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.TypeReference;
import jsinterop.generator.model.UnionTypeReference;

/**
 * Do some cleaning tasks:
 *
 * <ul>
 *   <li>Removes the extra type parameter from JsPropertyMap references. JsPropertyMap is the Java
 *       abstraction in JsInterop-base for IObject. IObject defines two templates (representing the
 *       type for the keys and the type for the values) but the first one can only be number or
 *       string and is abstracted away in the JsPropertyMap.
 *   <li>Replaces references to Object that are parameterized by references to JsPropertyMap.
 * </ul>
 */
public class ParametrizedObjectReferenceRewriter implements ModelVisitor {
  @Override
  public void applyTo(Program program) {
    program.accept(
        new AbstractRewriter() {
          @Override
          public TypeReference rewriteParametrizedTypeReference(
              ParametrizedTypeReference parametrizedTypeReference) {

            TypeReference mainTypeReference = parametrizedTypeReference.getMainType();

            // Fixup the parameterization for references to parametrized Object which are abstracted
            // in Java as JsProperty maps. The conversion removes the first type parameter which is
            // implicit in JsPropertyMap.
            if (mainTypeReference.isReferenceTo(JS_PROPERTY_MAP)
                || isNativeObjectTypeReference(mainTypeReference)) {
              validateIObjectOrParametrizedObjectReference(parametrizedTypeReference);

              return new ParametrizedTypeReference(
                  JS_PROPERTY_MAP.getReference(),
                  parametrizedTypeReference.getActualTypeArguments().subList(1, 2));
            }

            return parametrizedTypeReference;
          }
        });
  }

  /** Check that an IObject reference uses a String or an UnionType of String and number as key. */
  private static void validateIObjectOrParametrizedObjectReference(
      ParametrizedTypeReference typeReference) {
    String typeName = typeReference.getMainType().getTypeName();
    List<TypeReference> actualTypeArguments = typeReference.getActualTypeArguments();

    checkState(
        actualTypeArguments.size() == 2,
        "Wrong number of type parameters for %s type reference",
        typeName);

    TypeReference keyType = actualTypeArguments.get(0);

    if (keyType instanceof UnionTypeReference) {
      checkKeyType(
          isObjectOrDoubleOrString(((UnionTypeReference) keyType).getTypes()), keyType, typeName);
    } else {
      checkKeyType(
          keyType.isReferenceTo(STRING)
              || (isNativeObjectTypeReference(typeReference.getMainType())
                  // Closure allows Object to be parametrized with one type parameter(value).
                  // In this case, the key is hardcoded to Object
                  && keyType.isReferenceTo(OBJECT)),
          keyType,
          typeName);
    }
  }

  private static boolean isNativeObjectTypeReference(TypeReference reference) {
    return "Object".equals(reference.getJsDocAnnotationString());
  }

  private static boolean isObjectOrDoubleOrString(List<TypeReference> typesReferences) {
    return typesReferences.stream()
        .allMatch(
            t ->
                t.isReferenceTo(STRING)
                    || t.isReferenceTo(DOUBLE_OBJECT)
                    || t.isReferenceTo(OBJECT));
  }

  private static void checkKeyType(boolean condition, TypeReference keyType, String typeName) {
    checkState(condition, "Key type for %s is not supported: %s", typeName, keyType);
  }
}
