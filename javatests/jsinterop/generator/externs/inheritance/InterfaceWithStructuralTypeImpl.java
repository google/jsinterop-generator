package jsinterop.generator.externs.inheritance;

import java.lang.String;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class InterfaceWithStructuralTypeImpl<V, U> implements InterfaceWithStructuralType<V, U> {
  @JsFunction
  public interface BarCallbackFn {
    void onInvoke(boolean p0);
  }

  @JsType
  public interface FooFooType {
    @JsProperty
    String getFoo();

    @JsProperty
    void setFoo(String foo);
  }

  @JsType
  public interface FooReturnType {
    @JsProperty
    double getBar();

    @JsProperty
    void setBar(double bar);
  }

  @JsProperty(name = "bar")
  public static InterfaceWithStructuralTypeImpl.BarCallbackFn bar_STATIC;

  public static native InterfaceWithStructuralTypeImpl.FooReturnType foo(
      InterfaceWithStructuralTypeImpl.FooFooType[][] foo);

  public InterfaceWithStructuralType.BarCallbackFn bar;

  public native void bar2(InterfaceWithStructuralType.Bar2BarUnionType<V, U> bar);

  public native <U> void bar3(InterfaceWithStructuralType.Bar3Param1UnionType<U> param1, U param2);

  public native U baz(
      InterfaceWithStructuralType.BazBazType<V, U> baz,
      InterfaceWithStructuralType.BazBaz2Type<V, U> baz2);

  public native InterfaceWithStructuralType.FooReturnType foo(
      InterfaceWithStructuralType.FooFooType[][] foo);

  @JsProperty
  public native InterfaceWithStructuralType.BarCallbackFn getBar();

  @JsProperty
  public native void setBar(InterfaceWithStructuralType.BarCallbackFn bar);
}
