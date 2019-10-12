package jsinterop.generator.externs.entitiesrenaming;

import java.lang.Double;
import java.lang.Object;
import java.lang.String;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(isNative = true, name = "goog.global", namespace = JsPackage.GLOBAL)
public class Global {
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface FooFooUnionType {
    @JsOverlay
    static Global.FooFooUnionType of(Object o) {
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

  public static native Object foo(Global.FooFooUnionType foo);

  @JsOverlay
  public static final Object foo(String foo) {
    return foo(Js.<Global.FooFooUnionType>uncheckedCast(foo));
  }

  @JsOverlay
  public static final Object foo(double foo) {
    return foo(Js.<Global.FooFooUnionType>uncheckedCast(foo));
  }
}
