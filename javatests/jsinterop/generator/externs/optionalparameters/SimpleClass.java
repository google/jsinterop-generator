package jsinterop.generator.externs.optionalparameters;

import java.lang.String;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class SimpleClass {
  public SimpleClass() {}

  public SimpleClass(String foo) {}

  public native void foo(String foo, String bar, String baz);

  public native void foo(String foo, String bar);

  public native void foo(String foo);
}
