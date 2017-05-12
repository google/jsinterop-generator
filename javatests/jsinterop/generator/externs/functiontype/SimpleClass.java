package jsinterop.generator.externs.functiontype;

import java.lang.String;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class SimpleClass {
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

  public AliasedFunctionType bar;
  public SimpleClass.FooCallbackFn foo;

  public native void method(SimpleClass.MethodFooCallbackFn foo);

  public native SimpleClass.Method1CallbackFn method1(String foo);
}
