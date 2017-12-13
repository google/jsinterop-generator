package jsinterop.generator.externs.enums;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
public final class Foo {
  @JsOverlay public static final Foo FOO1 = Foo__Constants.FOO1;
  @JsOverlay public static final Foo FOO2 = Foo__Constants.FOO2;

  private Foo() {}

  @JsOverlay
  public final String getValue() {
    return Js.<String>uncheckedCast(this);
  }
}
