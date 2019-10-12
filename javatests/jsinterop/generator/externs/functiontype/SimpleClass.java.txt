package jsinterop.generator.externs.functiontype;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class SimpleClass {
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

  public AliasedFunctionType bar;
  public SimpleClass.FooFn foo;

  public native void method(SimpleClass.MethodFooCallbackFn fooCallback);

  public native SimpleClass.Method1Fn method1(String foo);
}
