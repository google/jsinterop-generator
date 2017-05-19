package jsinterop.generator.externs.inheritance;

import java.lang.Object;
import java.lang.String;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface InterfaceWithStructuralType<U, V> {
  @JsFunction
  public interface Bar2BarCallbackFn<U, V> {
    U onInvoke(V p0);
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface Bar2BarUnionType<U, V> {
    @JsOverlay
    // TODO(dramaix): could be: static <V,U> Bar2BarUnionType<V,U> of(Object o);
    static InterfaceWithStructuralType.Bar2BarUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default InterfaceWithStructuralType.Bar2BarCallbackFn<U, V> asBar2BarCallbackFn() {
      return Js.cast(this);
    }

    @JsOverlay
    default String asString() {
      return Js.asString(this);
    }

    @JsOverlay
    default boolean isBar2BarCallbackFn() {
      return (Object) this instanceof InterfaceWithStructuralType.Bar2BarCallbackFn;
    }

    @JsOverlay
    default boolean isString() {
      return (Object) this instanceof String;
    }
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface Bar3Param1UnionType<U> {
    @JsOverlay
    static InterfaceWithStructuralType.Bar3Param1UnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default String asString() {
      return Js.asString(this);
    }

    @JsOverlay
    default U asU() {
      return Js.cast(this);
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

  @JsType
  public interface BazBaz2Type<U, V> {
    @JsProperty
    V getBaz();

    @JsProperty
    U getBaz2();

    @JsProperty
    void setBaz(V baz);

    @JsProperty
    void setBaz2(U baz2);
  }

  @JsType
  public interface BazBazType<U, V> {
    @JsProperty
    U getBaz();

    @JsProperty
    V getBaz2();

    @JsProperty
    void setBaz(U baz);

    @JsProperty
    void setBaz2(V baz2);
  }

  @JsType
  public interface FooFooType {
    @JsProperty
    String getFoo();

    @JsProperty
    void setFoo(String foo);
  }

  @JsType
  public interface FooReturnType {
    @JsProperty
    double getBar();

    @JsProperty
    void setBar(double bar);
  }

  @JsOverlay
  default void bar2(InterfaceWithStructuralType.Bar2BarCallbackFn<U, V> bar) {
    bar2(Js.<InterfaceWithStructuralType.Bar2BarUnionType<U, V>>uncheckedCast(bar));
  }

  void bar2(InterfaceWithStructuralType.Bar2BarUnionType<U, V> bar);

  @JsOverlay
  default void bar2(String bar) {
    bar2(Js.<InterfaceWithStructuralType.Bar2BarUnionType<U, V>>uncheckedCast(bar));
  }

  <U> void bar3(InterfaceWithStructuralType.Bar3Param1UnionType<U> param1, U param2);

  @JsOverlay
  default <U> void bar3(String param1, U param2) {
    bar3(Js.<InterfaceWithStructuralType.Bar3Param1UnionType<U>>uncheckedCast(param1), param2);
  }

  @JsOverlay
  default <U> void bar3(U param1, U param2) {
    bar3(Js.<InterfaceWithStructuralType.Bar3Param1UnionType<U>>uncheckedCast(param1), param2);
  }

  V baz(
      InterfaceWithStructuralType.BazBazType<U, V> baz,
      InterfaceWithStructuralType.BazBaz2Type<U, V> baz2);

  InterfaceWithStructuralType.FooReturnType foo(InterfaceWithStructuralType.FooFooType[][] foo);

  @JsProperty
  InterfaceWithStructuralType.BarCallbackFn getBar();

  @JsProperty
  void setBar(InterfaceWithStructuralType.BarCallbackFn bar);
}
