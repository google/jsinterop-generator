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

import static jsinterop.generator.model.PredefinedTypeReference.OBJECT;

/** Models a reference to a type variable. */
public class TypeVariableReference extends AbstractTypeReference {

  private final String name;
  private TypeReference upperBound;

  public TypeVariableReference(String name, TypeReference upperBound) {
    this.name = name;
    this.upperBound = upperBound != null ? upperBound : OBJECT;
  }

  @Override
  public String getTypeName() {
    return name;
  }

  @Override
  public String getImport() {
    return null;
  }

  @Override
  public String getComment() {
    return null;
  }

  @Override
  public String getJsDocAnnotationString() {
    return name;
  }

  @Override
  public String getJavaTypeFqn() {
    return name;
  }

  @Override
  public String getJavaRelativeQualifiedTypeName() {
    return getTypeName();
  }

  @Override
  public String getJniSignature() {
    return upperBound.getJniSignature();
  }

  public TypeReference getUpperBound() {
    return upperBound;
  }

  public void setUpperBound(TypeReference upperBound) {
    this.upperBound = upperBound;
  }

  @Override
  public String toString() {
    return "TypeVariableReference " + name;
  }

  @Override
  public boolean isInstanceofAllowed() {
    return false;
  }

  @Override
  public TypeReference doVisit(ModelVisitor visitor) {
    if (visitor.visit(this)) {
      setUpperBound(visitor.accept(upperBound));
    }

    return visitor.endVisit(this);
  }
}
