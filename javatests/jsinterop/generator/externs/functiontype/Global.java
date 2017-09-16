package jsinterop.generator.externs.functiontype;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsConstructorFn;

@JsType(isNative = true, name = "window", namespace = JsPackage.GLOBAL)
public class Global {
  @JsFunction
  public interface BazCallbackFn {
    boolean onInvoke(String p0);
  }

  @JsFunction
  public interface FooCallbackFn {
    boolean onInvoke(String p0);
  }

  @JsFunction
  public interface Method1CallbackFn {
    boolean onInvoke(String p0);
  }

  @JsFunction
  public interface Method2FooCallbackFn {
    Object onInvoke(String p0);
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface Method2FooUnionType {
    @JsOverlay
    static Global.Method2FooUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default Global.Method2FooCallbackFn asMethod2FooCallbackFn() {
      return Js.cast(this);
    }

    @JsOverlay
    default String asString() {
      return Js.asString(this);
    }

    @JsOverlay
    default boolean isMethod2FooCallbackFn() {
      return (Object) this instanceof Global.Method2FooCallbackFn;
    }

    @JsOverlay
    default boolean isString() {
      return (Object) this instanceof String;
    }
  }

  @JsFunction
  public interface Method3FooCallbackFn {
    @JsFunction
    public interface P0CallbackFn {
      boolean onInvoke();
    }

    String onInvoke(Global.Method3FooCallbackFn.P0CallbackFn p0);
  }

  @JsFunction
  public interface Method4FooCallbackFn {
    Object onInvoke();
  }

  @JsFunction
  public interface Method5FooCallbackFn {
    @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
    public interface P0Type {
      @JsProperty
      String getFoo();

      @JsProperty
      void setFoo(String foo);
    }

    @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
    public interface ReturnType {
      @JsProperty
      double getFoo();

      @JsProperty
      void setFoo(double foo);
    }

    Global.Method5FooCallbackFn.ReturnType onInvoke(Global.Method5FooCallbackFn.P0Type p0);
  }

  @JsFunction
  public interface Method6FooCallbackFn {
    @JsFunction
    public interface CallbackFn {
      double onInvoke();
    }

    @JsFunction
    public interface P0CallbackFn {
      boolean onInvoke();
    }

    @JsFunction
    public interface P1CallbackFn {
      String onInvoke();
    }

    Global.Method6FooCallbackFn.CallbackFn onInvoke(
        Global.Method6FooCallbackFn.P0CallbackFn p0, Global.Method6FooCallbackFn.P1CallbackFn p1);
  }

  @JsFunction
  public interface Method7BarCallbackFn {
    @JsFunction
    public interface P0CallbackFn {
      double onInvoke();
    }

    void onInvoke(Global.Method7BarCallbackFn.P0CallbackFn p0);
  }

  @JsFunction
  public interface Method7FooCallbackFn {
    @JsFunction
    public interface P0CallbackFn {
      boolean onInvoke();
    }

    void onInvoke(Global.Method7FooCallbackFn.P0CallbackFn p0);
  }

  @JsFunction
  public interface MethodFooCallbackFn {
    boolean onInvoke(String p0);
  }

  public static AliasedFunctionType bar;
  public static Global.BazCallbackFn[] baz;
  public static Global.FooCallbackFn foo;
  public static JsConstructorFn<SimpleClass> simpleClassCtor;

  public static native void method(
      Global.MethodFooCallbackFn foo, JsConstructorFn<SimpleClass> ctor);

  public static native Global.Method1CallbackFn method1(String foo);

  @JsOverlay
  public static final void method2(Global.Method2FooCallbackFn foo) {
    method2(Js.<Global.Method2FooUnionType>uncheckedCast(foo));
  }

  public static native void method2(Global.Method2FooUnionType foo);

  @JsOverlay
  public static final void method2(String foo) {
    method2(Js.<Global.Method2FooUnionType>uncheckedCast(foo));
  }

  public static native void method3(Global.Method3FooCallbackFn foo);

  public static native void method4(Global.Method4FooCallbackFn fooCallback);

  public static native void method5(Global.Method5FooCallbackFn fooCallback);

  public static native void method6(Global.Method6FooCallbackFn fooCallback);

  public static native void method7(
      Global.Method7FooCallbackFn fooCallback, Global.Method7BarCallbackFn barCallback);
}
