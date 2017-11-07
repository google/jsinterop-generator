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

import static jsinterop.generator.model.AnnotationType.JS_FUNCTION;
import static jsinterop.generator.model.AnnotationType.JS_OVERLAY;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jsinterop.generator.model.ArrayTypeReference;
import jsinterop.generator.model.Field;
import jsinterop.generator.model.JavaTypeReference;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Method.Parameter;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;
import jsinterop.generator.model.WildcardTypeReference;

/**
 * Replaces generic JsFunction type references to bounded wildcard type references in method's
 * parameters where it's possible in order to improve API quality.
 *
 * <p>We follow the PECS principle standing for producer-extends, consumer-super for defining
 * bounds; if a parameterized type represents a T producer, we use {@code <? extends T>}; if it
 * represents a T consumer, we use {@code <? super T>} If it represents both or if we cannot
 * determine if it's a producer or consumer we use {@code <T>}.
 *
 * <pre>E.g.
 *  <code>
 *    @JsFunction
 *    interface FooCallback<T,V> {
 *      V onInvoke(T param);
 *    }
 *  </code>
 *
 *  The reference to this interface can use bounded wildcard type as following:
 *  <code>
 *    interface Bar<T, V> {
 *      void foo(FooCallback<? super T, ? extends V> callback);
 *    }
 *  </code>
 * </pre>
 *
 * <p>We only apply this logic on methods parameters and fields. We never create wildcard types as
 * return type of a method. Rather than providing flexibility, that would force the users to use
 * wildcard types in client code.
 */
public class WildcardTypeCreator extends AbstractModelVisitor {
  private final Set<Type> unionTypeHelperTypes;

  public WildcardTypeCreator(Set<Type> unionTypeHelperTypes) {
    this.unionTypeHelperTypes = unionTypeHelperTypes;
  }

  @Override
  public boolean visit(Field field) {
    field.setType(maybeCreateWildcardType(field.getType()));
    return false;
  }

  @Override
  public boolean visit(Method method) {
    for (Parameter parameter : method.getParameters()) {
      parameter.setType(maybeCreateWildcardType(parameter.getType()));
    }
    return false;
  }

  private TypeReference maybeCreateWildcardType(TypeReference typeReference) {
    if (isGenericJsFunctionTypeReference(typeReference)) {
      Type jsFunctionType = getMainType(typeReference);
      Method jsFunctionMethod = getCallbackMethod(jsFunctionType);
      List<TypeReference> methodParameterTypes =
          jsFunctionMethod
              .getParameters()
              .stream()
              .map(Parameter::getType)
              .collect(Collectors.toList());

      List<TypeReference> actualTypeArguments =
          ((ParametrizedTypeReference) typeReference).getActualTypeArguments();
      List<TypeReference> newTypeArguments = new ArrayList<>();

      for (TypeReference typeParameter : jsFunctionType.getTypeParameters()) {

        TypeReferenceCounter finder = new TypeReferenceCounter(typeParameter);

        int refsInReturnType =
            finder.countReferences(ImmutableList.of(jsFunctionMethod.getReturnType()));
        int refsInParameter = finder.countReferences(methodParameterTypes);

        TypeReference currentTypeArgument = actualTypeArguments.get(newTypeArguments.size());

        if (refsInReturnType > 0
            && refsInParameter == 0
            && isDirectReference(typeParameter, jsFunctionMethod.getReturnType())) {
          // Type variable is only used as the return type of a callback function. The function
          // can be considered as a producer of this type (i.e. ? extends T).
          newTypeArguments.add(WildcardTypeReference.createWildcardUpperBound(currentTypeArgument));
        } else if (refsInReturnType == 0
            && refsInParameter > 0
            && areAllDirectReferences(typeParameter, methodParameterTypes, refsInParameter)) {
          // Type variable is only used as parameter type of callback function. The function
          // can be considered as a consumer of this type (i.e. ? super T).
          newTypeArguments.add(WildcardTypeReference.createWildcardLowerBound(currentTypeArgument));
        } else {
          // Impossible to know if the function is a consumer or producer of the type so cannot use
          // a wildcard type.
          newTypeArguments.add(currentTypeArgument);
        }
      }

      return new ParametrizedTypeReference(new JavaTypeReference(jsFunctionType), newTypeArguments);
    }

    return typeReference;
  }

  private boolean isGenericJsFunctionTypeReference(TypeReference typeReference) {
    if (!(typeReference instanceof ParametrizedTypeReference)) {
      return false;
    }

    Type mainType = getMainType(typeReference);

    return mainType != null && mainType.hasAnnotation(JS_FUNCTION);
  }

  private boolean areAllDirectReferences(
      TypeReference target, List<TypeReference> references, int parameterReferenceCount) {

    return references.stream().filter((ref) -> isDirectReference(target, ref)).count()
        == parameterReferenceCount;
  }

  private boolean isDirectReference(TypeReference target, TypeReference reference) {
    // The reference is a 'direct reference' if it's not part of a type parameter of a parametrized
    // type reference. We also consider arrays as direct reference since unlike generic types
    // they are covariant and wildcard upgrade will result in complaint types.
    return reference.equals(target)
        || isArrayTypeReference(target, reference)
        || isUnionTypeHelperTypeReference(target, reference);
  }

  private boolean isUnionTypeHelperTypeReference(
      TypeReference target, TypeReference unionTypeHelperReference) {
    if (!(unionTypeHelperReference instanceof ParametrizedTypeReference)) {
      return false;
    }

    Type mainType = getMainType(unionTypeHelperReference);

    if (!unionTypeHelperTypes.contains(mainType)) {
      return false;
    }

    List<TypeReference> actualTypeArguments =
        ((ParametrizedTypeReference) unionTypeHelperReference).getActualTypeArguments();
    return actualTypeArguments.contains(target);
  }

  private boolean isArrayTypeReference(TypeReference target, TypeReference arrayTypeReference) {
    return arrayTypeReference instanceof ArrayTypeReference
        && ((ArrayTypeReference) arrayTypeReference).getArrayType().equals(target);
  }

  private Method getCallbackMethod(Type jsFunctionType) {
    // A JsFunction type should have only one method that is not annotated with JsOverlay annotation
    return jsFunctionType
        .getMethods()
        .stream()
        .filter(m -> !m.hasAnnotation(JS_OVERLAY))
        .findAny()
        .get();
  }

  private Type getMainType(TypeReference typeReference) {
    TypeReference mainType = ((ParametrizedTypeReference) typeReference).getMainType();

    if (mainType instanceof JavaTypeReference) {
      return ((JavaTypeReference) mainType).getJavaType();
    }

    return null;
  }

  private static class TypeReferenceCounter extends AbstractModelVisitor {
    private final TypeReference typeReferenceToFind;
    private int referenceCount;

    TypeReferenceCounter(TypeReference typeReferenceToFind) {
      this.typeReferenceToFind = typeReferenceToFind;
    }

    @Override
    public boolean visit(TypeReference typeReference) {
      if (typeReference.equals(typeReferenceToFind)) {
        referenceCount++;
      }

      return true;
    }

    public int countReferences(List<TypeReference> rootTypeReferences) {
      referenceCount = 0;
      accept(rootTypeReferences);
      return referenceCount;
    }
  }
}
