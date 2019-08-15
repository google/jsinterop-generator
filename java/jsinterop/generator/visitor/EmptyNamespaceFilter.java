/*
 * Copyright 2015 Google Inc.
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
package jsinterop.generator.visitor;

import jsinterop.generator.model.AbstractRewriter;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.Type;

/**
 * Deletes Java class from our model that have been generated from a namespace that don't contain
 * any functions nor variables.
 */
public class EmptyNamespaceFilter extends AbstractModelVisitor {
  @Override
  public void applyTo(Program program) {
    program.accept(
        new AbstractRewriter() {
          @Override
          public Type rewriteType(Type node) {
            return isEmptyNamespace(node) ? null : node;
          }
        });
  }

  private static boolean isEmptyNamespace(Type type) {
    return type.isNamespace()
        && type.getFields().isEmpty()
        && type.getMethods().isEmpty()
        && type.getInnerTypes().isEmpty();
  }
}
