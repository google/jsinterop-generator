package jsinterop.generator.externs.structuraltypes;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class SimpleModule {
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface FooBarType {
    @JsProperty
    String getBar();

    @JsProperty
    void setBar(String bar);
  }

  public static native void foo(SimpleModule.FooBarType bar);
}
