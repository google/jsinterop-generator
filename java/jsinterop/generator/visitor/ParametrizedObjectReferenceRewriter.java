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
import static jsinterop.generator.model.PredefinedTypeReference.DOUBLE_OBJECT;
import static jsinterop.generator.model.PredefinedTypeReference.JS_PROPERTY_MAP;
import static jsinterop.generator.model.PredefinedTypeReference.OBJECT;
import static jsinterop.generator.model.PredefinedTypeReference.STRING;

import java.util.List;
import jsinterop.generator.model.AbstractRewriter;
import jsinterop.generator.model.ModelVisitor;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.PredefinedTypeReference;
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
            if (isJsPropertyMapReference(mainTypeReference)
                || isObjectTypeReference(mainTypeReference)) {
              validateIObjectOrParametrizedObjectReference(parametrizedTypeReference);

              return new ParametrizedTypeReference(
                  JS_PROPERTY_MAP,
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
          STRING.getJavaTypeFqn().equals(keyType.getJavaTypeFqn())
              || (isObjectTypeReference(typeReference.getMainType())
                  // Closure allows Object to be parametrized with one type parameter(value).
                  // In this case, the key is hardcoded to Object
                  && PredefinedTypeReference.OBJECT
                      .getJavaTypeFqn()
                      .equals(keyType.getJavaTypeFqn())),
          keyType,
          typeName);
    }
  }

  private static boolean isJsPropertyMapReference(TypeReference reference) {
    return JS_PROPERTY_MAP.equals(reference);
  }

  private static boolean isObjectTypeReference(TypeReference reference) {
    return "Object".equals(reference.getJsDocAnnotationString());
  }

  private static boolean isObjectOrDoubleOrString(List<TypeReference> typesReferences) {
    return typesReferences.stream()
            .map(TypeReference::getJavaTypeFqn)
            .filter(
                t ->
                    STRING.getJavaTypeFqn().equals(t)
                        || DOUBLE_OBJECT.getJavaTypeFqn().equals(t)
                        || OBJECT.getJavaTypeFqn().equals(t))
            .count()
        == typesReferences.size();
  }

  private static void checkKeyType(boolean condition, TypeReference keyType, String typeName) {
    checkState(condition, "Key type for %s is not supported: %s", typeName, keyType);
  }
}
