package jsinterop.generator.externs.modules;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.generator.externs.modules.foo.bar.BarInterface;

@JsType(isNative = true, name = "foo", namespace = JsPackage.GLOBAL)
public class Foo {
  public static BarInterface fooProperty;

  public static native void foo();
}
