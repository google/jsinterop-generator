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
package jsinterop.generator.visitor;

import java.util.Map;
import jsinterop.generator.model.JavaTypeReference;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;

/**
 * {@link DuplicatedTypesUnifier} detects synthetic types that are duplicated across type hierarchy.
 *
 * <p>This visitor will visit all type references and reuse the type defined on parent interfaces if
 * it found a duplicated type. It will remove the duplicated types from the java program.
 */
public class FixReferencesToDuplicatedTypes extends AbstractModelVisitor {

  private final Map<Type, Type> typesToReplace;

  public FixReferencesToDuplicatedTypes(Map<Type, Type> typesToReplace) {
    this.typesToReplace = typesToReplace;
  }

  @Override
  public void endVisit(Program program) {
    // Remove the synthetic types at the end of this visitor. Otherwise we delete the relationship
    // with their enclosing type and we cannot clean correctly all type references to the synthetic
    // types.
    typesToReplace.keySet().forEach(Type::removeFromParent);
  }

  @Override
  public boolean visit(TypeReference typeReference) {
    if (typeReference instanceof JavaTypeReference) {
      JavaTypeReference javaTypeReference = (JavaTypeReference) typeReference;
      Type syntheticType = javaTypeReference.getTypeDeclaration();

      if (typesToReplace.containsKey(syntheticType)) {
        javaTypeReference.setTypeDeclaration(typesToReplace.get(syntheticType));
      }
    }
    return true;
  }
}
