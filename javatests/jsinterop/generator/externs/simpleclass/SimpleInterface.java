package jsinterop.generator.externs.simpleclass;

import java.lang.Deprecated;
import java.lang.String;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface SimpleInterface {
  @Deprecated @JsOverlay boolean deprecatedStaticProperty = SimpleInterface__Constants.deprecatedStaticProperty;
  @JsOverlay String staticProperty = SimpleInterface__Constants.staticProperty;

  @Deprecated
  boolean deprecatedMethod(String bar, String foo, boolean baz);

  @Deprecated
  boolean deprecatedMethod(String bar, String foo);

  boolean fooMethod(String foo, String bar, boolean baz);

  boolean fooMethod(String foo, String bar);

  @Deprecated
  @JsProperty
  String getDeprecatedProperty();

  @JsProperty
  String getFooProperty();

  @JsProperty
  boolean isReadonlyProperty();

  @Deprecated
  @JsProperty
  void setDeprecatedProperty(String deprecatedProperty);

  @JsProperty
  void setFooProperty(String fooProperty);
}
