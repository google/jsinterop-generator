package jsinterop.generator.externs.inheritance;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface Parent2Interface {
  @JsProperty
  double getParent2InterfaceProperty();

  double parent2InterfaceMethod();

  @JsProperty
  void setParent2InterfaceProperty(double parent2InterfaceProperty);
}
