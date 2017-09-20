package jsinterop.generator.externs.entitiesrenaming;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class Foo {
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface BarBarType {
    @JsOverlay
    static Foo.BarBarType create() {
      return Js.uncheckedCast(JsPropertyMap.of());
    }

    @JsProperty
    String getBar();

    @JsProperty
    void setBar(String bar);
  }

  @JsFunction
  public interface FooFooCallbackFn {
    void onInvoke(String bar);
  }

  public native void bar(Foo.BarBarType bar);

  public native void foo(Foo.FooFooCallbackFn foo);
}
