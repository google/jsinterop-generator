package jsinterop.generator.externs.enums;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface Baz {
  Bar toBar(Foo foo);
}
