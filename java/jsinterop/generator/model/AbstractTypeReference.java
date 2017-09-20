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

import static jsinterop.generator.model.LiteralExpression.NULL;

import java.util.Objects;

/** Abstract implementation of TypeReference */
public abstract class AbstractTypeReference implements TypeReference {
  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AbstractTypeReference)) {
      return false;
    }

    return Objects.equals(getJavaTypeFqn(), ((AbstractTypeReference) o).getJavaTypeFqn());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getJavaTypeFqn());
  }

  @Override
  public Expression getDefaultValue() {
    return NULL;
  }

  @Override
  public String toString() {
    return getTypeName();
  }
}
