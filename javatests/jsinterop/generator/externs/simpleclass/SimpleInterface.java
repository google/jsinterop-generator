package jsinterop.generator.externs.simpleclass;

import java.lang.String;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface SimpleInterface {
  @JsOverlay String staticProperty = SimpleInterface__Constants.staticProperty;
  boolean fooMethod(String foo, String bar, boolean baz);

  boolean fooMethod(String foo, String bar);

  @JsProperty
  String getFooProperty();

  @JsProperty
  boolean isReadonlyProperty();

  @JsProperty
  void setFooProperty(String fooProperty);
}
