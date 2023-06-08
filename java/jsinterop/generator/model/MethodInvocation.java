/*
 * Copyright 2016 Google Inc.
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
import com.google.common.collect.Lists;
import com.google.j2cl.common.visitor.Processor;
import com.google.j2cl.common.visitor.Visitable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/** Represents an invocation to a method. */
@Visitable
public class MethodInvocation extends Expression {
  @Visitable @Nullable Expression invocationTarget;
  @Visitable List<TypeReference> argumentTypes;
  @Visitable List<Expression> arguments;
  @Visitable List<TypeReference> localTypeArguments;
  private final String methodName;

  private MethodInvocation(
      Expression invocationTarget,
      String methodName,
      List<TypeReference> argumentTypes,
      List<Expression> arguments,
      List<TypeReference> localTypeArguments) {
    this.invocationTarget = invocationTarget;
    this.methodName = methodName;
    this.argumentTypes = argumentTypes;
    this.arguments = arguments;
    this.localTypeArguments = localTypeArguments;
  }

  public Expression getInvocationTarget() {
    return invocationTarget;
  }

  public String getMethodName() {
    return methodName;
  }

  public List<Expression> getArguments() {
    return arguments;
  }

  public List<TypeReference> getArgumentTypes() {
    return argumentTypes;
  }

  public List<TypeReference> getLocalTypeArguments() {
    return localTypeArguments;
  }

  @Override
  Expression acceptInternal(Processor processor) {
    return Visitor_MethodInvocation.visit(processor, this);
  }

  public static Builder builder() {
    return new Builder();
  }

  /** A Builder for MethodInvocation. */
  public static class Builder {
    private Expression invocationTarget;
    private List<TypeReference> argumentTypes = ImmutableList.of();
    private List<Expression> arguments = ImmutableList.of();
    private List<TypeReference> localTypeArguments = ImmutableList.of();
    private String methodName;

    private Builder() {}

    public Builder setInvocationTarget(Expression invocationTarget) {
      this.invocationTarget = invocationTarget;
      return this;
    }

    public Builder setArgumentTypes(TypeReference... argumentTypes) {
      this.argumentTypes = Lists.newArrayList(argumentTypes);
      return this;
    }

    public Builder setArgumentTypes(List<TypeReference> argumentTypes) {
      this.argumentTypes = new ArrayList<>(argumentTypes);
      return this;
    }

    public Builder setArguments(Expression... arguments) {
      this.arguments = Lists.newArrayList(arguments);
      return this;
    }

    public Builder setArguments(List<Expression> arguments) {
      this.arguments = new ArrayList<>(arguments);
      return this;
    }

    public Builder setLocalTypeArguments(TypeReference... localTypeArguments) {
      this.localTypeArguments = Lists.newArrayList(localTypeArguments);
      return this;
    }

    public Builder setLocalTypeArguments(List<TypeReference> localTypeArguments) {
      this.localTypeArguments = new ArrayList<>(localTypeArguments);
      return this;
    }

    public Builder setMethodName(String methodName) {
      this.methodName = methodName;
      return this;
    }

    public MethodInvocation build() {
      return new MethodInvocation(
          invocationTarget, methodName, argumentTypes, arguments, localTypeArguments);
    }
  }
}
