package jsinterop.generator.externs.structuraltypes;

import java.lang.Double;
import java.lang.Object;
import java.lang.String;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class SimpleClass {
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface ConsumeAliasedTypeNumberUnionType {
    @JsOverlay
    static SimpleClass.ConsumeAliasedTypeNumberUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default double asDouble() {
      return Js.asDouble(this);
    }

    @JsOverlay
    default String asString() {
      return Js.asString(this);
    }

    @JsOverlay
    default boolean isDouble() {
      return (Object) this instanceof Double;
    }

    @JsOverlay
    default boolean isString() {
      return (Object) this instanceof String;
    }
  }

  @JsType
  public interface ConsumeAndReturnAnonymousReturnType {
    @JsProperty
    String getBar();

    @JsProperty
    void setBar(String bar);
  }

  @JsType
  public interface ConsumeAndReturnAnonymousTypeFooType {
    @JsProperty
    double getFoo();

    @JsProperty
    void setFoo(double foo);
  }

  @JsType
  public interface ConsumeUnionTypeWithRecordTypeStringOrFooType {
    @JsProperty
    String getFoo();

    @JsProperty
    void setFoo(String foo);
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface ConsumeUnionTypeWithRecordTypeStringOrFooUnionType {
    @JsOverlay
    static SimpleClass.ConsumeUnionTypeWithRecordTypeStringOrFooUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default SimpleClass.ConsumeUnionTypeWithRecordTypeStringOrFooType
        asConsumeUnionTypeWithRecordTypeStringOrFooType() {
      return Js.cast(this);
    }

    @JsOverlay
    default String asString() {
      return Js.asString(this);
    }

    @JsOverlay
    default boolean isConsumeUnionTypeWithRecordTypeStringOrFooType() {
      return (Object) this instanceof SimpleClass.ConsumeUnionTypeWithRecordTypeStringOrFooType;
    }

    @JsOverlay
    default boolean isString() {
      return (Object) this instanceof String;
    }
  }

  @JsType
  public interface RecordTypeArrayFieldType {
    @JsProperty
    double getFoo();

    @JsProperty
    void setFoo(double foo);
  }

  public SimpleClass.RecordTypeArrayFieldType[] recordTypeArray;

  public native void consumeAliasedType(SimpleClass.ConsumeAliasedTypeNumberUnionType number);

  @JsOverlay
  public final void consumeAliasedType(String number) {
    consumeAliasedType(Js.<SimpleClass.ConsumeAliasedTypeNumberUnionType>uncheckedCast(number));
  }

  @JsOverlay
  public final void consumeAliasedType(double number) {
    consumeAliasedType(Js.<SimpleClass.ConsumeAliasedTypeNumberUnionType>uncheckedCast(number));
  }

  public native SimpleClass.ConsumeAndReturnAnonymousReturnType consumeAndReturnAnonymousType();

  public native SimpleClass.ConsumeAndReturnAnonymousReturnType consumeAndReturnAnonymousType(
      SimpleClass.ConsumeAndReturnAnonymousTypeFooType foo);

  public native FooBar consumeFooBar2(FooBar2 fooBar);

  public native void consumeInnerStructuralType(InnerStructuralType foo);

  @JsOverlay
  public final void consumeUnionTypeWithRecordType(
      SimpleClass.ConsumeUnionTypeWithRecordTypeStringOrFooType stringOrFoo) {
    consumeUnionTypeWithRecordType(
        Js.<SimpleClass.ConsumeUnionTypeWithRecordTypeStringOrFooUnionType>uncheckedCast(
            stringOrFoo));
  }

  public native void consumeUnionTypeWithRecordType(
      SimpleClass.ConsumeUnionTypeWithRecordTypeStringOrFooUnionType stringOrFoo);

  @JsOverlay
  public final void consumeUnionTypeWithRecordType(String stringOrFoo) {
    consumeUnionTypeWithRecordType(
        Js.<SimpleClass.ConsumeUnionTypeWithRecordTypeStringOrFooUnionType>uncheckedCast(
            stringOrFoo));
  }
}
