/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jsinterop.generator.visitor;

import static jsinterop.generator.model.AnnotationType.FUNCTIONAL_INTERFACE;

import jsinterop.generator.model.AbstractVisitor;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.ModelVisitor;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.Type;

/** Annotates natives interfaces with one method with FunctionalInterface annotation. */
public final class FunctionalInterfaceAnnotator implements ModelVisitor {

  @Override
  public void applyTo(Program program) {
    program.accept(
        new AbstractVisitor() {
          @Override
          public void exitType(Type type) {
            if (type.isInterface()
                && !type.isStructural()
                && type.getExtendedTypes().isEmpty()
                && type.getMethods().size() == 1) {
              type.addAnnotation(Annotation.builder().type(FUNCTIONAL_INTERFACE).build());
            }
          }
        });
  }
}
