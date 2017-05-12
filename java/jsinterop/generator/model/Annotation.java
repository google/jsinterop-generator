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

import com.google.auto.value.AutoValue;
import javax.annotation.Nullable;

/** Model Java Annotation and more especially JsInterop annotations. */
@AutoValue
public abstract class Annotation {
  /** Builder for {@link Annotation} */
  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder type(AnnotationType type);

    public abstract Builder nameAttribute(@Nullable String nameAttribute);

    public abstract Builder namespaceAttribute(@Nullable String namespaceAttribute);

    public abstract Builder isNativeAttribute(boolean isNativeAttribute);

    public abstract Annotation build();
  }

  public static Builder builder() {
    return new AutoValue_Annotation.Builder().isNativeAttribute(false);
  }

  @Nullable
  public abstract String getNameAttribute();

  @Nullable
  public abstract String getNamespaceAttribute();

  public abstract boolean getIsNativeAttribute();

  public abstract AnnotationType getType();

  public Annotation withNamespaceAttribute(String namespaceAttribute) {
    return toBuilder().namespaceAttribute(namespaceAttribute).build();
  }

  public Annotation withNameAttribute(String nameAttribute) {
    return toBuilder().nameAttribute(nameAttribute).build();
  }

  abstract Builder toBuilder();
}
