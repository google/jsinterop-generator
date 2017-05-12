package jsinterop.generator.externs.globalscope;

import java.lang.String;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "window", namespace = JsPackage.GLOBAL)
public class Global {
  @JsOverlay public static final double constantFoo = Global__Constants.constantFoo;
  public static String foo;

  public static native double bar(double bar, String foo, boolean baz);

  public static native double bar(double bar, String foo);

  public static native double bar(double bar);
}
