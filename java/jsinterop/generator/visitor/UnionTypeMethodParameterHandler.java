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

import static com.google.common.collect.Lists.cartesianProduct;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jsinterop.generator.helper.ModelHelper;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Parameter;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;
import jsinterop.generator.model.UnionTypeReference;

/**
 * This visitor will handle methods where union types are involved in the parameters by creating
 * methods overloading.
 */
public class UnionTypeMethodParameterHandler extends AbstractJsOverlayMethodCreator {
  @Override
  protected boolean processMethod(Method method) {
    Type currentType = method.getEnclosingType();

    List<Method> overloadingMethods = new ArrayList<>();

    // Create overloading methods if union types are used in method's parameters.
    List<List<TypeReference>> decomposedTypeParameters =
        method
            .getParameters()
            .stream()
            .map(parameter -> decomposeUnionTypes(parameter.getType()))
            .collect(Collectors.toList());

    List<List<TypeReference>> typesReferencesPermutations =
        cartesianProduct(decomposedTypeParameters);

    // Create one new method by permutation
    for (List<TypeReference> parameterTypes : typesReferencesPermutations) {
      Method overloadingMethod =
          ModelHelper.createDelegatingOverlayMethod(
              method,
              (i, p) ->
                  new Parameter(p.getName(), parameterTypes.get(i), p.isVarargs(), p.isOptional()),
              ModelHelper::callUncheckedCast);

      if (overloadingMethod != null) {
        overloadingMethods.add(overloadingMethod);
      }
    }

    // Check if the overloading methods don't collide together. That happens when several generics
    // are involved in the union type, several overloading methods have the same signature after
    // generics erasure. In this case, don't add the methods.
    Set<String> uniqueMethodSignatures =
        overloadingMethods.stream().map(Method::getJniSignatureWithoutReturn).collect(toSet());

    if (uniqueMethodSignatures.size() == overloadingMethods.size()) {
      currentType.addMethods(overloadingMethods);
    }

    return false;
  }

  /**
   * Returns the different TypeReferences that can be built when we decompose an UnionType Ex:
   *
   * <pre>
   *   Foo<(Bar|Zoot), (String|number)> | Baz[] | string
   *
   * should return the following list after union type decomposition:
   *
   *  [
   *    (Foo<(Bar|Zoot), (String|number)> | Baz[] | string),
   *    Foo<(Bar|Zoot), (String|number)>,
   *    Baz[],
   *    string
   *  ]
   * </pre>
   */
  private static List<TypeReference> decomposeUnionTypes(TypeReference typeReference) {
    // UnionType: compute the TypeReference permutations for each types involved in the union type.
    if (typeReference instanceof UnionTypeReference) {
      List<TypeReference> result = new ArrayList<>();
      result.add(typeReference);

      for (TypeReference unionTypeItem : ((UnionTypeReference) typeReference).getTypes()) {
        result.addAll(decomposeUnionTypes(unionTypeItem));
      }

      return result;
    }

    // simple case, no more direct union type involved in the type reference.
    // Union type involved in generics or array type will be replaced later by an helper type.
    return newArrayList(typeReference);
  }
}
