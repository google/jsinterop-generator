package jsinterop.generator.externs.simpleclass;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class PrivateClass {
  private PrivateClass() {}
}
