package jsinterop.generator.externs.entitiesrenaming;

import java.lang.String;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface SimpleInterface {
  @JsFunction
  public interface MethodFooCallbackFn {
    @JsFunction
    public interface ValueCallbackFn {
      void onInvoke(String baz);
    }

    boolean onInvoke(SimpleInterface.MethodFooCallbackFn.ValueCallbackFn value);
  }

  void method(SimpleInterface.MethodFooCallbackFn foo);
}
