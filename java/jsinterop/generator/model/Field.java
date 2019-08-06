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

import static com.google.common.base.Preconditions.checkNotNull;
import static jsinterop.generator.model.EntityKind.FIELD;

import java.util.Objects;

/** Models a Java field. */
public class Field extends Entity implements Visitable<Field> {

  public static Field from(Field field) {
    Field clonedField = new Field();

    copyEntityProperties(field, clonedField);

    clonedField.setType(field.getType());
    clonedField.setInitialValue(field.getInitialValue());

    return clonedField;
  }

  public static Field create(String name, TypeReference type, boolean readOnly, boolean isStatic) {
    Field field = new Field();

    field.setName(name);
    field.setNativeReadOnly(readOnly);
    field.setStatic(isStatic);
    field.setType(type);

    return field;
  }

  private TypeReference type;
  private String initialValue;
  private boolean nativeReadOnly;
  private boolean enumConstant;

  public Field() {
    setKind(FIELD);
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }

    Field other = (Field) o;

    if (type == null && other.type == null) {
      return true;
    }

    if (type == null || other.type == null) {
      return false;
    }

    return Objects.equals(type.getJniSignature(), (other).getType().getJniSignature());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getType());
  }

  public TypeReference getType() {
    return type;
  }

  public void setType(TypeReference type) {
    this.type = type;
  }

  public void setInitialValue(String initialValue) {
    this.initialValue = initialValue;
  }

  public String getInitialValue() {
    return initialValue;
  }

  @Override
  public Field doVisit(ModelVisitor visitor) {
    if (visitor.visit(this)) {
      setType(visitor.accept(getType()));
    }
    visitor.endVisit(this);
    return this;
  }

  @Override
  public String toString() {
    return "Field: " + (type != null ? type.getJavaTypeFqn() : "null") + " " + getName();
  }

  public void setNativeReadOnly(boolean nativeReadOnly) {
    this.nativeReadOnly = nativeReadOnly;
  }

  public boolean isNativeReadOnly() {
    return nativeReadOnly;
  }

  public void setEnumConstant(boolean isEnumConstant) {
    this.enumConstant = isEnumConstant;
  }

  public boolean isEnumConstant() {
    return enumConstant;
  }

  public void removeFromParent() {
    getEnclosingType().removeField(this);
  }

  @Override
  public String getConfigurationIdentifier() {
    return checkNotNull(getEnclosingType()).getJavaFqn() + "." + getName();
  }
}
