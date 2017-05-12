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
 */
package jsinterop.generator.model;


/** A list of annotations we use in our JsInterop code generation. */
public enum AnnotationType {
  JS_TYPE(PredefinedTypeReference.JS_TYPE, true, true, true),
  JS_PROPERTY(PredefinedTypeReference.JS_PROPERTY, false, true, true),
  JS_METHOD(PredefinedTypeReference.JS_METHOD, false, true, true),
  JS_PACKAGE(PredefinedTypeReference.JS_PACKAGE, false, true, false),
  JS_FUNCTION(PredefinedTypeReference.JS_FUNCTION, false, false, false),
  JS_OVERLAY(PredefinedTypeReference.JS_OVERLAY, false, false, false);

  private final PredefinedTypeReference type;
  private boolean hasNamespaceAttribute;
  private boolean hasNameAttribute;
  private boolean hasIsNativeAttribute;

  AnnotationType(
      PredefinedTypeReference type,
      boolean hasIsNativeAttribute,
      boolean hasNamespaceAttribute,
      boolean hasNameAttribute) {
    this.type = type;
    this.hasNamespaceAttribute = hasNamespaceAttribute;
    this.hasNameAttribute = hasNameAttribute;
    this.hasIsNativeAttribute = hasIsNativeAttribute;
  }

  public TypeReference getType() {
    return type;
  }

  public boolean hasNamespaceAttribute() {
    return hasNamespaceAttribute;
  }

  public boolean hasNameAttribute() {
    return hasNameAttribute;
  }

  public boolean hasIsNativeAttribute() {
    return hasIsNativeAttribute;
  }
}
