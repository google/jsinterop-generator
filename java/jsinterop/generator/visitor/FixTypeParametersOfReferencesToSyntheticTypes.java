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

import jsinterop.generator.model.Expression;
import jsinterop.generator.model.JavaTypeReference;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.MethodInvocation;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.Statement;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;

/**
 * When we create a synthetic type that contains type parameters, we have to parametrize all
 * references using this type.
 */
public class FixTypeParametersOfReferencesToSyntheticTypes extends AbstractModelVisitor {
  private boolean syntheticTypeChanged;
  private boolean inSyntheticType;

  @Override
  public boolean visit(Type type) {
    inSyntheticType = type.isSynthetic();
    return true;
  }

  @Override
  public void endVisit(Type type) {
    inSyntheticType = type.getEnclosingType() != null && type.getEnclosingType().isSynthetic();
  }

  @Override
  public boolean visit(Statement statement) {
    return true;
  }

  @Override
  public boolean visit(Expression expression) {
    return expression instanceof MethodInvocation;
  }

  @Override
  public boolean visit(Method method) {
    // We don't retrofit type parameters on static method of synthetic type.
    if (inSyntheticType && method.isStatic()) {
      // For the time being we only generate two static methods: of() method on UnionType helper
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
  public boolean visit(TypeReference typeReference) {
    // Avoid visiting the reference to the underlying type of a ParametrizedTypeReference of
    // a synthetic type otherwise we will recreate another ParametrizedTypeReference.
    return !(typeReference instanceof ParametrizedTypeReference
        && getSyntheticType(((ParametrizedTypeReference) typeReference).getMainType()) != null);
  }

  @Override
  public TypeReference endVisit(TypeReference typeReference) {
    if (typeReference instanceof ParametrizedTypeReference) {
      ParametrizedTypeReference parametrizedTypeReference =
          (ParametrizedTypeReference) typeReference;
      Type syntheticType = getSyntheticType(parametrizedTypeReference.getMainType());

      // Check if we have to update type parameters because FixTypeParameterOfSyntheticType could
      // have added type parameters to the generic type.
      if (syntheticType != null
          && syntheticType.getTypeParameters().size()
              != parametrizedTypeReference.getActualTypeArguments().size()) {
        parametrizedTypeReference.setActualTypeArguments(syntheticType.getTypeParameters());
        syntheticTypeChanged |= inSyntheticType;
      }
    } else {
      Type syntheticType = getSyntheticType(typeReference);
      // if we have a reference to a synthetic type, check if the syntheticType doesn't have
      // any type parameters defined on it. If so, fix the type reference by replacing it with a
      // ParametrizedTypeReference.
      if (syntheticType != null && !syntheticType.getTypeParameters().isEmpty()) {
        syntheticTypeChanged |= inSyntheticType;
        return new ParametrizedTypeReference(typeReference, syntheticType.getTypeParameters());
      }
    }

    return typeReference;
  }

  /**
   * Returns the synthetic type referred by the type reference if it's a reference to a synthetic
   * type. False otherwise.
   */
  private static Type getSyntheticType(TypeReference typeReference) {
    if (typeReference instanceof JavaTypeReference) {
      Type javaType = ((JavaTypeReference) typeReference).getJavaType();
      if (javaType.isSynthetic()) {
        return javaType;
      }
    }

    return null;
  }

  public boolean isSyntheticTypeChanged() {
    return syntheticTypeChanged;
  }
}
