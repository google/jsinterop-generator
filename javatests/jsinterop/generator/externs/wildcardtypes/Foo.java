package jsinterop.generator.externs.wildcardtypes;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class Foo<T> {
  public native void bar(Bar<? super String, Double, T> bar);

  public native void foo(Bar<? super String, Double, ? extends T> foo);
}
