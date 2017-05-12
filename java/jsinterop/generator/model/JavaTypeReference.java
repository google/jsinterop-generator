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

/** Model a reference to a Java type created from a typescript type. */
public class JavaTypeReference extends AbstractTypeReference {
  private Type javaType;
  public String comment;

  public JavaTypeReference(Type javaType) {

    this.javaType = javaType;
  }

  @Override
  public String getTypeName() {
    return javaType.getName();
  }

  @Override
  public String getImport() {
    return javaType.getJavaFqn();
  }

  @Override
  public String getComment() {
    return comment;
  }

  @Override
  public String getJsDocAnnotationString() {
    return javaType.getNativeFqn();
  }

  @Override
  public String getJavaTypeFqn() {
    return javaType.getJavaFqn();
  }

  @Override
  public String getJavaRelativeQualifiedTypeName() {
    return javaType.getJavaRelativeQualifiedTypeName();
  }

  @Override
  public String getJniSignature() {
    return "L" + javaType.getJavaFqn().replace(".", "/") + ";";
  }

  public Type getJavaType() {
    return javaType;
  }

  public void setJavaType(Type javaType) {
    this.javaType = javaType;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
