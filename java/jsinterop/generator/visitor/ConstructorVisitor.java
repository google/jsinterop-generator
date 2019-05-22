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
import jsinterop.generator.model.AccessModifier;
import jsinterop.generator.model.Expression;
import jsinterop.generator.model.ExpressionStatement;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Method.Parameter;
import jsinterop.generator.model.MethodInvocation;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;

/**
 * Ensure that if the parent class doesn't have an available empty constructor, each constructors of
 * the child class implements a fake call to one constructor of the parent class.
 */
public class ConstructorVisitor extends AbstractModelVisitor {
  @Override
  public boolean visit(Type type) {
    if (type.isInterface() || type.getExtendedTypes().isEmpty()) {
      return true;
    }

    Type superType = getSuperType(type);

    if (superType == null) {
      // not a generated type.
      return true;
    }

    Set<Method> superClassConstructors = new HashSet<>(superType.getConstructors());

    if (hasDefaultConstructor(superClassConstructors)) {
      return true;
    }

    // Make a call to one of the parent class constructor.
    ExpressionStatement superConstructorCall =
        new ExpressionStatement(createSuperMethodInvocation(superType));
    superConstructorCall.setLeadingComment("This call is only here for java compilation purpose.");

    // If no constructor exist on the type, add an empty constructor
    if (type.getConstructors().isEmpty()) {
      Method emptyConstructor = Method.newConstructor();
      emptyConstructor.setBody(superConstructorCall);
      type.addConstructor(emptyConstructor);
    } else {
      // Ensure that all constructor call correctly a super constructor.
      for (Method constructor : type.getConstructors()) {
        constructor.setBody(superConstructorCall);
      }
    }

    return true;
  }

  private static MethodInvocation createSuperMethodInvocation(Type type) {
    checkState(
        !type.getConstructors().isEmpty(), "Super type should have at least one constructor");

    Method constructor = type.getConstructors().get(0);

    List<TypeReference> parametersType =
        constructor.getParameters().stream().map(Parameter::getType).collect(toList());
    List<Expression> parametersValues =
        parametersType.stream().map(TypeReference::getDefaultValue).collect(toList());

    return new MethodInvocation(null, "super", parametersType, parametersValues);
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

  private static Type getSuperType(Type type) {
    TypeReference superTypeReference = type.getExtendedTypes().iterator().next();
    return superTypeReference.getTypeDeclaration();
  }
}
