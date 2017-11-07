package jsinterop.generator.externs.generics;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface Bar<U, T, V> {
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface BarFieldType<T> {
    @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
    public interface FooFieldType<T> {
      @JsOverlay
      static Bar.BarFieldType.FooFieldType create() {
        return Js.uncheckedCast(JsPropertyMap.of());
      }

      @JsProperty
      T getBaz();

      @JsProperty
      void setBaz(T baz);
    }

    @JsOverlay
    static Bar.BarFieldType create() {
      return Js.uncheckedCast(JsPropertyMap.of());
    }

    @JsProperty
    Bar.BarFieldType.FooFieldType<T> getFoo();

    @JsProperty
    void setFoo(Bar.BarFieldType.FooFieldType<T> foo);
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface BarMethod2ParamType<V, T> {
    @JsOverlay
    static Bar.BarMethod2ParamType create() {
      return Js.uncheckedCast(JsPropertyMap.of());
    }

    @JsProperty
    T getBar();

    @JsProperty
    V getFoo();

    @JsProperty
    void setBar(T bar);

    @JsProperty
    void setFoo(V foo);
  }

  @JsFunction
  public interface BarMethod3FooCallbackFn<Z, Y, T, V> {
    @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
    public interface ReturnType<Z, Y, T> {
      @JsOverlay
      static Bar.BarMethod3FooCallbackFn.ReturnType create() {
        return Js.uncheckedCast(JsPropertyMap.of());
      }

      @JsProperty
      Y getBar();

      @JsProperty
      Z getBaz();

      @JsProperty
      T getFoo();

      @JsProperty
      void setBar(Y bar);

      @JsProperty
      void setBaz(Z baz);

      @JsProperty
      void setFoo(T foo);
    }

    Bar.BarMethod3FooCallbackFn.ReturnType<Z, Y, T> onInvoke(V p0);
  }

  @JsFunction
  public interface BarMethod4FooCallbackFn<U> {
    U onInvoke(U p0);
  }

  @JsFunction
  public interface BarMethod5FooCallbackFn<U, T> {
    @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
    public interface P2UnionType<T> {
      @JsOverlay
      static Bar.BarMethod5FooCallbackFn.P2UnionType of(Object o) {
        return Js.cast(o);
      }

      @JsOverlay
      default String asString() {
        return Js.asString(this);
      }

      @JsOverlay
      default T asT() {
        return Js.cast(this);
      }

      @JsOverlay
      default boolean isString() {
        return (Object) this instanceof String;
      }
    }

    void onInvoke(U p0, U[] p1, Bar.BarMethod5FooCallbackFn.P2UnionType<T> p2);

    @JsOverlay
    default void onInvoke(U p0, U[] p1, String p2) {
      onInvoke(p0, p1, Js.<Bar.BarMethod5FooCallbackFn.P2UnionType<T>>uncheckedCast(p2));
    }

    @JsOverlay
    default void onInvoke(U p0, U[] p1, T p2) {
      onInvoke(p0, p1, Js.<Bar.BarMethod5FooCallbackFn.P2UnionType<T>>uncheckedCast(p2));
    }
  }

  @JsFunction
  public interface BarMethod6FooCallbackFn<U, T> {
    InterfaceWithGeneric<T> onInvoke(U p0, InterfaceWithGeneric<U> p1);
  }

  <V> V barMethod2(Bar.BarMethod2ParamType<V, T> param);

  <Z, Y> void barMethod3(Bar.BarMethod3FooCallbackFn<Z, Y, T, ? super V> fooCallback);

  void barMethod4(Bar.BarMethod4FooCallbackFn<U> fooCallback);

  void barMethod5(Bar.BarMethod5FooCallbackFn<? super U, ? super T> fooCallback);

  void barMethod6(Bar.BarMethod6FooCallbackFn<U, T> fooCallback);

  @JsProperty
  InterfaceWithGeneric<Bar.BarFieldType<T>> getBar();

  @JsProperty
  InterfaceWithGeneric<Double> getBar2();

  @JsProperty
  V getBaz();

  @JsProperty
  U getFoo();

  @JsProperty
  void setBar(InterfaceWithGeneric<Bar.BarFieldType<T>> bar);

  @JsProperty
  void setBar2(InterfaceWithGeneric<Double> bar2);

  @JsProperty
  void setBaz(V baz);

  @JsProperty
  void setFoo(U foo);
}
