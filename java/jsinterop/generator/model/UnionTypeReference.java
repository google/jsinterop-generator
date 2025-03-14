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

import static com.google.common.collect.Lists.transform;

import com.google.common.base.Joiner;
import com.google.j2cl.common.visitor.Processor;
import com.google.j2cl.common.visitor.Visitable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** Models a reference to a union type. */
@Visitable
public class UnionTypeReference extends TypeReference {
  @Visitable List<TypeReference> types;

  public UnionTypeReference(Collection<TypeReference> types) {
    this(types, false);
  }

  public UnionTypeReference(Collection<TypeReference> types, boolean isNullable) {
    super(isNullable);
    setTypes(types);
  }

  @Override
  public String getComment() {
    return Joiner.on(" | ").join(transform(types, TypeReference::getTypeName));
  }

  @Override
  public String getJsDocAnnotationString() {
    return types
        .stream()
        .map(TypeReference::getJsDocAnnotationString)
        .sorted()
        .collect(Collectors.joining("|", "(", ")"));
  }

  @Override
  public String getJavaTypeFqn() {
    return null;
  }

  @Override
  public String getJavaRelativeQualifiedTypeName() {
    return getTypeName();
  }

  @Override
  public String getJniSignature() {
    return null;
  }

  @Override
  public TypeReference toNonNullableTypeReference() {
    return new UnionTypeReference(this.getTypes(), false);
  }

  @Override
  public TypeReference toNullableTypeReference() {
    return new UnionTypeReference(this.getTypes(), true);
  }

  @Override
  public String getTypeName() {
    return null;
  }

  @Override
  public String getImport() {
    throw new UnsupportedOperationException();
  }

  public List<TypeReference> getTypes() {
    return types;
  }

  private void setTypes(Collection<TypeReference> types) {
    this.types = new ArrayList<>(types);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UnionTypeReference)) {
      return false;
    }

    return Objects.equals(types, ((UnionTypeReference) o).types);
  }

  @Override
  public int hashCode() {
    return Objects.hash(types);
  }

  @Override
  public Expression getDefaultValue() {
    throw new UnsupportedOperationException();
  }

  @Override
  public TypeReference acceptInternal(Processor processor) {
    return Visitor_UnionTypeReference.visit(processor, this);
  }

  @Override
  public String toString() {
    return "(" + Joiner.on(" | ").join(types) + ")";
  }
}
