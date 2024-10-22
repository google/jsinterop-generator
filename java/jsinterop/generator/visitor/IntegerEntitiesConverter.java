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

import static jsinterop.generator.model.PredefinedTypes.DOUBLE;
import static jsinterop.generator.model.PredefinedTypes.INT;

import com.google.common.collect.ImmutableSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import jsinterop.generator.helper.Problems;
import jsinterop.generator.model.AbstractRewriter;
import jsinterop.generator.model.ModelVisitor;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.TypeReference;

/** Handles number javascript entities that need to be converted to int instead of double. */
public class IntegerEntitiesConverter implements ModelVisitor {
  private final Set<String> integerEntities;
  private final Set<String> unusedIntegerEntities;
  private final Problems problems;

  IntegerEntitiesConverter(List<String> integerEntities, Problems problems) {
    this.integerEntities = ImmutableSet.copyOf(integerEntities);
    // use of LinkedHashSet for always reporting unused entities in the same order
    this.unusedIntegerEntities = new LinkedHashSet<>(integerEntities);
    this.problems = problems;
  }

  @Override
  public void applyTo(Program program) {
    if (integerEntities.isEmpty()) {
      return;
    }

    program.accept(
        new AbstractRewriter() {

          @Override
          public TypeReference rewriteTypeReference(TypeReference typeReference) {
            if (mustBeConvertedToInt(typeReference)) {
              unusedIntegerEntities.remove(getCurrentConfigurationIdentifier());
              return INT.getReference();
            }

            return typeReference;
          }

          private boolean mustBeConvertedToInt(TypeReference originalTypeReference) {
            return integerEntities.contains(getCurrentConfigurationIdentifier())
                && originalTypeReference.isReferenceTo(DOUBLE);
          }

          private String getCurrentConfigurationIdentifier() {
            if (getCurrentParameter() != null) {
              return getCurrentMethod().getConfigurationIdentifier()
                  + "."
                  + getCurrentParameter().getConfigurationIdentifier();
            }

            if (getCurrentMethod() != null) {
              return getCurrentMethod().getConfigurationIdentifier();
            }

            if (getCurrentField() != null) {
              return getCurrentField().getConfigurationIdentifier();
            }

            return null;
          }
        });

    if (!unusedIntegerEntities.isEmpty()) {
      problems.warning("Unused integer entities: %s", unusedIntegerEntities);
    }
  }
}
