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

package jsinterop.generator.model;

import java.util.Collection;
import jsinterop.generator.model.Method.Parameter;

/** Base contract for a object that want to visit the java model. */
public interface ModelVisitor {
  boolean visit(TypeReference typeReference);

  boolean visit(Type type);

  boolean visit(Method method);

  boolean visit(Field field);

  boolean visit(Method.Parameter parameter);

  boolean visit(Program program);

  void endVisit(Type type);

  void endVisit(Field field);

  boolean visit(Statement statement);

  boolean visit(Expression expression);

  <T extends Visitable<T>> T accept(T visitable);

  <T extends Visitable<T>> Collection<T> accept(Collection<T> visitables);

  void endVisit(Method method);

  void endVisit(Program program);

  void endVisit(Parameter parameter);

  TypeReference endVisit(TypeReference typeReference);
}
