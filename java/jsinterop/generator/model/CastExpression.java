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

import com.google.j2cl.common.visitor.Processor;
import com.google.j2cl.common.visitor.Visitable;

/** Represents a cast operation. */
@Visitable
public class CastExpression implements Expression {
  @Visitable TypeReference type;
  @Visitable Expression expression;

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
  public Expression accept(Processor processor) {
    return Visitor_CastExpression.visit(processor, this);
  }
}
