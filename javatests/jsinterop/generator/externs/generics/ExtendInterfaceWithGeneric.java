package jsinterop.generator.externs.generics;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.generator.externs.natives.JsArray;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface ExtendInterfaceWithGeneric extends InterfaceWithGeneric<Double> {
  void bar(InterfaceWithGeneric<JsArray<Boolean>> param);
}
