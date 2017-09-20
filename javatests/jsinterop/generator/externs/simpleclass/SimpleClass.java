package jsinterop.generator.externs.simpleclass;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class SimpleClass {
  public static double staticProperty;
  @JsOverlay
  public static final String staticReadonlyProperty = SimpleClass__Constants.staticReadonlyProperty;

  public static native boolean staticMethod(String foo, String bar, boolean baz);

  public static native boolean staticMethod(String foo, String bar);

  public Object fooProperty;
  public String[][][] fooProperty2;
  public boolean readonlyProperty;
  public SimpleClass thisType;

  public SimpleClass() {}

  public SimpleClass(String foo) {}

  public native boolean fooMethod(String foo, String bar, boolean baz);

  public native boolean fooMethod(String foo, String bar);
}
