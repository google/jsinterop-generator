package jsinterop.generator.externs.simpleclass;

import java.lang.Deprecated;
import java.lang.String;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "SimpleClass", namespace = JsPackage.GLOBAL)
class SimpleClass__Constants {
  @Deprecated static String deprecatedConstant;
  static String staticReadonlyProperty;
}
