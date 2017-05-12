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
import java.util.ArrayList;
import java.util.List;

/** Represents an invocation to a method. */
public class MethodInvocation implements Expression {
  private final Expression invocationTarget;
  private final String methodName;
  private List<TypeReference> argumentTypes;
  private final List<Expression> arguments;
  private List<TypeReference> localTypeArguments;

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
    this.argumentTypes = ImmutableList.copyOf(argumentTypes);
    this.arguments = ImmutableList.copyOf(arguments);
    this.localTypeArguments = ImmutableList.copyOf(localTypeArguments);
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
  public Expression doVisit(ModelVisitor visitor) {
    if (visitor.visit(this)) {
      // In our model, null means that the method is invoked on the current scope ("this" or current
      // class if static method)
      if (invocationTarget != null) {
        visitor.accept(invocationTarget);
      }
      argumentTypes = ImmutableList.copyOf(visitor.accept(argumentTypes));
      visitor.accept(arguments);

      localTypeArguments = ImmutableList.copyOf(visitor.accept(localTypeArguments));
    }

    return this;
  }
}
