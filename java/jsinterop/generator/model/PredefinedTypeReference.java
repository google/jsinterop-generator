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

import static jsinterop.generator.model.LiteralExpression.FALSE;
import static jsinterop.generator.model.LiteralExpression.NULL;
import static jsinterop.generator.model.LiteralExpression.ZERO;

/** A list of known Java types used during the generation. */
public enum PredefinedTypeReference implements TypeReference {
  VOID("void", "void", null, "V"),
  VOID_OBJECT("Void", "void", "java.lang.Void", "Ljava/lang/Void"),
  BOOLEAN("boolean", "boolean", null, "Z", FALSE),
  BOOLEAN_OBJECT("Boolean", "boolean", "java.lang.Boolean", "Ljava/lang/Boolean"),
  INT("int", "number", null, "I", ZERO),
  LONG("long", "number", null, "J", ZERO),
  DOUBLE("double", "number", null, "D", ZERO),
  DOUBLE_OBJECT("Double", "number", "java.lang.Double", "Ljava/lang/Double"),
  OBJECT("Object", "Object", "java.lang.Object", "Ljava/lang/Object"),
  STRING("String", "string", "java.lang.String", "Ljava/lang/String"),
  JS_TYPE("JsType", null, "jsinterop.annotations.JsType", "Ljsinterop/annotations/JsType"),
  JS_PROPERTY(
      "JsProperty", null, "jsinterop.annotations.JsProperty", "Ljsinterop/annotations/JsProperty"),
  JS_METHOD("JsMethod", null, "jsinterop.annotations.JsMethod", "Ljsinterop/annotations/JsMethod"),
  JS_PACKAGE(
      "JsPackage", null, "jsinterop.annotations.JsPackage", "Ljsinterop/annotations/JsPackage"),
  JS_FUNCTION(
      "JsFunction", null, "jsinterop.annotations.JsFunction", "Ljsinterop/annotations/JsFunction"),
  JS_OVERLAY(
      "JsOverlay", null, "jsinterop.annotations.JsOverlay", "Ljsinterop/annotations/JsOverlay"),
  JS_ARRAY_LIKE("JsArrayLike", null, "jsinterop.base.JsArrayLike", "Ljsinterop/base/JsArrayLike"),
  JS_PROPERTY_MAP(
      "JsPropertyMap", null, "jsinterop.base.JsPropertyMap", "Ljsinterop/base/JsPropertyMap"),
  JS("Js", null, "jsinterop.base.Js", "Ljsinterop/base/Js"),
  ANY("Any", null, "jsinterop.base.Any", "Ljsinterop/base/Any"),
  JS_CONSTRUCTOR_FN(
      "JsConstructorFn", null, "jsinterop.base.JsConstructorFn", "Ljsinterop/base/JsConstructorFn");

  private final String typeName;
  private final String nativeFqn;
  private final String typeSignature;
  private final String typeImport;
  private final LiteralExpression defaultValue;

  PredefinedTypeReference(
      String typeName, String nativeFqn, String typeImport, String typeSignature) {
    this(typeName, nativeFqn, typeImport, typeSignature, NULL);
  }

  PredefinedTypeReference(
      String typeName,
      String nativeFqn,
      String typeImport,
      String typeSignature,
      LiteralExpression defaultValue) {
    this.typeName = typeName;
    this.nativeFqn = nativeFqn;
    this.typeImport = typeImport;
    this.typeSignature = typeSignature;
    this.defaultValue = defaultValue;
  }

  @Override
  public String getTypeName() {
    return typeName;
  }

  @Override
  public String getImport() {
    return typeImport;
  }

  @Override
  public String getComment() {
    return null;
  }

  @Override
  public String getJsDocAnnotationString() {
    return nativeFqn;
  }

  @Override
  public String getJavaTypeFqn() {
    return typeImport != null ? typeImport : typeName;
  }

  @Override
  public String getJavaRelativeQualifiedTypeName() {
    return getTypeName();
  }

  @Override
  public String getJniSignature() {
    return typeSignature;
  }

  @Override
  public Expression getDefaultValue() {
    return defaultValue;
  }

  @Override
  public TypeReference doVisit(ModelVisitor visitor) {
    visitor.visit(this);
    return visitor.endVisit(this);
  }
}
