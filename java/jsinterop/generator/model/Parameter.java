/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jsinterop.generator.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.j2cl.ast.annotations.Context;
import com.google.j2cl.ast.annotations.Visitable;
import com.google.j2cl.ast.processors.common.Processor;

/** Models parameters of java methods. */
@Visitable
@Context
public class Parameter implements HasName, Node {
  public static Parameter from(Parameter parameter) {
    return new Parameter(
        parameter.getName(), parameter.getType(), parameter.isVarargs(), parameter.isOptional());
  }

  @Visitable TypeReference type;
  private final boolean varargs;
  private final boolean optional;
  private String name;
  private Method enclosingMethod;

  public Parameter(String name, TypeReference type, boolean varargs, boolean optional) {
    this.name = name;
    this.type = type;
    this.varargs = varargs;

    this.optional = optional;
  }

  public TypeReference getType() {
    return type;
  }

  @Override
  public String getName() {
    return name;
  }

  public boolean isVarargs() {
    return varargs;
  }

  public boolean isOptional() {
    return optional;
  }

  public void setType(TypeReference type) {
    this.type = type;
  }

  public String getConfigurationIdentifier() {
    return checkNotNull(getEnclosingMethod()).getConfigurationIdentifier() + "." + getName();
  }

  @Override
  public Node accept(Processor processor) {
    return Visitor_Parameter.visit(processor, this);
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

  public String getJniSignature() {
    TypeReference jniType = isVarargs() ? new ArrayTypeReference(getType()) : getType();
    return jniType.getJniSignature();
  }

  public Method getEnclosingMethod() {
    return enclosingMethod;
  }

  void setEnclosingMethod(Method enclosingMethod) {
    this.enclosingMethod = enclosingMethod;
  }
}
