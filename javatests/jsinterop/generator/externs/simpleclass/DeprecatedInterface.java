package jsinterop.generator.externs.simpleclass;

import java.lang.Deprecated;
import java.lang.String;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
@Deprecated
public interface DeprecatedInterface {
  boolean deprecatedMethod(String bar, String foo, boolean baz);

  boolean deprecatedMethod(String bar, String foo);
}
