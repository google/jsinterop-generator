package jsinterop.generator.externs.functiontype;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class SimpleModule {
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

  public static AliasedFunctionType bar;
  public static SimpleModule.FooFn foo;

  public static native void method(SimpleModule.MethodFooCallbackFn fooCallback);

  public static native SimpleModule.Method1Fn method1(String foo);
}
