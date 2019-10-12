package jsinterop.generator.externs.structuraltypes;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface FooBar2 {
  @JsOverlay
  static FooBar2 create() {
    return Js.uncheckedCast(JsPropertyMap.of());
  }

  @JsProperty
  String getBar();

  @JsProperty
  double getFoo();

  @JsProperty
  void setBar(String bar);

  @JsProperty
  void setFoo(double foo);
}
