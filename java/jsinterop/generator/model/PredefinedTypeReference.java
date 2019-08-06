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

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static jsinterop.generator.model.LiteralExpression.FALSE;
import static jsinterop.generator.model.LiteralExpression.NULL;
import static jsinterop.generator.model.LiteralExpression.ZERO;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

/** A list of known Java types used during the generation. */
public enum PredefinedTypeReference implements TypeReference {
  VOID("void", "void", "V", NULL),
  VOID_OBJECT("java.lang.Void", "void"),
  BOOLEAN("boolean", "boolean", "Z", FALSE),
  BOOLEAN_OBJECT("java.lang.Boolean", "boolean"),
  INT("int", "number", "I", ZERO),
  LONG("long", "number", "J", ZERO),
  DOUBLE("double", "number", "D", ZERO),
  DOUBLE_OBJECT("java.lang.Double", "number"),
  OBJECT("java.lang.Object", "*"),
  STRING("java.lang.String", "string"),
  CLASS("java.lang.Class", null),
  DEPRECATED("java.lang.Deprecated", "deprecated"),
  JS_ENUM("jsinterop.annotations.JsEnum", null),
  JS_TYPE("jsinterop.annotations.JsType", null),
  JS_PROPERTY("jsinterop.annotations.JsProperty", null),
  JS_METHOD("jsinterop.annotations.JsMethod", null),
  JS_PACKAGE("jsinterop.annotations.JsPackage", null),
  JS_FUNCTION("jsinterop.annotations.JsFunction", null),
  JS_OVERLAY("jsinterop.annotations.JsOverlay", null),
  JS_ARRAY_LIKE("jsinterop.base.JsArrayLike", "IArrayLike") {
    @Override
    public boolean isInstanceofAllowed() {
      return false;
    }
  },
  JS_PROPERTY_MAP("jsinterop.base.JsPropertyMap", "IObject") {
    @Override
    public boolean isInstanceofAllowed() {
      return false;
    }
  },
  JS("jsinterop.base.Js", null),
  ANY("jsinterop.base.Any", null),
  JS_CONSTRUCTOR_FN("jsinterop.base.JsConstructorFn", null),
  ITHENABLE("elemental2.promise.IThenable", "IThenable") {
    @Override
    public boolean isInstanceofAllowed() {
      return false;
    }
  },
  PROMISE("elemental2.promise.Promise", "Promise");

  private static final Map<String, PredefinedTypeReference> nativePredefinedTypesByNativeFqn =
      Arrays.stream(values())
          .filter(t -> t.nativeFqn != null && !t.isPrimitive())
          .collect(toImmutableMap(t -> t.nativeFqn, Function.identity()));

  /**
   * Returns true if the native type is associated with a {@link PredefinedTypeReference}, false
   * otherwise.
   */
  public static boolean isPredefinedType(String nativeFqn) {
    return getPredefinedType(nativeFqn) != null;
  }

  /**
   * Return the possible {@link PredefinedTypeReference} associated with a native type. This method
   * may return null.
   */
  public static PredefinedTypeReference getPredefinedType(String nativeFqn) {
    return nativePredefinedTypesByNativeFqn.get(nativeFqn);
  }

  private final String typeName;
  private final String nativeFqn;
  private final String typeSignature;
  private final String typeImport;
  private final LiteralExpression defaultValue;
  private final boolean isPrimitive;

  /** Constructor defining a PredefinedTypeReference to a primitive type */
  PredefinedTypeReference(
      String typeName, String nativeFqn, String typeSignature, LiteralExpression defaultValue) {
    this.typeName = typeName;
    this.nativeFqn = nativeFqn;
    this.typeImport = null;
    this.typeSignature = typeSignature;
    this.defaultValue = defaultValue;
    this.isPrimitive = true;
  }

  /** Constructor defining a PredefinedTypeReference to a object type */
  PredefinedTypeReference(String javaFqn, String nativeFqn) {
    this.nativeFqn = nativeFqn;
    this.typeImport = javaFqn;
    checkState(javaFqn.lastIndexOf(".") > 0);
    this.typeName = javaFqn.substring(javaFqn.lastIndexOf(".") + 1);
    this.typeSignature = "L" + javaFqn.replace('.', '/');
    this.defaultValue = NULL;
    this.isPrimitive = false;
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

  private boolean isPrimitive() {
    return isPrimitive;
  }
}
