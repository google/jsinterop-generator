package jsinterop.generator.externs.structuraltypes;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface InnerStructuralType {
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface BarFieldType {
    @JsOverlay
    static InnerStructuralType.BarFieldType create() {
      return Js.uncheckedCast(JsPropertyMap.of());
    }

    @JsProperty
    String getBaz();

    @JsProperty
    void setBaz(String baz);
  }

  @JsOverlay
  static InnerStructuralType create() {
    return Js.uncheckedCast(JsPropertyMap.of());
  }

  @JsProperty
  InnerStructuralType.BarFieldType getBar();

  @JsProperty
  double getFoo();

  @JsProperty
  void setBar(InnerStructuralType.BarFieldType bar);

  @JsProperty
  void setFoo(double foo);
}
