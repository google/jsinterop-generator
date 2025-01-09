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

package jsinterop.generator.model;

import com.google.common.base.Joiner;
import com.google.j2cl.common.visitor.Context;
import com.google.j2cl.common.visitor.Processor;
import com.google.j2cl.common.visitor.Visitable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/** Models a reference to a type with generics. */
@Visitable
@Context
public class ParametrizedTypeReference extends AbstractTypeReference
    implements DelegableTypeReference {
  @Visitable TypeReference mainType;
  @Visitable List<TypeReference> actualTypeArguments;

  public ParametrizedTypeReference(
      TypeReference mainType, Collection<TypeReference> actualTypeArguments) {
    super(mainType.isNullable());
    this.mainType = mainType;
    setActualTypeArguments(actualTypeArguments);
  }

  public List<TypeReference> getActualTypeArguments() {
    return actualTypeArguments;
  }

  @Override
  public TypeReference toNonNullableTypeReference() {
    return new ParametrizedTypeReference(
        this.mainType.toNonNullableTypeReference(), this.getActualTypeArguments());
  }

  @Override
  public TypeReference toNullableTypeReference() {
    return new ParametrizedTypeReference(
        this.mainType.toNullableTypeReference(), this.getActualTypeArguments());
  }

  @Override
  public String getImport() {
    return mainType.getImport();
  }

  @Override
  public String getTypeName() {
    return mainType.getTypeName();
  }

  @Override
  public String getComment() {
    return mainType.getComment();
  }

  @Override
  public String getJavaTypeFqn() {
    return mainType.getJavaTypeFqn();
  }

  @Override
  public String getJavaRelativeQualifiedTypeName() {
    return mainType.getJavaRelativeQualifiedTypeName();
  }

  @Override
  public String getJniSignature() {
    return mainType.getJniSignature();
  }

  @Override
  public String getJsDocAnnotationString() {
    return mainType.getJsDocAnnotationString()
        + actualTypeArguments
            .stream()
            .map(TypeReference::getJsDocAnnotationString)
            .collect(Collectors.joining(",", "<", ">"));
  }

  public TypeReference getMainType() {
    return mainType;
  }


  public void setActualTypeArguments(Collection<TypeReference> actualTypeArguments) {
    this.actualTypeArguments = new ArrayList<>(actualTypeArguments);
  }

  @Override
  public Type getTypeDeclaration() {
    return mainType.getTypeDeclaration();
  }

  @Override
  public TypeReference getDelegate() {
    return mainType;
  }

  @Override
  public TypeReference acceptInternal(Processor processor) {
    return Visitor_ParametrizedTypeReference.visit(processor, this);
  }

  @Override
  public boolean isInstanceofAllowed() {
    return mainType.isInstanceofAllowed();
  }

  @Override
  public String toString() {
    return getMainType().getTypeName() + "<" + Joiner.on(", ").join(getActualTypeArguments()) + ">";
  }
}
