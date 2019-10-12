package jsinterop.generator.externs.iobjectiarraylike;

import java.lang.String;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class Foo implements JsPropertyMap<String> {}
