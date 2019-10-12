package jsinterop.generator.externs.optionalparameters;

import java.lang.String;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface SimpleInterface {
  void foo();

  void foo(String foo, String bar, String baz);

  void foo(String foo, String bar);

  void foo(String foo);
}
