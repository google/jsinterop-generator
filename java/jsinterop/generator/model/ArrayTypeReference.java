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

import static jsinterop.generator.model.LiteralExpression.NULL;

import java.util.Objects;

/** Models reference to a type Array. */
public class ArrayTypeReference implements TypeReference, DelegableTypeReference {
  private TypeReference arrayType;

  public ArrayTypeReference(TypeReference arrayType) {
    this.arrayType = arrayType;
  }

  public ArrayTypeReference() {}

  @Override
  public String getImport() {
    return arrayType.getImport();
  }

  @Override
  public String getComment() {
    return null;
  }

  @Override
  public String getJsDocAnnotationString() {
    return arrayType.getJsDocAnnotationString() + "[]";
  }

  @Override
  public String getJavaTypeFqn() {
    return arrayType.getJavaTypeFqn() + "[]";
  }

  @Override
  public String getJavaRelativeQualifiedTypeName() {
    return arrayType.getJavaRelativeQualifiedTypeName();
  }

  @Override
  public String getJniSignature() {
    return "[" + arrayType.getJniSignature();
  }

  @Override
  public String getTypeName() {
    return arrayType.getTypeName();
  }

  public TypeReference getArrayType() {
    return arrayType;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ArrayTypeReference)) {
      return false;
    }

    return Objects.equals(arrayType, ((ArrayTypeReference) o).arrayType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(true, getJsDocAnnotationString());
  }

  @Override
  public TypeReference getDelegate() {
    return arrayType;
  }

  private void setArrayType(TypeReference arrayType) {
    this.arrayType = arrayType;
  }

  @Override
  public Expression getDefaultValue() {
    return NULL;
  }

  @Override
  public TypeReference doVisit(ModelVisitor visitor) {
    if (visitor.visit(this)) {
      setArrayType(visitor.accept(getArrayType()));
    }

    return visitor.endVisit(this);
  }
}
