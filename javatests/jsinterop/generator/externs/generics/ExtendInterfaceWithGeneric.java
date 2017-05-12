package jsinterop.generator.externs.generics;

import java.lang.Double;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface ExtendInterfaceWithGeneric extends InterfaceWithGeneric<Double> {
  void bar(InterfaceWithGeneric<boolean[]> param);
}
