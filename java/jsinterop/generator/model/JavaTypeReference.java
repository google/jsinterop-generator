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

import com.google.j2cl.ast.annotations.Visitable;
import com.google.j2cl.ast.processors.common.Processor;

/** Model a reference to a Java type created from a typescript type. */
@Visitable
public class JavaTypeReference extends AbstractTypeReference {
  private Type typeDeclaration;
  public String comment;

  public JavaTypeReference(Type typeDeclaration) {
    this.typeDeclaration = typeDeclaration;
  }

  @Override
  public String getTypeName() {
    return typeDeclaration.getName();
  }

  @Override
  public String getImport() {
    return typeDeclaration.getJavaFqn();
  }

  @Override
  public String getComment() {
    return comment;
  }

  @Override
  public String getJsDocAnnotationString() {
    return typeDeclaration.getNativeFqn();
  }

  @Override
  public String getJavaTypeFqn() {
    return typeDeclaration.getJavaFqn();
  }

  @Override
  public String getJavaRelativeQualifiedTypeName() {
    return typeDeclaration.getJavaRelativeQualifiedTypeName();
  }

  @Override
  public String getJniSignature() {
    return "L" + typeDeclaration.getJavaFqn().replace('.', '/') + ";";
  }

  @Override
  public Type getTypeDeclaration() {
    return typeDeclaration;
  }

  public void setTypeDeclaration(Type typeDeclaration) {
    this.typeDeclaration = typeDeclaration;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public boolean isInstanceofAllowed() {
    return !typeDeclaration.isNativeInterface();
  }

  @Override
  public TypeReference accept(Processor processor) {
    return Visitor_JavaTypeReference.visit(processor, this);
  }
}
