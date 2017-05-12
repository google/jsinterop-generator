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

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/** Models a reference to a type with generics. */
public class ParametrizedTypeReference extends AbstractTypeReference
    implements DelegableTypeReference {
  private TypeReference mainType;
  private List<TypeReference> actualTypeArguments;

  public ParametrizedTypeReference(
      TypeReference mainType, Collection<TypeReference> actualTypeArguments) {
    this.mainType = mainType;
    setActualTypeArguments(actualTypeArguments);
  }

  public List<TypeReference> getActualTypeArguments() {
    return actualTypeArguments;
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

  private void setMainType(TypeReference typeReference) {
    this.mainType = typeReference;
  }

  public void setActualTypeArguments(Collection<TypeReference> actualTypeArguments) {
    this.actualTypeArguments = ImmutableList.copyOf(actualTypeArguments);
  }

  @Override
  public TypeReference getDelegate() {
    return mainType;
  }

  @Override
  public TypeReference doVisit(ModelVisitor visitor) {
    if (visitor.visit(this)) {
      setMainType(visitor.accept(mainType));
      setActualTypeArguments(visitor.accept(actualTypeArguments));
    }

    return visitor.endVisit(this);
  }
}
