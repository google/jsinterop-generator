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

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.cartesianProduct;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toSet;
import static jsinterop.generator.model.AnnotationType.JS_OVERLAY;
import static jsinterop.generator.model.PredefinedTypeReference.OBJECT;
import static jsinterop.generator.model.PredefinedTypeReference.VOID;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.EntityKind;
import jsinterop.generator.model.Expression;
import jsinterop.generator.model.ExpressionStatement;
import jsinterop.generator.model.LiteralExpression;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Method.Parameter;
import jsinterop.generator.model.MethodInvocation;
import jsinterop.generator.model.PredefinedTypeReference;
import jsinterop.generator.model.ReturnStatement;
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
        .map(Method::getName)
        .collect(toSet());
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
      Method overloadingMethod = Method.from(method);
      // clean any JsProperty or JsMethod jsinterop annotations
      overloadingMethod.getAnnotations().clear();

      // change the type of all parameters by the types from the permutation.
      for (int i = 0; i < parameterTypes.size(); i++) {
        overloadingMethod.getParameters().get(i).setType(parameterTypes.get(i));
      }

      if (method.equals(overloadingMethod)) {
        continue;
      }

      if (method.getKind() != EntityKind.CONSTRUCTOR) {
        // Because JsType can be extended, we need to create default JsOverlay method that delegates
        // to the original method in order to force developer to override only the original method.
        boolean needDefaultMethod = currentType.isInterface();
        overloadingMethod.setDefault(needDefaultMethod);
        overloadingMethod.setFinal(!needDefaultMethod);
        overloadingMethod.addAnnotation(Annotation.builder().type(JS_OVERLAY).build());
        Expression delegation = createOriginalMethodInvocation(method, overloadingMethod);
        overloadingMethod.setBody(
            method.getReturnType() == VOID
                ? new ExpressionStatement(delegation)
                : new ReturnStatement(delegation));
      }

      overloadingMethods.add(overloadingMethod);
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
    System.out.println(
        method.getEnclosingType().getName()
            + "."
            + method
            + ": "
            + parentInterfaceMethodsStack.peek());
    return parentInterfaceMethodsStack.peek().contains(method.getName());
  }

  private static Expression createOriginalMethodInvocation(
      Method originalMethod, Method overloadingMethod) {
    List<TypeReference> originalParameterTypes =
        originalMethod
            .getParameters()
            .stream()
            .map(Parameter::getType)
            .collect(Collectors.toList());
    List<TypeReference> overloadingParameterTypes =
        overloadingMethod
            .getParameters()
            .stream()
            .map(Parameter::getType)
            .collect(Collectors.toList());
    List<Expression> arguments = new ArrayList<>();

    for (int i = 0; i < originalParameterTypes.size(); i++) {
      LiteralExpression parameterName =
          new LiteralExpression(overloadingMethod.getParameters().get(i).getName());

      if (originalParameterTypes.get(i).equals(overloadingParameterTypes.get(i))) {
        // pass the parameter
        arguments.add(parameterName);
      } else {
        TypeReference originalTypeParameter = originalParameterTypes.get(i);
        checkState(originalTypeParameter instanceof UnionTypeReference);

        // Use an unchecked cast because we know the cast is safe.
        // will generate: Js.<UnionTypeHelperType>uncheckedCast(parameterName)
        // We need to add the local type argument to ensure to call the original method.
        arguments.add(
            new MethodInvocation(
                new TypeQualifier(PredefinedTypeReference.JS),
                "uncheckedCast",
                ImmutableList.of(OBJECT),
                ImmutableList.of(parameterName),
                ImmutableList.of(originalTypeParameter)));
      }
    }

    return new MethodInvocation(null, originalMethod.getName(), originalParameterTypes, arguments);
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
