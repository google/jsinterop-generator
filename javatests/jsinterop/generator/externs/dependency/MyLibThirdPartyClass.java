package jsinterop.generator.externs.dependency;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import jsinterop.generator.externs.dependency.thirdparty.ThirdPartyClass;
import jsinterop.generator.externs.dependency.thirdparty2.ThirdParty2Class;

@JsType(isNative = true, name = "ThirdPartyClass", namespace = JsPackage.GLOBAL)
public class MyLibThirdPartyClass<T> extends ThirdPartyClass<T> {
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface ExtraMethodBarType<T> {
    @JsOverlay
    static MyLibThirdPartyClass.ExtraMethodBarType create() {
      return Js.uncheckedCast(JsPropertyMap.of());
    }

    @JsProperty
    T getBar();

    @JsProperty
    void setBar(T bar);
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface ExtraMethodBarUnionType<T> {
    @JsOverlay
    static MyLibThirdPartyClass.ExtraMethodBarUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default MyLibThirdPartyClass.ExtraMethodBarType<T> asExtraMethodBarType() {
      return Js.cast(this);
    }

    @JsOverlay
    default T asT() {
      return Js.cast(this);
    }
  }

  @JsOverlay
  public static MyLibThirdPartyClass of(ThirdPartyClass o) {
    return Js.cast(o);
  }

  public ThirdParty2Class extraField;

  public MyLibThirdPartyClass(ThirdPartyClass.ConstructorFooUnionType foo) {
    super((ThirdPartyClass.ConstructorFooUnionType) null);
  }

  public MyLibThirdPartyClass(String foo) {
    super((ThirdPartyClass.ConstructorFooUnionType) null);
  }

  public MyLibThirdPartyClass(double foo) {
    super((ThirdPartyClass.ConstructorFooUnionType) null);
  }

  @JsOverlay
  public final void extraMethod(T foo, MyLibThirdPartyClass.ExtraMethodBarType<T> bar) {
    extraMethod(foo, Js.<MyLibThirdPartyClass.ExtraMethodBarUnionType<T>>uncheckedCast(bar));
  }

  public native void extraMethod(T foo, MyLibThirdPartyClass.ExtraMethodBarUnionType<T> bar);

  @JsOverlay
  public final void extraMethod(T foo, T bar) {
    extraMethod(foo, Js.<MyLibThirdPartyClass.ExtraMethodBarUnionType<T>>uncheckedCast(bar));
  }
}
