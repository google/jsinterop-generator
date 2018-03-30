package jsinterop.generator.externs.simpleclass;

import java.lang.Deprecated;
import java.lang.String;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "SimpleInterface", namespace = JsPackage.GLOBAL)
class SimpleInterface__Constants {
  @Deprecated static boolean deprecatedStaticProperty;
  static String staticProperty;
}
