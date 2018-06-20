package jsinterop.generator.externs.dependency;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "window", namespace = JsPackage.GLOBAL)
class MyLibGlobal__Constants {
  static double globalConstant;
}
