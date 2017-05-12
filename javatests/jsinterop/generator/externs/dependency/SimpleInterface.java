package jsinterop.generator.externs.dependency;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.generator.externs.dependency.thirdparty.ThirdPartyInterface;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface SimpleInterface extends ThirdPartyInterface {}
