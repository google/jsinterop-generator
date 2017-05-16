/*
 * Copyright 2015 Google Inc.
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
import static java.util.stream.Collectors.toList;

import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import jsinterop.generator.model.ArrayTypeReference;
import jsinterop.generator.model.Field;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Method.Parameter;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;
import jsinterop.generator.model.TypeVariableReference;
import jsinterop.generator.model.UnionTypeReference;

/**
 * Synthetic type can refer to generics from parent scope:
 *
 * <pre>
 * ex (with typescript): class
 *   Foo<T> {
 *     foo(callback: (result: T) => void): void;
 *   }
 * </pre>
 *
 * <p>If generics from the parent scope are used in the definition of the synthetic type, we have to
 * retrofit the generic definition on the java type used to abstract the synthetic type.
 *
 * <pre>
 * Generated java code:
 *   public class Foo<T> {
 *     @JsFunction
 *     interface FooCallback<T> {
 *       void onInvoke(T result);
 *     }
 *
 *     public native void foo(FooCallback<T> callback);
 *   }
 * </pre>
 */
public class FixTypeParametersOfSyntheticTypes extends AbstractModelVisitor {

  @Override
  public boolean visit(Type type) {
    if (type.isSynthetic()) {
      retrofitUndefinedTypeParameters(type);
    }
    return true;
  }

  private static void retrofitUndefinedTypeParameters(Type javaType) {
    Set<TypeVariableReference> undefinedTypeParameters = new HashSet<>();

    // For each method, collect all generics (used in the return type or in the parameters)
    // that are not defined at the method level
    for (Method method : javaType.getMethods()) {
      Set<TypeReference> definedTypeParameters = new HashSet<>(method.getTypeParameters());

      method
          .getParameters()
          .stream()
          .map(Parameter::getType)
          .flatMap(FixTypeParametersOfSyntheticTypes::extractTypeParameter)
          .filter(t -> isUndefinedTypeParameter(t, definedTypeParameters))
          .forEach(undefinedTypeParameters::add);

      extractTypeParameter(method.getReturnType())
          .filter(t -> isUndefinedTypeParameter(t, definedTypeParameters))
          .forEach(undefinedTypeParameters::add);
    }

    // collect all generics used as type of a field
    javaType
        .getFields()
        .stream()
        .map(Field::getType)
        .flatMap(FixTypeParametersOfSyntheticTypes::extractTypeParameter)
        .forEach(undefinedTypeParameters::add);

    // with typescript a literal could contain an index signature and the corresponding java type
    // will automatically extends JsArray. Collect generics that are used in an index signature and
    // so present in the heritage clause.
    javaType
        .getInheritedTypes()
        .stream()
        .filter(t -> t instanceof ParametrizedTypeReference)
        .flatMap(FixTypeParametersOfSyntheticTypes::extractTypeParameter)
        .forEach(undefinedTypeParameters::add);

    javaType.getTypeParameters().addAll(sortTypeParameter(undefinedTypeParameters, javaType));
  }

  private static Collection<? extends TypeReference> sortTypeParameter(
      Set<TypeVariableReference> undefinedTypeParameters, Type syntheticType) {
    // Sort the type parameter using the order of the parent's type parameters at type definition.
    // Type parameters coming from local type parameter of a method will be sorted alphabetically
    // and placed at the end.
    List<TypeReference> sortedUndefinedTypeParameters =
        syntheticType
            .getParent()
            .getTypeParameters()
            .stream()
            .filter(undefinedTypeParameters::contains)
            .collect(toList());
    // remove the already sorted type parameters
    undefinedTypeParameters.removeAll(sortedUndefinedTypeParameters);
    // add the rest by sorting them alphabetically
    // TODO(b/34858931): order should be based on the order at method definition.
    sortedUndefinedTypeParameters.addAll(
        undefinedTypeParameters
            .stream()
            .sorted(Ordering.natural().onResultOf(TypeReference::getTypeName))
            .collect(toList()));
    return sortedUndefinedTypeParameters;
  }

  private static Stream<TypeVariableReference> extractTypeParameter(TypeReference typeReference) {
    // UnionType have been processed before this visitor runs and don't exist anymore.
    checkState(!(typeReference instanceof UnionTypeReference));

    if (typeReference instanceof TypeVariableReference) {
      return Stream.of((TypeVariableReference) typeReference);
    }
    if (typeReference instanceof ParametrizedTypeReference) {
      return ((ParametrizedTypeReference) typeReference)
          .getActualTypeArguments()
          .stream()
          .flatMap(FixTypeParametersOfSyntheticTypes::extractTypeParameter);
    }

    if (typeReference instanceof ArrayTypeReference) {
      return extractTypeParameter(((ArrayTypeReference) typeReference).getArrayType());
    }

    return Stream.empty();
  }

  private static boolean isUndefinedTypeParameter(
      TypeReference typeReference, Set<TypeReference> definedTypeParameters) {
    return typeReference instanceof TypeVariableReference
        && !definedTypeParameters.contains(typeReference);
  }
}
