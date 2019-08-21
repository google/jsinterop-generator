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

import jsinterop.generator.model.AbstractRewriter;
import jsinterop.generator.model.Expression;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.MethodInvocation;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;

/**
 * When we create a synthetic type that contains type parameters, we have to parametrize all
 * references using this type.
 */
public class FixTypeParametersOfReferencesToSyntheticTypes implements ModelVisitor {
  private boolean syntheticTypeChanged;

  boolean isSyntheticTypeChanged() {
    return syntheticTypeChanged;
  }

  @Override
  public void applyTo(Program program) {
    program.accept(
        new AbstractRewriter() {
          @Override
          public boolean shouldProcessMethod(Method method) {
            // We don't retrofit type parameters on static method of synthetic type.
            if (getCurrentType().isSynthetic() && method.isStatic()) {
              // For the time being we only generate two static methods: of() method on UnionType
              // helper
              // object and create method for dictionary types.
              checkState(
                  "of".equals(method.getName()) || "create".equals(method.getName()),
                  "We don't expect a static method named [%s] in a synthetic type",
                  method.getName());
              return false;
            }

            return true;
          }

          @Override
          public boolean shouldProcessExpression(Expression expression) {
            return false;
          }

          @Override
          public boolean shouldProcessMethodInvocation(MethodInvocation node) {
            return true;
          }

          @Override
          public boolean shouldProcessParametrizedTypeReference(ParametrizedTypeReference node) {
            // Avoid visiting the reference to the underlying type of a ParametrizedTypeReference of
            // a synthetic type otherwise we will recreate another ParametrizedTypeReference.
            return getSyntheticType(node.getMainType()) == null;
          }

          @Override
          public ParametrizedTypeReference rewriteParametrizedTypeReference(
              ParametrizedTypeReference typeReference) {
            Type syntheticType = getSyntheticType(typeReference.getMainType());

            // Check if we have to update type parameters because FixTypeParameterOfSyntheticType
            // could have added type parameters to the generic type.
            if (syntheticType != null
                && syntheticType.getTypeParameters().size()
                    != typeReference.getActualTypeArguments().size()) {
              typeReference.setActualTypeArguments(syntheticType.getTypeParameters());
              syntheticTypeChanged |= getCurrentType().isSynthetic();
            }

            return typeReference;
          }

          @Override
          public TypeReference rewriteTypeReference(TypeReference typeReference) {

            Type syntheticType = getSyntheticType(typeReference);
            // if we have a reference to a synthetic type, check if the syntheticType doesn't have
            // any type parameters defined on it. If so, fix the type reference by replacing it with
            // a ParametrizedTypeReference.
            if (syntheticType != null && !syntheticType.getTypeParameters().isEmpty()) {
              syntheticTypeChanged |= getCurrentType().isSynthetic();
              ;
              return new ParametrizedTypeReference(
                  typeReference, syntheticType.getTypeParameters());
            }

            return typeReference;
          }
        });
  }

  /**
   * Returns the synthetic type referred by the type reference if it's a reference to a synthetic
   * type. False otherwise.
   */
  private static Type getSyntheticType(TypeReference typeReference) {
    Type typeDeclaration = typeReference.getTypeDeclaration();

    if (typeDeclaration != null && typeDeclaration.isSynthetic()) {
      return typeDeclaration;
    }

    return null;
  }
}
