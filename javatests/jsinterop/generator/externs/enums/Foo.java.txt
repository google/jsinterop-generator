package jsinterop.generator.externs.enums;

import jsinterop.annotations.JsEnum;
import jsinterop.annotations.JsPackage;

@JsEnum(isNative = true, namespace = JsPackage.GLOBAL)
public enum Foo {
  FOO1,
  FOO2;
}
