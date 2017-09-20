package jsinterop.generator.externs.structuraltypes.simplemodule;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, name = "SimpleModule.ClassInModule", namespace = JsPackage.GLOBAL)
public class ClassInModule {
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface FooBazType {
    @JsOverlay
    static ClassInModule.FooBazType create() {
      return Js.uncheckedCast(JsPropertyMap.of());
    }

    @JsProperty
    double getBaz();

    @JsProperty
    void setBaz(double baz);
  }

  public native void foo(ClassInModule.FooBazType baz);
}
