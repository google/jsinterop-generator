package jsinterop.generator.externs.inheritance;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface Parent1Interface extends GreatParentInterface {
  @JsProperty
  boolean isParent1InterfaceProperty();

  boolean parent1InterfaceMethod(double foo);

  @JsProperty
  void setParent1InterfaceProperty(boolean parent1InterfaceProperty);
}
