package jsinterop.generator.externs.generics;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class SimpleClass {
  public static native <V> V foo(SimpleClass obj);

  public native <V> V foo();
}
