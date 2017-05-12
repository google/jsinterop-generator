package jsinterop.generator.externs.structuraltypes.simplemodule;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "SimpleModule.ClassInModule", namespace = JsPackage.GLOBAL)
public class ClassInModule {
  @JsType
  public interface FooBazType {
    @JsProperty
    double getBaz();

    @JsProperty
    void setBaz(double baz);
  }

  public native void foo(ClassInModule.FooBazType baz);
}
