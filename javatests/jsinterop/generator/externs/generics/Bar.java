package jsinterop.generator.externs.generics;

import java.lang.Double;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface Bar<U, T, V> {
  @JsFunction
  public interface BarMethod3FooCallbackFn<V, T> {
    @JsType
    public interface ReturnType<T> {
      @JsProperty
      T getFoo();

      @JsProperty
      void setFoo(T foo);
    }

    Bar.BarMethod3FooCallbackFn.ReturnType<T> onInvoke(V p0);
  }

  <V> V barMethod2(T param);
 
  void barMethod3(Bar.BarMethod3FooCallbackFn<V, T> foo);

  @JsProperty
  T getBar();

  @JsProperty
  InterfaceWithGeneric<Double> getBar2();

  @JsProperty
  V getBaz();

  @JsProperty
  U getFoo();

  @JsProperty
  void setBar(T bar);

  @JsProperty
  void setBar2(InterfaceWithGeneric<Double> bar2);

  @JsProperty
  void setBaz(V baz);

  @JsProperty
  void setFoo(U foo);
}
