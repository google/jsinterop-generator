package jsinterop.generator.externs.inheritance;

import java.lang.String;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class ParentClass extends GreatParentClass {
  public double parentClassProperty;

  public ParentClass() {
    // This call is only there for java compilation purpose.
    super((String) null, false, 0);
  }

  public native double parentClassMethod();
}
