package jsinterop.generator.externs.generics;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface InterfaceWithGeneric<T> {
  @JsProperty
  T getFoo();

  void method(T foo);

  T method2();

  @JsProperty
  void setFoo(T foo);
}
