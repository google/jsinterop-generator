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

import java.util.HashSet;
import java.util.Set;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.Type;

/**
 * Deletes Java class from our model that have been generated from a namespace that don't contain
 * any functions nor variables.
 */
public class EmptyNamespaceFilter extends AbstractModelVisitor {
  private static final Set<Type> typeToDelete = new HashSet<>();

  @Override
  public boolean visit(Type type) {
    if (type.isNamespace() && isEmpty(type)) {
      typeToDelete.add(type);
    }

    return true;
  }

  @Override
  public void endVisit(Program program) {
    program.removeTypes(typeToDelete);
  }

  private static boolean isEmpty(Type type) {
    return type.getFields().isEmpty()
        && type.getMethods().isEmpty()
        && type.getInnerTypes().isEmpty();
  }
}
