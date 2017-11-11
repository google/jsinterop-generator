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

package jsinterop.generator.model;

import static jsinterop.generator.model.LiteralExpression.NULL;
import static jsinterop.generator.model.PredefinedTypeReference.OBJECT;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a wildcard type argument. Examples:
 *
 * <ul>
 *   <li>?
 *   <li>? extends Foo
 *   <li>? super T
 * </ul>
 *
 * <p>A wildcard may have a upper bound ({@code ? extends Foo}) or a lower bound ({@code ? super
 * Foo}) or neither ({@code ?}) but not both.
 */
public class WildcardTypeReference implements TypeReference {
  public static WildcardTypeReference createWildcardUpperBound(TypeReference upperBound) {
    if (upperBound.equals(PredefinedTypeReference.OBJECT)) {
      return createUnboundedWildcard();
    }
    return new WildcardTypeReference(upperBound, null);
  }

  public static WildcardTypeReference createWildcardLowerBound(TypeReference lowerBound) {
    if (lowerBound.equals(PredefinedTypeReference.OBJECT)) {
      return createUnboundedWildcard();
    }
    return new WildcardTypeReference(null, lowerBound);
  }

  public static WildcardTypeReference createUnboundedWildcard() {
    return new WildcardTypeReference(null, null);
  }

  private TypeReference upperBound;
  private TypeReference lowerBound;

  private WildcardTypeReference(TypeReference upperBound, TypeReference lowerBound) {
    this.upperBound = upperBound;
    this.lowerBound = lowerBound;
  }

  public TypeReference getLowerBound() {
    return lowerBound;
  }

  public TypeReference getUpperBound() {
    return upperBound;
  }

  public void setLowerBound(TypeReference lowerBound) {
    this.lowerBound = lowerBound;
  }

  public void setUpperBound(TypeReference upperBound) {
    this.upperBound = upperBound;
  }

  public TypeReference getBound() {
    return (lowerBound != null) ? lowerBound : (upperBound != null) ? upperBound : OBJECT;
  }

  @Override
  public String getTypeName() {
    return getBound().getTypeName();
  }

  @Override
  public String getImport() {
    return null;
  }

  @Override
  public String getComment() {
    return null;
  }

  @Override
  public String getJsDocAnnotationString() {
    return getBound().getJsDocAnnotationString();
  }

  @Override
  public String getJavaTypeFqn() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getJavaRelativeQualifiedTypeName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getJniSignature() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Expression getDefaultValue() {
    return NULL;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof WildcardTypeReference)) {
      return false;
    }

    return Objects.equals(lowerBound, ((WildcardTypeReference) o).lowerBound)
        && Objects.equals(upperBound, ((WildcardTypeReference) o).upperBound);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {lowerBound, upperBound});
  }

  @Override
  public String toString() {
    if (lowerBound != null) {
      return "? super " + lowerBound;
    } else if (upperBound != null) {
      return "? extends " + upperBound;
    }
    return "?";
  }
}
