package jsinterop.generator.externs.dependency;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "goog.global", namespace = JsPackage.GLOBAL)
public class MyLibGlobal {
  @JsOverlay public static final double globalConstant = MyLibGlobal__Constants.globalConstant;

  public static native String foo();
}
