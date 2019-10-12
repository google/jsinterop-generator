package jsinterop.generator.externs.dictionarytype;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface DictionaryType {
  @JsOverlay
  static DictionaryType create() {
    return Js.uncheckedCast(JsPropertyMap.of());
  }

  @JsProperty
  double getBar();

  @JsProperty
  String getFoo();

  @JsProperty
  void setBar(double bar);

  @JsProperty
  void setFoo(String foo);
}
