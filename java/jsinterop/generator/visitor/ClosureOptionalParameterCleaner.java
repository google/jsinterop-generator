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

import static jsinterop.generator.helper.GeneratorUtils.toSafeTypeName;

import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;
import jsinterop.generator.model.AbstractVisitor;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Parameter;
import jsinterop.generator.model.Program;

/**
 * Closure requires that optional parameters are prefixed by <code>opt_</code>. In Java, we create a
 * method overloading for each optional parameters. This visitor removes the unneeded prefix <code>
 * opt_</code>.
 */
public class ClosureOptionalParameterCleaner extends AbstractModelVisitor {

  @Override
  public void applyTo(Program program) {
    program.accept(
        new AbstractVisitor() {
          @Override
          public void exitMethod(Method node) {
            Set<String> parameterNames = new HashSet<>();

            for (ListIterator<Parameter> it = node.getParameters().listIterator(); it.hasNext(); ) {
              Parameter parameter = it.next();

              if (parameter.getName().startsWith("opt_")) {
                it.set(
                    parameter.toBuilder()
                        .setName(toSafeTypeName(parameter.getName().substring(4), parameterNames))
                        .build());
              }
              parameterNames.add(parameter.getName());
            }
          }
        });
  }
}
