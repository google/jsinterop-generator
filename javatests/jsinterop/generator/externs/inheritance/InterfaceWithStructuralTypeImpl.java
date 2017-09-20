package jsinterop.generator.externs.inheritance;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class InterfaceWithStructuralTypeImpl<V, U> implements InterfaceWithStructuralType<V, U> {
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface Bar4Param1UnionType {
    @JsOverlay
    static InterfaceWithStructuralTypeImpl.Bar4Param1UnionType of(Object o) {
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

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface Bar4Param2UnionType {
    @JsOverlay
    static InterfaceWithStructuralTypeImpl.Bar4Param2UnionType of(Object o) {
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

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface Bar4UnionType {
    @JsOverlay
    static InterfaceWithStructuralTypeImpl.Bar4UnionType of(Object o) {
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

  @JsFunction
  public interface BarCallbackFn {
    void onInvoke(boolean p0);
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface FooFooType {
    @JsOverlay
    static InterfaceWithStructuralTypeImpl.FooFooType create() {
      return Js.uncheckedCast(JsPropertyMap.of());
    }

    @JsProperty
    String getFoo();

    @JsProperty
    void setFoo(String foo);
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface FooReturnType {
    @JsOverlay
    static InterfaceWithStructuralTypeImpl.FooReturnType create() {
      return Js.uncheckedCast(JsPropertyMap.of());
    }

    @JsProperty
    double getBar();

    @JsProperty
    void setBar(double bar);
  }

  @JsProperty(name = "bar")
  public static InterfaceWithStructuralTypeImpl.BarCallbackFn bar_STATIC;

  public static native InterfaceWithStructuralTypeImpl.FooReturnType foo(
      InterfaceWithStructuralTypeImpl.FooFooType[][] foo);

  public InterfaceWithStructuralType.BarCallbackFn bar;

  public native void bar2(InterfaceWithStructuralType.Bar2BarUnionType<V, U> bar);

  public native <U> void bar3(InterfaceWithStructuralType.Bar3Param1UnionType<U> param1, U param2);

  public native InterfaceWithStructuralTypeImpl.Bar4UnionType bar4(
      InterfaceWithStructuralTypeImpl.Bar4Param1UnionType param1,
      InterfaceWithStructuralTypeImpl.Bar4Param2UnionType param2);

  @JsOverlay
  public final InterfaceWithStructuralTypeImpl.Bar4UnionType bar4(
      InterfaceWithStructuralTypeImpl.Bar4Param1UnionType param1, String param2) {
    return bar4(
        param1, Js.<InterfaceWithStructuralTypeImpl.Bar4Param2UnionType>uncheckedCast(param2));
  }

  @JsOverlay
  public final InterfaceWithStructuralTypeImpl.Bar4UnionType bar4(
      InterfaceWithStructuralTypeImpl.Bar4Param1UnionType param1, double param2) {
    return bar4(
        param1, Js.<InterfaceWithStructuralTypeImpl.Bar4Param2UnionType>uncheckedCast(param2));
  }

  @JsOverlay
  public final InterfaceWithStructuralTypeImpl.Bar4UnionType bar4(
      String param1, InterfaceWithStructuralTypeImpl.Bar4Param2UnionType param2) {
    return bar4(
        Js.<InterfaceWithStructuralTypeImpl.Bar4Param1UnionType>uncheckedCast(param1), param2);
  }

  @JsOverlay
  public final InterfaceWithStructuralTypeImpl.Bar4UnionType bar4(String param1, String param2) {
    return bar4(
        Js.<InterfaceWithStructuralTypeImpl.Bar4Param1UnionType>uncheckedCast(param1),
        Js.<InterfaceWithStructuralTypeImpl.Bar4Param2UnionType>uncheckedCast(param2));
  }

  @JsOverlay
  public final InterfaceWithStructuralTypeImpl.Bar4UnionType bar4(String param1, double param2) {
    return bar4(
        Js.<InterfaceWithStructuralTypeImpl.Bar4Param1UnionType>uncheckedCast(param1),
        Js.<InterfaceWithStructuralTypeImpl.Bar4Param2UnionType>uncheckedCast(param2));
  }

  @JsOverlay
  public final InterfaceWithStructuralTypeImpl.Bar4UnionType bar4(
      double param1, InterfaceWithStructuralTypeImpl.Bar4Param2UnionType param2) {
    return bar4(
        Js.<InterfaceWithStructuralTypeImpl.Bar4Param1UnionType>uncheckedCast(param1), param2);
  }

  @JsOverlay
  public final InterfaceWithStructuralTypeImpl.Bar4UnionType bar4(double param1, String param2) {
    return bar4(
        Js.<InterfaceWithStructuralTypeImpl.Bar4Param1UnionType>uncheckedCast(param1),
        Js.<InterfaceWithStructuralTypeImpl.Bar4Param2UnionType>uncheckedCast(param2));
  }

  @JsOverlay
  public final InterfaceWithStructuralTypeImpl.Bar4UnionType bar4(double param1, double param2) {
    return bar4(
        Js.<InterfaceWithStructuralTypeImpl.Bar4Param1UnionType>uncheckedCast(param1),
        Js.<InterfaceWithStructuralTypeImpl.Bar4Param2UnionType>uncheckedCast(param2));
  }

  public native U baz(
      InterfaceWithStructuralType.BazBazType<V, U> baz,
      InterfaceWithStructuralType.BazBaz2Type<V, U> baz2);

  public native InterfaceWithStructuralType.FooReturnType foo(
      InterfaceWithStructuralType.FooFooType[][] foo);

  @JsProperty
  public native InterfaceWithStructuralType.BarCallbackFn getBar();

  @JsProperty
  public native void setBar(InterfaceWithStructuralType.BarCallbackFn bar);
}
