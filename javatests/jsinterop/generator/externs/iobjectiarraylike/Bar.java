package jsinterop.generator.externs.iobjectiarraylike;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class Bar implements JsArrayLike<String> {
  public JsArrayLike<String> iArrayLikeField;
  public JsPropertyMap<String> iObjectField;
  public JsPropertyMap<String> templatizedObject;
  public JsPropertyMap<Double> templatizedObjectWithTwoParameters;

  public native JsArrayLike<String> asIArrayLike();

  public native JsPropertyMap<String> asIObject();

  public native void consumeIObjectAndIArrayLike(
      JsPropertyMap<String> object,
      JsArrayLike<String> arrayLike,
      JsArrayLike<JsArrayLike<String>> doubleArrayLike);

  @JsOverlay
  public final void consumeIObjectAndIArrayLike(
      JsPropertyMap<String> object, String[] arrayLike, String[][] doubleArrayLike) {
    consumeIObjectAndIArrayLike(
        object,
        Js.<JsArrayLike<String>>uncheckedCast(arrayLike),
        Js.<JsArrayLike<JsArrayLike<String>>>uncheckedCast(doubleArrayLike));
  }

  @JsOverlay
  public final void consumeObjectIArrayLikeAndCtorFn(
      JsObject object, JsArrayLike<String> arrayLike, Class<Bar> ctor) {
    consumeObjectIArrayLikeAndCtorFn(object, arrayLike, Js.asConstructorFn(ctor));
  }

  public native void consumeObjectIArrayLikeAndCtorFn(
      JsObject object, JsArrayLike<String> arrayLike, JsConstructorFn<Bar> ctor);

  @JsOverlay
  public final void consumeObjectIArrayLikeAndCtorFn(
      JsObject object, String[] arrayLike, Class<Bar> ctor) {
    consumeObjectIArrayLikeAndCtorFn(
        object, Js.<JsArrayLike<String>>uncheckedCast(arrayLike), ctor);
  }

  @JsOverlay
  public final void consumeObjectIArrayLikeAndCtorFn(
      JsObject object, String[] arrayLike, JsConstructorFn<Bar> ctor) {
    consumeObjectIArrayLikeAndCtorFn(
        object, Js.<JsArrayLike<String>>uncheckedCast(arrayLike), ctor);
  }

  @JsOverlay
  public final void consumeObjectIArrayLikeAndCtorFn(
      Object object, JsArrayLike<String> arrayLike, Class<Bar> ctor) {
    consumeObjectIArrayLikeAndCtorFn(Js.<JsObject>uncheckedCast(object), arrayLike, ctor);
  }

  @JsOverlay
  public final void consumeObjectIArrayLikeAndCtorFn(
      Object object, JsArrayLike<String> arrayLike, JsConstructorFn<Bar> ctor) {
    consumeObjectIArrayLikeAndCtorFn(Js.<JsObject>uncheckedCast(object), arrayLike, ctor);
  }

  @JsOverlay
  public final void consumeObjectIArrayLikeAndCtorFn(
      Object object, String[] arrayLike, Class<Bar> ctor) {
    consumeObjectIArrayLikeAndCtorFn(Js.<JsObject>uncheckedCast(object), arrayLike, ctor);
  }

  @JsOverlay
  public final void consumeObjectIArrayLikeAndCtorFn(
      Object object, String[] arrayLike, JsConstructorFn<Bar> ctor) {
    consumeObjectIArrayLikeAndCtorFn(Js.<JsObject>uncheckedCast(object), arrayLike, ctor);
  }
}
