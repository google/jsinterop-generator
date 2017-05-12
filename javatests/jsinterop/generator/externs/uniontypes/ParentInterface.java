package jsinterop.generator.externs.uniontypes;

import java.lang.Double;
import java.lang.Object;
import java.lang.String;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface ParentInterface<T> {
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface ParentMethodFooUnionType {
    @JsOverlay
    static ParentInterface.ParentMethodFooUnionType of(Object o) {
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

  Object parentMethod(ParentInterface.ParentMethodFooUnionType foo);

  @JsOverlay
  default Object parentMethod(String foo) {
    return parentMethod(Js.<ParentInterface.ParentMethodFooUnionType>uncheckedCast(foo));
  }

  @JsOverlay
  default Object parentMethod(double foo) {
    return parentMethod(Js.<ParentInterface.ParentMethodFooUnionType>uncheckedCast(foo));
  }
}
