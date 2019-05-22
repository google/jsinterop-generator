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
import static jsinterop.generator.model.PredefinedTypeReference.JS_PROPERTY_MAP;
import static jsinterop.generator.model.PredefinedTypeReference.STRING;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import jsinterop.generator.model.ArrayTypeReference;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Method.Parameter;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.PredefinedTypeReference;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;
import jsinterop.generator.model.UnionTypeReference;
import jsinterop.generator.visitor.AbstractModelVisitor;

/**
 * Do some cleaning tasks around built-in closure types:
 *
 * <ul>
 *   <li>Removes the extra type parameter from Array and Object type definitions
 *   <li>Removes the extra type parameter from JsPropertyMap references. JsPropertyMap is the Java
 *       abstraction in JsInterop-base for IObject. IObject defines two templates (representing the
 *       type for the keys and the type for the values) but the first one can only be number or
 *       string and is abstracted away in the JsPropertyMap.
 *   <li>Replaces references to Object that are parameterized by references to JsPropertyMap.
 * </ul>
 */
public class BuiltInClosureTypeCleaner extends AbstractModelVisitor {
  private static final String OBJECT = "Object";
  private static final String IOBJECT_KEY_NAME = "IObject#KEY1";
  private static final String IOBJECT_VALUE_NAME = "IObject#VALUE";

  @Override
  public boolean visit(Type type) {
    String nativeFqn = type.getNativeFqn();
    if ("Array".equals(nativeFqn)) {
      cleanArrayType(type);

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

  private static void cleanArrayType(Type arrayType) {
    // 1. For some obscure reason, JsCompiler uses its own built-in definition of Array Type. This
    // definition defines two type parameters: the first one is the IObject value name and
    // the second one is the value type : Array<IObject#Value, T>
    // Clean that up by removing the first type parameter.
    Collection<TypeReference> typeParameters = arrayType.getTypeParameters();

    checkState(typeParameters.size() == 2, "Unexpected array definitions from JsCompiler");
    Iterator<TypeReference> typeParameterIterator = typeParameters.iterator();
    TypeReference firstTypeParameter = typeParameterIterator.next();
    TypeReference arrayValueTypeParameter = typeParameterIterator.next();
    checkState(IOBJECT_VALUE_NAME.equals(firstTypeParameter.getTypeName()));
    typeParameters.remove(firstTypeParameter);

    // 2. Improve the typing of the Array constructor. Array constructor should be parameterized by
    // T (not Object).
    checkState(arrayType.getConstructors().size() == 1);
    Method arrayConstructor = arrayType.getConstructors().get(0);
    improveArrayMethodTyping(arrayConstructor, arrayValueTypeParameter);

    // 3. Improve the typing of Array.unshift. It must accept items of type T (not Object).
    Optional<Method> unshiftMethod =
        arrayType.getMethods().stream().filter(m -> "unshift".equals(m.getName())).findAny();
    checkState(unshiftMethod.isPresent());
    improveArrayMethodTyping(unshiftMethod.get(), arrayValueTypeParameter);

    // 4. Improve the typing of Array.concat. It must accept items of type T (not Object) and
    // return an array of type T[] (not Object[]).
    Optional<Method> concatMethodOptional =
        arrayType.getMethods().stream().filter(m -> "concat".equals(m.getName())).findAny();
    checkState(concatMethodOptional.isPresent());
    Method concatMethod = concatMethodOptional.get();

    improveArrayMethodTyping(concatMethod, arrayValueTypeParameter);

    checkState(
        concatMethod.getReturnType() instanceof ArrayTypeReference
            && PredefinedTypeReference.OBJECT.equals(
                ((ArrayTypeReference) concatMethod.getReturnType()).getArrayType()));
    concatMethod.setReturnType(new ArrayTypeReference(arrayValueTypeParameter));
  }

  /**
   * Improves the typing of Array methods that should accept items of type T (the type parameter of
   * the Array) instead of Object.
   */
  private static void improveArrayMethodTyping(Method m, TypeReference arrayTypeParameter) {
    checkState("Array".equals(m.getEnclosingType().getNativeFqn()));
    checkState(m.getParameters().size() == 1);
    Parameter firstParameter = m.getParameters().get(0);
    checkState(PredefinedTypeReference.OBJECT.equals(firstParameter.getType()));
    firstParameter.setName("items");
    firstParameter.setType(arrayTypeParameter);
  }

  @Override
  public TypeReference endVisit(TypeReference typeReference) {
    if (!(typeReference instanceof ParametrizedTypeReference)) {
      return typeReference;
    }

    ParametrizedTypeReference parametrizedTypeReference = (ParametrizedTypeReference) typeReference;
    TypeReference mainTypeReference = parametrizedTypeReference.getMainType();

    // Fixup the parameterization for references to Object and IObject which are abstracted in
    // Java as JsProperty maps. The conversion removes the first type parameter which is
    // implicit in JsPropertyMap.
    if (isJsPropertyMapReference(mainTypeReference) || isObjectTypeReference(mainTypeReference)) {
      validateIObjectOrParametrizedObjectReference(parametrizedTypeReference);

      return new ParametrizedTypeReference(
          JS_PROPERTY_MAP, parametrizedTypeReference.getActualTypeArguments().subList(1, 2));
    }

    return typeReference;
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

    if (STRING.getJavaTypeFqn().equals(keyType.getJavaTypeFqn())
        || (isObjectTypeReference(typeReference.getMainType())
            // Closure allows Object to be parametrized with one type parameter(value).
            // In this case, the key is hardcoded to Object
            && PredefinedTypeReference.OBJECT.getJavaTypeFqn().equals(keyType.getJavaTypeFqn()))) {
      return;
    }

    checkKeyType(keyType instanceof UnionTypeReference, keyType, typeName);
    checkKeyType(isDoubleAndString(((UnionTypeReference) keyType).getTypes()), keyType, typeName);
  }

  private static boolean isObjectTypeReference(TypeReference reference) {
    return OBJECT.equals(reference.getJsDocAnnotationString());
  }

  private static boolean isJsPropertyMapReference(TypeReference reference) {
    return JS_PROPERTY_MAP.equals(reference);
  }

  private static boolean isDoubleAndString(List<TypeReference> typesReferences) {
    return typesReferences.stream()
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
