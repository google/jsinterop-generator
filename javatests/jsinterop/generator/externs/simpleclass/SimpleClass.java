package jsinterop.generator.externs.simpleclass;

import java.lang.Object;
import java.lang.String;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class SimpleClass {
  public static double staticProperty;

  @JsOverlay
  public static final String staticReadonlyProperty = SimpleClass__Constants.staticReadonlyProperty;

  @JsMethod(name = "clone")
  public static native Object clone__STATIC();

  @JsMethod(name = "equals")
  public static native boolean equals__STATIC(Object other);

  @JsMethod(name = "getClass")
  public static native String getClass__STATIC();

  @JsMethod(name = "hashCode")
  public static native double hashCode__STATIC();

  @JsMethod(name = "notifyAll")
  public static native void notifyAll__STATIC();

  @JsMethod(name = "notify")
  public static native void notify__STATIC();

  public static native boolean staticMethod(String foo, String bar, boolean baz);

  public static native boolean staticMethod(String foo, String bar);

  @JsMethod(name = "toString")
  public static native String toString__STATIC();

  @JsMethod(name = "wait")
  public static native void wait__STATIC();

  public String fooProperty;
  public String[][][] fooProperty2;
  public boolean readonlyProperty;
  public SimpleClass thisType;

  public SimpleClass() {}

  public SimpleClass(String foo) {}

  @JsMethod(name = "clone")
  public native Object clone_();

  @JsMethod(name = "equals")
  public native boolean equals_(Object other);

  public native boolean fooMethod(String foo, String bar, JsObject baz);

  @JsOverlay
  public final boolean fooMethod(String foo, String bar, Object baz) {
    return fooMethod(foo, bar, Js.<JsObject>uncheckedCast(baz));
  }
  
  public native boolean fooMethod(String foo, String bar);

  @JsMethod(name = "getClass")
  public native String getClass_();

  @JsMethod(name = "hashCode")
  public native double hashCode_();

  @JsMethod(name = "notifyAll")
  public native void notifyAll_();

  @JsMethod(name = "notify")
  public native void notify_();

  @JsMethod(name = "toString")
  public native String toString_();

  @JsMethod(name = "wait")
  public native void wait_();
}
