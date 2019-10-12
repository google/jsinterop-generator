package jsinterop.generator.externs.functiontype;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface SimpleInterface {
  @JsFunction
  public interface FooFn {
    boolean onInvoke(String p0);
  }

  @JsFunction
  public interface Method1Fn {
    boolean onInvoke(String p0);
  }

  @JsFunction
  public interface MethodFooCallbackFn {
    boolean onInvoke(String p0);
  }

  @JsFunction
  public interface WithUnionTypeFooCallbackFn {
    @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
    public interface P0UnionType {
      @JsOverlay
      static SimpleInterface.WithUnionTypeFooCallbackFn.P0UnionType of(Object o) {
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

    boolean onInvoke(SimpleInterface.WithUnionTypeFooCallbackFn.P0UnionType p0);

    @JsOverlay
    default boolean onInvoke(String p0) {
      return onInvoke(Js.<SimpleInterface.WithUnionTypeFooCallbackFn.P0UnionType>uncheckedCast(p0));
    }

    @JsOverlay
    default boolean onInvoke(double p0) {
      return onInvoke(Js.<SimpleInterface.WithUnionTypeFooCallbackFn.P0UnionType>uncheckedCast(p0));
    }
  }

  @JsProperty
  AliasedFunctionType getBar();

  @JsProperty
  SimpleInterface.FooFn getFoo();

  void method(SimpleInterface.MethodFooCallbackFn fooCallback);

  SimpleInterface.Method1Fn method1(String foo);

  @JsProperty
  void setBar(AliasedFunctionType bar);

  @JsProperty
  void setFoo(SimpleInterface.FooFn foo);

  void withUnionType(SimpleInterface.WithUnionTypeFooCallbackFn fooCallback);
}
