package jsinterop.generator.externs.enums;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
class Foo__Constants {
  @JsProperty(name = "FOO1", namespace = "Foo")
  static Foo FOO1;

  @JsProperty(name = "FOO2", namespace = "Foo")
  static Foo FOO2;
}
