package jsinterop.generator.externs.dependency;

import java.lang.String;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "window", namespace = JsPackage.GLOBAL)
public class MyLibGlobal {
  public static native String foo();
}
