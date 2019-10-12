package jsinterop.generator.externs.generics;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class SimpleClassChild extends SimpleClass<String> {
  public SimpleClassChild() {
    super((String) null);
  }
}
