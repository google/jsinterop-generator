package jsinterop.generator.externs.structuraltypes;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class InnerStructuralType {
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface BarFieldType {
    @JsProperty
    String getBaz();

    @JsProperty
    void setBaz(String baz);
  }

  public InnerStructuralType.BarFieldType bar;
  public double foo;
}
