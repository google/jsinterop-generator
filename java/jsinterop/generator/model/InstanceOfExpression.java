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

/** Represents an instanceof expression. */
public class InstanceOfExpression implements Expression {
  private final Expression leftOperand;
  private TypeReference rightOperand;

  public InstanceOfExpression(Expression leftOperand, TypeReference rightOperand) {
    this.leftOperand = leftOperand;
    this.rightOperand = rightOperand;
  }

  public Expression getLeftOperand() {
    return leftOperand;
  }

  public TypeReference getRightOperand() {
    return rightOperand;
  }

  @Override
  public Expression doVisit(ModelVisitor visitor) {
    if (visitor.visit(this)) {
      visitor.accept(leftOperand);
      rightOperand = visitor.accept(rightOperand);
    }
    return this;
  }
}
