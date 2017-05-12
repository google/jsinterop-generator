package jsinterop.generator.externs.structuraltypes;

import java.lang.String;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class FooBar {
  public String bar;
  public double foo;
}
