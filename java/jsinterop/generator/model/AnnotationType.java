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
  JS_ENUM(PredefinedTypeReference.JS_ENUM, true, true, true, true),
  JS_TYPE(PredefinedTypeReference.JS_TYPE, true, true, true, true),
  JS_PROPERTY(PredefinedTypeReference.JS_PROPERTY, false, false, true, true),
  JS_METHOD(PredefinedTypeReference.JS_METHOD, false, false, true, true),
  JS_PACKAGE(PredefinedTypeReference.JS_PACKAGE, false, false, true, false),
  JS_FUNCTION(PredefinedTypeReference.JS_FUNCTION, false, false, false, false),
  JS_OVERLAY(PredefinedTypeReference.JS_OVERLAY, false, false, false, false),
  DEPRECATED(PredefinedTypeReference.DEPRECATED, false, false, false, false);

  private final PredefinedTypeReference type;
  private final boolean isTypeAnnotation;
  private final boolean hasNameAttribute;
  private final boolean hasNamespaceAttribute;
  private final boolean hasIsNativeAttribute;

  AnnotationType(
      PredefinedTypeReference type,
      boolean isTypeAnnotation,
      boolean hasIsNativeAttribute,
      boolean hasNamespaceAttribute,
      boolean hasNameAttribute) {
    this.type = type;
    this.isTypeAnnotation = isTypeAnnotation;
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

  public boolean isTypeAnnotation() {
    return isTypeAnnotation;
  }
}
