package jsinterop.generator.externs.functiontype;

import java.lang.String;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class SimpleModule {
  @JsFunction
  public interface FooCallbackFn {
    boolean onInvoke(String p0);
  }

  @JsFunction
  public interface Method1CallbackFn {
    boolean onInvoke(String p0);
  }

  @JsFunction
  public interface MethodFooCallbackFn {
    boolean onInvoke(String p0);
  }

  public static AliasedFunctionType bar;
  public static SimpleModule.FooCallbackFn foo;

  public static native void method(SimpleModule.MethodFooCallbackFn foo);

  public static native SimpleModule.Method1CallbackFn method1(String foo);
}
