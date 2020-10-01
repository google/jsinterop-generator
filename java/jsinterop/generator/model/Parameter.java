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

import com.google.j2cl.common.visitor.Context;
import com.google.j2cl.common.visitor.Processor;
import com.google.j2cl.common.visitor.Visitable;

/** Models parameters of java methods. */
@Visitable
@Context
public class Parameter implements HasName, Node {
  public static Parameter from(Parameter parameter) {
    return parameter.toBuilder().build();
  }

  // TODO(b/156549743): For varargs parameters the type does not reflect the real type of the
  // variable which is always an array.
  @Visitable TypeReference type;
  private final boolean varargs;
  private final boolean optional;
  private final String name;

  private Parameter(String name, TypeReference type, boolean varargs, boolean optional) {
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
    return getName();
  }

  @Override
  public Node accept(Processor processor) {
    return Visitor_Parameter.visit(processor, this);
  }

  @Override
  public String toString() {
    return name;
  }

  public String getJniSignature() {
    TypeReference jniType = isVarargs() ? new ArrayTypeReference(getType()) : getType();
    return jniType.getJniSignature();
  }

  public Builder toBuilder() {
    return new Builder().setName(name).setType(type).setVarargs(varargs).setOptional(optional);
  }

  public static Builder builder() {
    return new Builder();
  }

  /** A Builder for Parameter. */
  public static class Builder {
    private TypeReference type;
    private boolean varargs;
    private boolean optional;
    private String name;

    private Builder() {}

    public Builder setType(TypeReference type) {
      this.type = type;
      return this;
    }

    public Builder setVarargs(boolean varargs) {
      this.varargs = varargs;
      return this;
    }

    public Builder setOptional(boolean optional) {
      this.optional = optional;
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Parameter build() {
      return new Parameter(name, type, varargs, optional);
    }
  }
}
