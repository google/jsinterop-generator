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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jsinterop.generator.model.AbstractVisitor;
import jsinterop.generator.model.AccessModifier;
import jsinterop.generator.model.Expression;
import jsinterop.generator.model.ExpressionStatement;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.MethodInvocation;
import jsinterop.generator.model.Parameter;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;
import jsinterop.generator.model.TypeVariableReference;

/**
 * Ensure that if the parent class doesn't have an available empty constructor, each constructors of
 * the child class implements a fake call to one constructor of the parent class.
 */
public class ConstructorVisitor implements ModelVisitor {

  @Override
  public void applyTo(Program program) {
    program.accept(
        new AbstractVisitor() {
          @Override
          public void exitType(Type type) {
            if (type.isInterface() || type.getExtendedTypes().isEmpty()) {
              return;
            }

            TypeReference superTypeReference = type.getSuperClass();
            Type superTypeDeclaration = superTypeReference.getTypeDeclaration();

            if (superTypeDeclaration == null) {
              // Not a generated type. We assume that a default constructor is available.
              return;
            }

            Set<Method> superClassConstructors =
                new HashSet<>(superTypeDeclaration.getConstructors());

            if (hasDefaultConstructor(superClassConstructors)) {
              return;
            }

            // Call the super class constructor (the first that is declared), so that the code
            // compiles.
            // The class is a native JsType so the code will have no effect.
            ExpressionStatement superConstructorCall =
                new ExpressionStatement(createSuperMethodInvocation(superTypeReference));
            superConstructorCall.setLeadingComment(
                "This super call is here only for the code to compile; it is never executed.");

            if (type.getConstructors().isEmpty()) {
              // If there are no constructors in the type, add a default constructor
              Method emptyConstructor = Method.newConstructor();
              emptyConstructor.setBody(superConstructorCall);
              type.addConstructor(emptyConstructor);
            } else {
              // Ensure all constructors call a super constructor with the right parameters.
              for (Method constructor : type.getConstructors()) {
                constructor.setBody(superConstructorCall);
              }
            }
          }
        });
  }

  private static MethodInvocation createSuperMethodInvocation(TypeReference superTypeReference) {
    Type superTypeDeclaration = superTypeReference.getTypeDeclaration();
    checkState(
        !superTypeDeclaration.getConstructors().isEmpty(),
        "Super type should have at least one constructor");

    Method constructor = superTypeDeclaration.getConstructors().get(0);

    List<TypeReference> parameterTypes =
        constructor.getParameters().stream()
            .map(Parameter::getType)
            .map(t -> resolveTypeVariableFromSuperType(t, superTypeReference))
            .collect(toList());
    List<Expression> parameterValues =
        parameterTypes.stream().map(TypeReference::getDefaultValue).collect(toList());

    return MethodInvocation.builder()
        .setMethodName("super")
        .setArgumentTypes(parameterTypes)
        .setArguments(parameterValues)
        .build();
  }

  private static TypeReference resolveTypeVariableFromSuperType(
      TypeReference typeReference, TypeReference superTypeReference) {
    if (!(typeReference instanceof TypeVariableReference)) {
      return typeReference;
    }

    Type superTypeDeclaration = superTypeReference.getTypeDeclaration();

    // typeReference is a reference to a type of a parameter of the super constructor. If it's a
    // type variable reference, the super type must be parametrized.
    checkState(superTypeReference instanceof ParametrizedTypeReference);
    ParametrizedTypeReference parametrizedSuperTypeReference =
        (ParametrizedTypeReference) superTypeReference;
    checkState(
        superTypeDeclaration.getTypeParameters().size()
            == parametrizedSuperTypeReference.getActualTypeArguments().size());

    int index = 0;
    for (TypeReference typeParameter : superTypeDeclaration.getTypeParameters()) {
      if (typeParameter.equals(typeReference)) {
        return parametrizedSuperTypeReference.getActualTypeArguments().get(index);
      }
      index++;
    }

    throw new RuntimeException(
        "Type variable " + typeReference + " not resolved on " + superTypeReference);
  }

  private static boolean hasDefaultConstructor(Set<Method> classConstructors) {
    if (classConstructors.isEmpty()) {
      return true;
    }

    for (Method constructor : classConstructors) {
      if (constructor.getParameters().isEmpty()
          && constructor.getAccessModifier() != AccessModifier.PRIVATE) {
        return true;
      }
    }

    return false;
  }
}
