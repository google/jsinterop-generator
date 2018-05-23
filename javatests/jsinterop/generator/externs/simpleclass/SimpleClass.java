package jsinterop.generator.externs.simpleclass;

import java.lang.Deprecated;
import java.lang.Double;
import java.lang.Object;
import java.lang.String;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class SimpleClass {
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface DeprecatedMethodFooUnionType {
    @JsOverlay
    static SimpleClass.DeprecatedMethodFooUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default double asDouble() {
      return Js.asDouble(this);
    }

    @JsOverlay
    default String asString() {
      return Js.asString(this);
    }

    @JsOverlay
    default boolean isDouble() {
      return (Object) this instanceof Double;
    }

    @JsOverlay
    default boolean isString() {
      return (Object) this instanceof String;
    }
  }

  @Deprecated
  @JsOverlay
  public static final String deprecatedConstant = SimpleClass__Constants.deprecatedConstant;
  @Deprecated public static String deprecatedStaticProperty;
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

  @Deprecated
  public String deprecatedProperty;
  public String fooProperty;
  public String[][][] fooProperty2;
  public boolean readonlyProperty;
  public SimpleClass thisType;

  public SimpleClass() {}

  public SimpleClass(String foo) {}


  @JsMethod(name = "clone")
  public native Object clone_();

  @Deprecated
  public native boolean deprecatedMethod(String bar, SimpleClass.DeprecatedMethodFooUnionType foo, JsObject baz);

  @JsOverlay
  @Deprecated
  public final boolean deprecatedMethod(String bar, SimpleClass.DeprecatedMethodFooUnionType foo, Object baz) {
    return deprecatedMethod(bar, foo, Js.<JsObject>uncheckedCast(baz));
  }

  @Deprecated
  public native boolean deprecatedMethod(String bar, SimpleClass.DeprecatedMethodFooUnionType foo);

  @JsOverlay
  @Deprecated
  public final boolean deprecatedMethod(String bar, String foo, JsObject baz) {
    return deprecatedMethod(
            bar, Js.<SimpleClass.DeprecatedMethodFooUnionType>uncheckedCast(foo), baz);
  }

  @JsOverlay
  @Deprecated
  public final boolean deprecatedMethod(String bar, String foo, Object baz) {
    return deprecatedMethod(bar, foo, Js.<JsObject>uncheckedCast(baz));
  }

  @JsOverlay
  @Deprecated
  public final boolean deprecatedMethod(String bar, String foo) {
    return deprecatedMethod(bar, Js.<SimpleClass.DeprecatedMethodFooUnionType>uncheckedCast(foo));
  }

  @JsOverlay
  @Deprecated
  public final boolean deprecatedMethod(String bar, double foo, JsObject baz) {
    return deprecatedMethod(
            bar, Js.<SimpleClass.DeprecatedMethodFooUnionType>uncheckedCast(foo), baz);
  }

  @JsOverlay
  @Deprecated
  public final boolean deprecatedMethod(String bar, double foo, Object baz) {
    return deprecatedMethod(bar, foo, Js.<JsObject>uncheckedCast(baz));
  }

  @JsOverlay
  @Deprecated
  public final boolean deprecatedMethod(String bar, double foo) {
    return deprecatedMethod(bar, Js.<SimpleClass.DeprecatedMethodFooUnionType>uncheckedCast(foo));
  }

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
