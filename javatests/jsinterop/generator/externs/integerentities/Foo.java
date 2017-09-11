package jsinterop.generator.externs.integerentities;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class Foo {
  @JsFunction
  public interface FooCallbackFn {
    void onInvoke(int foo);
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface UnionUnionParamUnionType {
    @JsOverlay
    static Foo.UnionUnionParamUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default int asInt() {
      return Js.asInt(this);
    }

    @JsOverlay
    default String asString() {
      return Js.asString(this);
    }

    @JsOverlay
    default boolean isInt() {
      return (Object) this instanceof Double;
    }

    @JsOverlay
    default boolean isString() {
      return (Object) this instanceof String;
    }
  }

  public static int baz;
  public int bar;

  public native int foo(int integerParam, double doubleParam, Foo.FooCallbackFn callback);

  public native void methodWithOptionalParameter(int param1, double optional);

  public native void methodWithOptionalParameter(int param1);

  @JsOverlay
  public final Object union(String unionParam) {
    return union(Js.<Foo.UnionUnionParamUnionType>uncheckedCast(unionParam));
  }

  public native Object union(Foo.UnionUnionParamUnionType unionParam);

  @JsOverlay
  public final Object union(int unionParam) {
    return union(Js.<Foo.UnionUnionParamUnionType>uncheckedCast(unionParam));
  }
}
