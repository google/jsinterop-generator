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

import com.google.errorprone.annotations.Immutable;

/** Represents a literal. */
@Immutable
public class LiteralExpression implements Expression {
  public static final LiteralExpression ZERO = new LiteralExpression("0");
  public static final LiteralExpression FALSE = new LiteralExpression("false");
  public static final LiteralExpression NULL = new LiteralExpression("null");

  private final String literal;

  public LiteralExpression(String value) {
    this.literal = value;
  }

  public String getLiteral() {
    return literal;
  }

  @Override
  public Expression doVisit(ModelVisitor visitor) {
    return this;
  }
}
