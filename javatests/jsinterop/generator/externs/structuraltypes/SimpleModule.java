package jsinterop.generator.externs.structuraltypes;

import java.lang.String;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;


@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class SimpleModule {
  @JsType
  public interface FooBarType {
    @JsProperty
    String getBar();

    @JsProperty
    void setBar(String bar);
  }

  public static native void foo(SimpleModule.FooBarType bar);
}
