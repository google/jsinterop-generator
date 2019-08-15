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

/** Represents an expression to a type qualifier. */
@Visitable
public class TypeQualifier implements Expression {
  @Visitable TypeReference type;

  public TypeQualifier(TypeReference type) {
    this.type = type;
  }

  public TypeReference getType() {
    return type;
  }

  @Override
  public Expression accept(Processor processor) {
    return Visitor_TypeQualifier.visit(processor, this);
  }
}
