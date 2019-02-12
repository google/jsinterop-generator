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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import jsinterop.generator.model.ArrayTypeReference;
import jsinterop.generator.model.DelegableTypeReference;
import jsinterop.generator.model.Field;
import jsinterop.generator.model.JavaTypeReference;
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
 * <p>ex (with typescript): class</p>
 *
 * <pre>
 *   Foo&lt;T&gt; {
 *     foo(callback: (result: T) =&gt; void): void;
 *   }
 * </pre>
 *
 * <p>If generics from the parent scope are used in the definition of the synthetic type, we have to
 * retrofit the generic definition on the java type used to abstract the synthetic type.
 *
 * <p>Generated java code:</p>
 *
 * <pre>
 *   public class Foo&lt;T&gt; {
 *     &#64;JsFunction
 *     interface FooCallback&lt;T&gt; {
 *       void onInvoke(T result);
 *     }
 *
 *     public native void foo(FooCallback&lt;T&gt; callback);
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
    Set<TypeReference> undefinedTypeParameters = new HashSet<>();

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

    javaType.setTypeParameters(sortTypeParameter(undefinedTypeParameters, javaType));
  }

  private static Collection<TypeReference> sortTypeParameter(
      Set<TypeReference> undefinedTypeParameters, Type syntheticType) {
    // Type parameters of a synthetic type refer either to enclosing method's type parameters or the
    // top level type's type parameters.
    // In order to avoid breaking changes if someone rename type parameters, we sort the
    // type parameters based first on the order of type parameter at the method definition and then
    // on the order of type parameters at top level type definition.

    List<TypeReference> sortedUndefinedTypeParameters = new ArrayList<>();

    // First sort the type parameter using the order of the enclosing method's type parameters if
    // they are any
    Method enclosingMethod = findTopEnclosingMethod(syntheticType);

    if (enclosingMethod != null && !enclosingMethod.getTypeParameters().isEmpty()) {
      sortedUndefinedTypeParameters.addAll(
          enclosingMethod
              .getTypeParameters()
              .stream()
              .filter(undefinedTypeParameters::contains)
              .collect(toList()));

      undefinedTypeParameters.removeAll(enclosingMethod.getTypeParameters());
    }

    // The remaining type parameters are coming from top level enclosing type.
    if (!undefinedTypeParameters.isEmpty()) {
      sortedUndefinedTypeParameters.addAll(
          syntheticType
              .getTopLevelParentType()
              .getTypeParameters()
              .stream()
              .filter(undefinedTypeParameters::contains)
              .collect(toList()));

      undefinedTypeParameters.removeAll(sortedUndefinedTypeParameters);
    }

    checkState(
        undefinedTypeParameters.isEmpty(),
        "Undefined type parameter %s for type %s",
        undefinedTypeParameters,
        syntheticType);

    return sortedUndefinedTypeParameters;
  }

  private static Method findTopEnclosingMethod(Type syntheticType) {
    // Type parameters can only be defined on methods of the top level parent.
    // Synthetic types can be enclosed in another synthetic types, we need first to find the top
    // level synthetic type in order to be able to find the method of the top level parent
    // that refer to this type.
    // Ex:
    // /**
    //  * @param {function(V):{foo:T}} foo
    //  * @return {undefined}
    //  * @template T,V
    //  */
    // Bar.prototype.barMethod = function(foo) {};
    //
    // If we are visiting {foo: T}, and in order to find the barMethod where T is defined, we need
    // to search for method that refer to {function(V):{foo:T}} on Bar.

    Type topLevelSyntheticType = syntheticType;
    while (topLevelSyntheticType.getEnclosingType().isSynthetic()) {
      topLevelSyntheticType = topLevelSyntheticType.getEnclosingType();
    }

    for (Method topLevelMethod : syntheticType.getTopLevelParentType().getMethods()) {
      if (isReferenceTo(topLevelMethod.getReturnType(), topLevelSyntheticType)) {
        return topLevelMethod;
      }

      for (Parameter parameter : topLevelMethod.getParameters()) {
        if (isReferenceTo(parameter.getType(), topLevelSyntheticType)) {
          return topLevelMethod;
        }
      }
    }

    return null;
  }

  private static boolean isReferenceTo(TypeReference typeReference, Type type) {
    if (typeReference instanceof DelegableTypeReference) {
      return isReferenceTo(((DelegableTypeReference) typeReference).getDelegate(), type);
    }

    return typeReference instanceof JavaTypeReference
        && type.equals(((JavaTypeReference) typeReference).getJavaType());
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
