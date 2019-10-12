package jsinterop.generator.externs.dictionarytype;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface SimpleDictionaryType {
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface BarFieldType {
    @JsOverlay
    static SimpleDictionaryType.BarFieldType create() {
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

  @JsFunction
  public interface BazFn {
    @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
    public interface P0Type {
      @JsOverlay
      static SimpleDictionaryType.BazFn.P0Type create() {
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

    void onInvoke(SimpleDictionaryType.BazFn.P0Type p0);
  }

  @JsOverlay
  static SimpleDictionaryType create() {
    return Js.uncheckedCast(JsPropertyMap.of());
  }

  @JsProperty
  SimpleDictionaryType.BarFieldType getBar();

  @JsProperty
  SimpleDictionaryType.BazFn getBaz();

  @JsProperty
  double getFoo();

  @JsProperty
  void setBar(SimpleDictionaryType.BarFieldType bar);

  @JsProperty
  void setBaz(SimpleDictionaryType.BazFn baz);

  @JsProperty
  void setFoo(double foo);
}
