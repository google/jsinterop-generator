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
  JS_ENUM(PredefinedTypes.JS_ENUM, true),
  JS_TYPE(PredefinedTypes.JS_TYPE, true),
  JS_PROPERTY(PredefinedTypes.JS_PROPERTY, false),
  JS_METHOD(PredefinedTypes.JS_METHOD, false),
  JS_PACKAGE(PredefinedTypes.JS_PACKAGE, false),
  JS_FUNCTION(PredefinedTypes.JS_FUNCTION, false),
  JS_OVERLAY(PredefinedTypes.JS_OVERLAY, false),
  DEPRECATED(PredefinedTypes.DEPRECATED, false),
  FUNCTIONAL_INTERFACE(PredefinedTypes.FUNCTIONAL_INTERFACE, false);

  private final TypeReference type;
  private final boolean isJsInteropTypeAnnotation;

  AnnotationType(PredefinedTypes type, boolean isJsInteropTypeAnnotation) {
    this.type = type.getReference();
    this.isJsInteropTypeAnnotation = isJsInteropTypeAnnotation;
  }

  public TypeReference getType() {
    return type;
  }

  /**
   * Returns {@code true} if the annotation is a JsInterop annotation targeting a type, {@code
   * false} otherwise.
   */
  public boolean isJsInteropTypeAnnotation() {
    return isJsInteropTypeAnnotation;
  }
}
