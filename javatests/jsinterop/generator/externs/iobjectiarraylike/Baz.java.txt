package jsinterop.generator.externs.iobjectiarraylike;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsArrayLike;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface Baz<T> extends JsArrayLike<T>, JsPropertyMap<T> {}
