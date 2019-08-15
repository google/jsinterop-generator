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

import com.google.j2cl.ast.annotations.Visitable;
import com.google.j2cl.ast.processors.common.Processor;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/** Represents an invocation to a method. */
@Visitable
public class MethodInvocation implements Expression {
  @Visitable @Nullable Expression invocationTarget;
  @Visitable List<TypeReference> argumentTypes;
  @Visitable List<Expression> arguments;
  @Visitable List<TypeReference> localTypeArguments;
  private final String methodName;

  public MethodInvocation(
      Expression invocationTarget,
      String methodName,
      List<TypeReference> argumentTypes,
      List<Expression> arguments) {
    this(invocationTarget, methodName, argumentTypes, arguments, new ArrayList<>());
  }

  public MethodInvocation(
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
  public Expression accept(Processor processor) {
    return Visitor_MethodInvocation.visit(processor, this);
  }
}
