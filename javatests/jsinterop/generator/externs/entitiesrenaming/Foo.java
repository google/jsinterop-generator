package jsinterop.generator.externs.entitiesrenaming;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class Foo {
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface BarBarType {
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
