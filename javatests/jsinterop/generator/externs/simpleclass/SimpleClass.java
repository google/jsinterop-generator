package jsinterop.generator.externs.simpleclass;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class SimpleClass {
  public static double staticProperty;

  @JsOverlay
  public static final String staticReadonlyProperty = SimpleClass__Constants.staticReadonlyProperty;

  public static native boolean staticMethod(String foo, String bar, boolean baz);

  public static native boolean staticMethod(String foo, String bar);

  public String fooProperty;
  public String[][][] fooProperty2;
  public boolean readonlyProperty;
  public SimpleClass thisType;

  public SimpleClass() {}

  public SimpleClass(String foo) {}

  public native boolean fooMethod(String foo, String bar, JsObject baz);

  @JsOverlay
  public final boolean fooMethod(String foo, String bar, Object baz) {
    return fooMethod(foo, bar, Js.<JsObject>uncheckedCast(baz));
  }

  public native boolean fooMethod(String foo, String bar);
}
