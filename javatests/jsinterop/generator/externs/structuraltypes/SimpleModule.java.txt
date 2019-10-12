package jsinterop.generator.externs.structuraltypes;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class SimpleModule {
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface FooBarType {
    @JsOverlay
    static SimpleModule.FooBarType create() {
      return Js.uncheckedCast(JsPropertyMap.of());
    }

    @JsProperty
    String getBar();

    @JsProperty
    void setBar(String bar);
  }

  public static native void foo(SimpleModule.FooBarType bar);
}
