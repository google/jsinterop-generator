package jsinterop.generator.externs.inheritance;

import java.lang.Object;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class Array<T> {
  public Array(Object... args) {}
}
