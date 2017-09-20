package jsinterop.generator.externs.uniontypes;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface Foo<T, V> {
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface GetFooParentInterfaceTypeParameterUnionType<T, V> {
    @JsOverlay
    static Foo.GetFooParentInterfaceTypeParameterUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default T asT() {
      return Js.cast(this);
    }

    @JsOverlay
    default V asV() {
      return Js.cast(this);
    }
  }

  @JsProperty
  ParentInterface<Foo.GetFooParentInterfaceTypeParameterUnionType<T, V>> getFoo();

  @JsProperty
  void setFoo(ParentInterface<Foo.GetFooParentInterfaceTypeParameterUnionType<T, V>> foo);
}
