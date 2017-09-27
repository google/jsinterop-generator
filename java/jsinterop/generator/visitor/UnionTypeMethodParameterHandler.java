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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.cartesianProduct;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toSet;
import static jsinterop.generator.model.PredefinedTypeReference.OBJECT;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jsinterop.generator.helper.ModelHelper;
import jsinterop.generator.model.Expression;
import jsinterop.generator.model.LiteralExpression;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Method.Parameter;
import jsinterop.generator.model.MethodInvocation;
import jsinterop.generator.model.PredefinedTypeReference;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeQualifier;
import jsinterop.generator.model.TypeReference;
import jsinterop.generator.model.UnionTypeReference;

/**
 * This visitor will handle methods where union types are involved in the parameters by creating
 * methods overloading.
 */
public class UnionTypeMethodParameterHandler extends AbstractModelVisitor {
  private final Deque<Set<String>> parentInterfaceMethodsStack = new LinkedList<>();

  @Override
  public boolean visit(Type type) {
    parentInterfaceMethodsStack.push(getParentInterfacesMethodsWithUnionTypes(type));
    return true;
  }

  private Set<String> getParentInterfacesMethodsWithUnionTypes(Type type) {
    return getParentInterfaces(type, true)
        .stream()
        .flatMap(t -> t.getMethods().stream())
        .map(UnionTypeMethodParameterHandler::getOverrideKey)
        .collect(toSet());
  }

  private static String getOverrideKey(Method m) {
    // The only way a class in closure or typescript can redefine a method from a parent interface
    // is by adding optional parameters. Creating a key containing the name of the method and the
    // nbr of parameters is enough to check if a method exists on a parent interface.
    return m.getName() + "%" + m.getParameters().size();
  }

  @Override
  public void endVisit(Type type) {
    parentInterfaceMethodsStack.pop();
  }

  @Override
  public boolean visit(Method method) {
    // if the method are defined in a parent interfaces, default overloading methods will be created
    // on that interface. These methods cannot be overridden, so we don't need to do anything here.
    if (isParentInterfaceMethod(method)) {
      return false;
    }

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
              UnionTypeMethodParameterHandler::callUncheckedCast);

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

  private boolean isParentInterfaceMethod(Method method) {
    return parentInterfaceMethodsStack.peek().contains(getOverrideKey(method));
  }

  private static Expression callUncheckedCast(
      Parameter originalParameter, Parameter overloadParameter) {
    checkArgument(originalParameter.getType() instanceof UnionTypeReference);

    // Use an unchecked cast because we know the cast is safe.
    // will generate: Js.<UnionTypeHelperType>uncheckedCast(parameterName)
    // We need to add the local type argument to ensure to call the original method.
    return new MethodInvocation(
        new TypeQualifier(PredefinedTypeReference.JS),
        "uncheckedCast",
        ImmutableList.of(OBJECT),
        ImmutableList.of(new LiteralExpression(overloadParameter.getName())),
        ImmutableList.of(originalParameter.getType()));
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
