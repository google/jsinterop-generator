package jsinterop.generator.externs.integerentities;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "goog.global", namespace = JsPackage.GLOBAL)
public class Global {
  public static int baz;

  public static native int foo(int bar);
}
