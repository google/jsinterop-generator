package jsinterop.generator.externs.modules;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.generator.externs.modules.foo.FooInterface;
import jsinterop.generator.externs.modules.foo.bar.BarInterface;

@JsType(isNative = true, name = "baz", namespace = JsPackage.GLOBAL)
public class Baz {
  public static BarInterface barProperty;
  public static FooInterface fooProperty;

  public static native void foo();
}
