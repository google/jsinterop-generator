package jsinterop.generator.externs.inheritance;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class GreatParentClass extends JsArray<Double> {
  public double greatParentClassProperty;

  public GreatParentClass(String s, boolean b, double n) {
    // This call is only there for java compilation purpose.
    super((Double) null);
  }

  public native double greatParentClassMethod();
}
