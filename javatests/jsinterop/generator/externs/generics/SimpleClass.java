package jsinterop.generator.externs.generics;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class SimpleClass<T> {
  public static native <V> V foo(SimpleClass obj);

  public SimpleClass(T value) {}

  public native SimpleClass<T> chainableMethodWithThis(SimpleClass<T> param);

  public native <V> V foo();
}
