/*
 * Copyright 2017 Google Inc.
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

/** Represents a cast operation. */
public class CastExpression implements Expression {
  private TypeReference type;
  private final Expression expression;

  public CastExpression(TypeReference type, Expression expression) {
    this.type = type;
    this.expression = expression;
  }

  public Expression getExpression() {
    return expression;
  }

  public TypeReference getType() {
    return type;
  }

  @Override
  public Expression doVisit(ModelVisitor visitor) {
    if (visitor.visit(this)) {
      type = visitor.accept(type);
      visitor.accept(expression);
    }
    return this;
  }
}
