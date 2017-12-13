package jsinterop.generator.externs.enums;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
class Bar__Constants {
  @JsProperty(name = "BAR1", namespace = "Bar")
  static Bar BAR1;

  @JsProperty(name = "BAR2", namespace = "Bar")
  static Bar BAR2;
}
