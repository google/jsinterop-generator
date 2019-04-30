package jsinterop.generator.externs.generics;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface BarChild<U, T, V> extends Bar<U, T, V> {
  T methodWithThis(U u, V v);
}
