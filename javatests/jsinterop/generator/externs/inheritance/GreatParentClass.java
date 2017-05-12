package jsinterop.generator.externs.inheritance;

import java.lang.Double;
import java.lang.Object;
import java.lang.String;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class GreatParentClass extends Array<Double> {
  public double greatParentClassProperty;

  public GreatParentClass(String s, boolean b, double n) {
    // This call is only there for java compilation purpose.
    super((Object) null);
  }

  public native double greatParentClassMethod();
}
