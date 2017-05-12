package jsinterop.generator.externs.iobjectiarraylike;

import java.lang.Double;
import java.lang.String;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsArrayLike;
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
      JsPropertyMap<String> object, JsArrayLike<String> arrayLike);
}
