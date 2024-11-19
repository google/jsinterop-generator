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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.function.Function;

/** A list of known Java types used during the generation. */
public enum PredefinedTypes {
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
  NULLABLE("org.jspecify.annotations.Nullable", null),
  JS_ENUM("jsinterop.annotations.JsEnum", null),
  JS_TYPE("jsinterop.annotations.JsType", null),
  JS_PROPERTY("jsinterop.annotations.JsProperty", null),
  JS_METHOD("jsinterop.annotations.JsMethod", null),
  JS_PACKAGE("jsinterop.annotations.JsPackage", null),
  JS_FUNCTION("jsinterop.annotations.JsFunction", null),
  JS_OVERLAY("jsinterop.annotations.JsOverlay", null),
  JS_ARRAY_LIKE("jsinterop.base.JsArrayLike", "IArrayLike", true),
  JS_BIGINT("jsinterop.base.JsBigint", "bigint", true),
  JS_PROPERTY_MAP("jsinterop.base.JsPropertyMap", "IObject", true),
  JS("jsinterop.base.Js", null),
  ANY("jsinterop.base.Any", null, true),
  JS_CONSTRUCTOR_FN("jsinterop.base.JsConstructorFn", null, true),
  ITHENABLE("elemental2.promise.IThenable", "IThenable", true),
  PROMISE("elemental2.promise.Promise", "Promise"),
  ARRAY_STAMPER("javaemul.internal.ArrayStamper", null),
  FUNCTIONAL_INTERFACE("java.lang.FunctionalInterface", null);

  private static final ImmutableMap<String, PredefinedTypes> PREDEFINED_TYPES_BY_NATIVE_FQN =
      Arrays.stream(values())
          .filter(t -> t.nativeFqn != null && t.typeImport != null)
          .collect(toImmutableMap(t -> t.nativeFqn, Function.identity()));

  /**
   * Returns true if the native type is associated with a {@link PredefinedTypes}, false otherwise.
   */
  public static boolean isPredefinedType(String nativeFqn) {
    return PREDEFINED_TYPES_BY_NATIVE_FQN.containsKey(nativeFqn);
  }

  /**
   * Return the possible {@link PredefinedTypes} associated with a native type. This method may
   * return null.
   */
  public static PredefinedTypes getPredefinedType(String nativeFqn) {
    return PREDEFINED_TYPES_BY_NATIVE_FQN.get(nativeFqn);
  }

  private final String javaFqn;
  private final String nativeFqn;
  private final String typeImport;
  private final String typeName;
  private final String typeSignature;
  private final LiteralExpression defaultValue;
  private final boolean isNativeJsTypeInterface;

  /** Constructor defining a PredefinedType representing a known primitive type */
  PredefinedTypes(
      String typeName, String nativeFqn, String typeSignature, LiteralExpression defaultValue) {
    this.javaFqn = typeName;
    this.nativeFqn = nativeFqn;
    this.typeImport = null;
    this.typeName = typeName;
    this.typeSignature = typeSignature;
    this.defaultValue = defaultValue;
    this.isNativeJsTypeInterface = false;
  }

  /** Constructor defining a PredefinedType representing a known declared type */
  PredefinedTypes(String javaFqn, String nativeFqn) {
    this(javaFqn, nativeFqn, false);
  }

  /** Constructor defining a PredefinedType representing a known declared type */
  PredefinedTypes(String javaFqn, String nativeFqn, boolean isNativeJsTypeInterface) {
    checkState(javaFqn.lastIndexOf(".") > 0);

    this.javaFqn = javaFqn;
    this.nativeFqn = nativeFqn;
    this.typeImport = javaFqn;
    this.typeName = javaFqn.substring(javaFqn.lastIndexOf(".") + 1);
    this.typeSignature = "L" + javaFqn.replace('.', '/');
    this.defaultValue = NULL;
    this.isNativeJsTypeInterface = isNativeJsTypeInterface;
  }

  public static boolean isPrimitiveTypeReference(TypeReference typeReference) {
    return (typeReference instanceof PredefinedTypeReference)
        && ((PredefinedTypeReference) typeReference).predefinedType.isPrimitive();
  }

  private boolean isPrimitive() {
    return typeImport == null;
  }

  public TypeReference getReference(boolean isNullable) {
    return new PredefinedTypeReference(this, isNullable);
  }

  private static class PredefinedTypeReference extends TypeReference {
    private final PredefinedTypes predefinedType;

    private PredefinedTypeReference(PredefinedTypes predefinedType, boolean isNullable) {
      super(isNullable);
      this.predefinedType = predefinedType;
      // Nullable reference to a primitive type is not allowed.
      Preconditions.checkState(!isNullable || !predefinedType.isPrimitive());
    }

    @Override
    public String getTypeName() {
      return predefinedType.typeName;
    }

    @Override
    public String getImport() {
      return predefinedType.typeImport;
    }

    @Override
    public String getJsDocAnnotationString() {
      return predefinedType.nativeFqn;
    }

    @Override
    public String getJavaTypeFqn() {
      return predefinedType.javaFqn;
    }

    @Override
    public String getJavaRelativeQualifiedTypeName() {
      return getTypeName();
    }

    @Override
    public String getJniSignature() {
      return predefinedType.typeSignature;
    }

    @Override
    public TypeReference toNonNullableTypeReference() {
      return new PredefinedTypeReference(this.predefinedType, false);
    }

    @Override
    public TypeReference toNullableTypeReference() {
      return new PredefinedTypeReference(this.predefinedType, true);
    }

    @Override
    public Expression getDefaultValue() {
      return predefinedType.defaultValue;
    }

    @Override
    public boolean isInstanceofAllowed() {
      return !predefinedType.isNativeJsTypeInterface;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof PredefinedTypeReference)) {
        return false;
      }
      return predefinedType == ((PredefinedTypeReference) obj).predefinedType;
    }

    @Override
    public int hashCode() {
      return predefinedType.hashCode();
    }

    @Override
    public String toString() {
      return getJavaTypeFqn();
    }

    @Override
    public boolean isReferenceTo(PredefinedTypes predefinedType) {
      return this.predefinedType == predefinedType;
    }
  }
}
