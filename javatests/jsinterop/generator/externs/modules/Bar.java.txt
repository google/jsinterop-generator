package jsinterop.generator.externs.modules.foo;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "foo.bar", namespace = JsPackage.GLOBAL)
public class Bar {
  public static FooInterface fooProperty;

  public static native void bar();
}
