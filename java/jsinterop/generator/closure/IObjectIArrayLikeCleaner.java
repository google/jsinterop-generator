/*
 * Copyright 2016 Google Inc.
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
 */
package jsinterop.generator.closure;

import static com.google.common.base.Preconditions.checkState;
import static jsinterop.generator.model.PredefinedTypeReference.DOUBLE_OBJECT;
import static jsinterop.generator.model.PredefinedTypeReference.JS_ARRAY_LIKE;
import static jsinterop.generator.model.PredefinedTypeReference.JS_PROPERTY_MAP;
import static jsinterop.generator.model.PredefinedTypeReference.STRING;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import jsinterop.generator.model.DelegableTypeReference;
import jsinterop.generator.model.JavaTypeReference;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.PredefinedTypeReference;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;
import jsinterop.generator.model.UnionTypeReference;
import jsinterop.generator.visitor.AbstractModelVisitor;

/**
 * Replace references to IObject and IArrayLike types with respectively references to
 * jsinterop.base.JsPropertyMap and jsinterop.base.JsArrayLike
 */
public class IObjectIArrayLikeCleaner extends AbstractModelVisitor {
  private static final String OBJECT = "Object";
  private static final String IOBJECT = "IObject";
  private static final String IARRAY_LIKE = "IArrayLike";

  private static final String IOBJECT_KEY_NAME = "IObject#KEY1";
  private static final String IOBJECT_VALUE_NAME = "IObject#VALUE";

  @Override
  public boolean visit(Type type) {
    String nativeFqn = type.getNativeFqn();
    if (IOBJECT.equals(nativeFqn) || IARRAY_LIKE.equals(nativeFqn)) {
      type.setExtern(true);
      return false;
    } else if ("Array".equals(nativeFqn)) {
      // For some obscure reason, JsCompiler uses its own built-in definition of Array Type. This
      // definition defines two type parameters: the first one is the IObject value name and
      // the second one is the value type : Array<IObject#Value, T>
      // Clean that up by removing the first type parameter.
      Collection<TypeReference> typeParameters = type.getTypeParameters();

      checkState(typeParameters.size() == 2, "Array is not defined with two type parameters");
      TypeReference firstTypeParameter = typeParameters.iterator().next();
      checkState(IOBJECT_VALUE_NAME.equals(firstTypeParameter.getTypeName()));

      typeParameters.remove(firstTypeParameter);

    } else if (OBJECT.equals(nativeFqn)) {
      // JsCompiler uses its own built-in definition of Object type and add the two type parameter
      // of IObject. The resulting generated java type is a parametrized type:
      //    class JsObject<IObject#KEY1, IObject#VALUE> {}
      // Clear the params since we don't want parameterized Object type.
      Collection<TypeReference> typeParameters = type.getTypeParameters();

      checkState(typeParameters.size() == 2, "Object is not defined with two type parameters");
      Iterator<TypeReference> typeParameterIterator = typeParameters.iterator();
      checkState(IOBJECT_KEY_NAME.equals(typeParameterIterator.next().getTypeName()));
      checkState(IOBJECT_VALUE_NAME.equals(typeParameterIterator.next().getTypeName()));

      type.getTypeParameters().clear();
    }

    return true;
  }

  @Override
  public TypeReference endVisit(TypeReference typeReference) {
    String underlyingJsdas = getJsDocAnnotationStringOfUnderlyingType(typeReference);

    if (IOBJECT.equals(underlyingJsdas) && typeReference instanceof ParametrizedTypeReference) {
      ParametrizedTypeReference iObjectTypeReference = (ParametrizedTypeReference) typeReference;
      validateIObjectOrParametrizedObjectReference(iObjectTypeReference, false);
      // JsPropertyMap defines only one type parameter that is the type of the value and considers
      // that keys are string.
      // IObject is defined with two type parameters, the first defining the type of the key,
      // the second the type of the value.
      // We've validated that the type of the key is compatible with String, so we can remove the
      // first type parameter to convert the reference to IObjectLike with reference to
      // JsPropertyMap.
      return createJsPropertyMapRef(iObjectTypeReference.getActualTypeArguments().get(1));

    } else if (OBJECT.equals(underlyingJsdas)
        && typeReference instanceof ParametrizedTypeReference) {
      ParametrizedTypeReference objectTypeReference = (ParametrizedTypeReference) typeReference;
      // Even if Object is not defined with a template, Closure allows developers to templatized it
      // to act as a Map or IObject. When it's the case, replace it by JsPropertyMap.
      validateIObjectOrParametrizedObjectReference(objectTypeReference, true);

      return createJsPropertyMapRef(objectTypeReference.getActualTypeArguments().get(1));
    } else if (typeReference instanceof JavaTypeReference && IARRAY_LIKE.equals(underlyingJsdas)) {
      return JS_ARRAY_LIKE;
    }

    return typeReference;
  }

  private static String getJsDocAnnotationStringOfUnderlyingType(TypeReference typeReference) {
    if (typeReference instanceof DelegableTypeReference) {
      return ((DelegableTypeReference) typeReference).getDelegate().getJsDocAnnotationString();
    }

    return typeReference.getJsDocAnnotationString();
  }

  private static TypeReference createJsPropertyMapRef(TypeReference valueType) {
    return new ParametrizedTypeReference(JS_PROPERTY_MAP, ImmutableList.of(valueType));
  }

  /** Check that an IObject reference uses a String or an UnionType of String and number as key. */
  private static void validateIObjectOrParametrizedObjectReference(
      ParametrizedTypeReference typeReference, boolean isObject) {
    String typeName = isObject ? "Object" : "IObject";

    List<TypeReference> actualTypeArguments = typeReference.getActualTypeArguments();

    checkState(
        actualTypeArguments.size() == 2,
        "Wrong number of type parameters for %s type reference",
        typeName);

    TypeReference keyType = actualTypeArguments.get(0);

    if (STRING.getJavaTypeFqn().equals(keyType.getJavaTypeFqn())
        || (isObject
            // Closure allows Object to be parametrized with one type parameter(value).
            // In this case, the key is hardcoded to Object
            && PredefinedTypeReference.OBJECT.getJavaTypeFqn().equals(keyType.getJavaTypeFqn()))) {
      return;
    }

    checkKeyType(keyType instanceof UnionTypeReference, keyType, typeName);
    checkKeyType(isDoubleAndString(((UnionTypeReference) keyType).getTypes()), keyType, typeName);
  }

  private static boolean isDoubleAndString(List<TypeReference> typesReferences) {
    return typesReferences
            .stream()
            .map(TypeReference::getJavaTypeFqn)
            .filter(
                t -> STRING.getJavaTypeFqn().equals(t) || DOUBLE_OBJECT.getJavaTypeFqn().equals(t))
            .count()
        == 2;
  }

  private static void checkKeyType(boolean condition, TypeReference keyType, String typeName) {
    checkState(condition, "Key type for %s is not supported: %s", typeName, keyType);
  }
}
