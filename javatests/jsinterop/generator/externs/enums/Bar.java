package jsinterop.generator.externs.enums;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
public final class Bar {
  @JsOverlay public static final Bar BAR1 = Bar__Constants.BAR1;
  @JsOverlay public static final Bar BAR2 = Bar__Constants.BAR2;

  private Bar() {}

  @JsOverlay
  public final double getValue() {
    return Js.<Double>uncheckedCast(this);
  }
}
