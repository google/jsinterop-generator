package jsinterop.generator.externs.uniontypes;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class Child implements ParentInterface<Child.ParentInterfaceTypeParameterUnionType> {
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface ConstructorFooUnionType {
    @JsOverlay
    static Child.ConstructorFooUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default double asDouble() {
      return Js.asDouble(this);
    }

    @JsOverlay
    default String asString() {
      return Js.asString(this);
    }

    @JsOverlay
    default boolean isDouble() {
      return (Object) this instanceof Double;
    }

    @JsOverlay
    default boolean isString() {
      return (Object) this instanceof String;
    }
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface Method1BarUnionType {
    @JsOverlay
    static Child.Method1BarUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default boolean asBoolean() {
      return Js.asBoolean(this);
    }

    @JsOverlay
    default double asDouble() {
      return Js.asDouble(this);
    }

    @JsOverlay
    default String asString() {
      return Js.asString(this);
    }

    @JsOverlay
    default boolean isBoolean() {
      return (Object) this instanceof Boolean;
    }

    @JsOverlay
    default boolean isDouble() {
      return (Object) this instanceof Double;
    }

    @JsOverlay
    default boolean isString() {
      return (Object) this instanceof String;
    }
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface Method1FooUnionType {
    @JsOverlay
    static Child.Method1FooUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default Child asChild() {
      return Js.cast(this);
    }

    @JsOverlay
    default double asDouble() {
      return Js.asDouble(this);
    }

    @JsOverlay
    default String asString() {
      return Js.asString(this);
    }

    @JsOverlay
    default boolean isChild() {
      return (Object) this instanceof Child;
    }

    @JsOverlay
    default boolean isDouble() {
      return (Object) this instanceof Double;
    }

    @JsOverlay
    default boolean isString() {
      return (Object) this instanceof String;
    }
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface Method2FooUnionType {
    @JsOverlay
    static Child.Method2FooUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default Child asChild() {
      return Js.cast(this);
    }

    @JsOverlay
    default double asDouble() {
      return Js.asDouble(this);
    }

    @JsOverlay
    default String asString() {
      return Js.asString(this);
    }

    @JsOverlay
    default boolean isChild() {
      return (Object) this instanceof Child;
    }

    @JsOverlay
    default boolean isDouble() {
      return (Object) this instanceof Double;
    }

    @JsOverlay
    default boolean isString() {
      return (Object) this instanceof String;
    }
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface Method2UnionType {
    @JsOverlay
    static Child.Method2UnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default boolean asBoolean() {
      return Js.asBoolean(this);
    }

    @JsOverlay
    default double asDouble() {
      return Js.asDouble(this);
    }

    @JsOverlay
    default String asString() {
      return Js.asString(this);
    }

    @JsOverlay
    default boolean isBoolean() {
      return (Object) this instanceof Boolean;
    }

    @JsOverlay
    default boolean isDouble() {
      return (Object) this instanceof Double;
    }

    @JsOverlay
    default boolean isString() {
      return (Object) this instanceof String;
    }
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface Method3FooArrayUnionType {
    @JsOverlay
    static Child.Method3FooArrayUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default Child asChild() {
      return Js.cast(this);
    }

    @JsOverlay
    default double asDouble() {
      return Js.asDouble(this);
    }

    @JsOverlay
    default boolean isChild() {
      return (Object) this instanceof Child;
    }

    @JsOverlay
    default boolean isDouble() {
      return (Object) this instanceof Double;
    }
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface Method3FooFooTypeParameterUnionType {
    @JsOverlay
    static Child.Method3FooFooTypeParameterUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default Double asDouble() {
      return Js.cast(this);
    }

    @JsOverlay
    default String asString() {
      return Js.asString(this);
    }

    @JsOverlay
    default boolean isDouble() {
      return (Object) this instanceof Double;
    }

    @JsOverlay
    default boolean isString() {
      return (Object) this instanceof String;
    }
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface Method3FooUnionType {
    @JsOverlay
    static Child.Method3FooUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default Foo<Child.Method3FooFooTypeParameterUnionType, String> asFoo() {
      return Js.cast(this);
    }

    @JsOverlay
    default Child.Method3FooArrayUnionType[] asMethod3FooArrayUnionTypeArray() {
      return Js.cast(this);
    }

    @JsOverlay
    default String asString() {
      return Js.asString(this);
    }

    @JsOverlay
    default boolean isMethod3FooArrayUnionTypeArray() {
      return (Object) this instanceof Object[];
    }

    @JsOverlay
    default boolean isString() {
      return (Object) this instanceof String;
    }
  }

  @JsFunction
  public interface Method4BarCallbackFn<T, V> {
    @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
    public interface P0UnionType<T, V> {
      @JsOverlay
      static Child.Method4BarCallbackFn.P0UnionType of(Object o) {
        return Js.cast(o);
      }

      @JsOverlay
      default T asT() {
        return Js.cast(this);
      }

      @JsOverlay
      default V asV() {
        return Js.cast(this);
      }
    }

    boolean onInvoke(Child.Method4BarCallbackFn.P0UnionType<T, V> p0);
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface Method4FooUnionType<T, V> {
    @JsOverlay
    static Child.Method4FooUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default T asT() {
      return Js.cast(this);
    }

    @JsOverlay
    default V asV() {
      return Js.cast(this);
    }
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface MethodUnionType {
    @JsOverlay
    static Child.MethodUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default Child asChild() {
      return Js.cast(this);
    }

    @JsOverlay
    default double asDouble() {
      return Js.asDouble(this);
    }

    @JsOverlay
    default String asString() {
      return Js.asString(this);
    }

    @JsOverlay
    default boolean isChild() {
      return (Object) this instanceof Child;
    }

    @JsOverlay
    default boolean isDouble() {
      return (Object) this instanceof Double;
    }

    @JsOverlay
    default boolean isString() {
      return (Object) this instanceof String;
    }
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface ParentInterfaceTypeParameterUnionType {
    @JsOverlay
    static Child.ParentInterfaceTypeParameterUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default Double asDouble() {
      return Js.cast(this);
    }

    @JsOverlay
    default String asString() {
      return Js.asString(this);
    }

    @JsOverlay
    default boolean isDouble() {
      return (Object) this instanceof Double;
    }

    @JsOverlay
    default boolean isString() {
      return (Object) this instanceof String;
    }
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface ParentMethod2BarUnionType {
    @JsOverlay
    static Child.ParentMethod2BarUnionType of(Object o) {
      return Js.cast(o);
    }

    @JsOverlay
    default boolean asBoolean() {
      return Js.asBoolean(this);
    }

    @JsOverlay
    default String asString() {
      return Js.asString(this);
    }

    @JsOverlay
    default boolean isBoolean() {
      return (Object) this instanceof Boolean;
    }

    @JsOverlay
    default boolean isString() {
      return (Object) this instanceof String;
    }
  }

  public Child(Child.ConstructorFooUnionType foo) {}

  public Child(String foo) {}

  public Child(double foo) {}

  public native Child.MethodUnionType method();

  @JsOverlay
  public final Object method1(Child foo, Child.Method1BarUnionType bar, boolean baz) {
    return method1(Js.<Child.Method1FooUnionType>uncheckedCast(foo), bar, baz);
  }

  @JsOverlay
  public final Object method1(Child foo, String bar, boolean baz) {
    return method1(
        Js.<Child.Method1FooUnionType>uncheckedCast(foo),
        Js.<Child.Method1BarUnionType>uncheckedCast(bar),
        baz);
  }

  @JsOverlay
  public final Object method1(Child foo, boolean bar, boolean baz) {
    return method1(
        Js.<Child.Method1FooUnionType>uncheckedCast(foo),
        Js.<Child.Method1BarUnionType>uncheckedCast(bar),
        baz);
  }

  @JsOverlay
  public final Object method1(Child foo, double bar, boolean baz) {
    return method1(
        Js.<Child.Method1FooUnionType>uncheckedCast(foo),
        Js.<Child.Method1BarUnionType>uncheckedCast(bar),
        baz);
  }

  public native Object method1(
      Child.Method1FooUnionType foo, Child.Method1BarUnionType bar, boolean baz);

  @JsOverlay
  public final Object method1(Child.Method1FooUnionType foo, String bar, boolean baz) {
    return method1(foo, Js.<Child.Method1BarUnionType>uncheckedCast(bar), baz);
  }

  @JsOverlay
  public final Object method1(Child.Method1FooUnionType foo, boolean bar, boolean baz) {
    return method1(foo, Js.<Child.Method1BarUnionType>uncheckedCast(bar), baz);
  }

  @JsOverlay
  public final Object method1(Child.Method1FooUnionType foo, double bar, boolean baz) {
    return method1(foo, Js.<Child.Method1BarUnionType>uncheckedCast(bar), baz);
  }

  @JsOverlay
  public final Object method1(String foo, Child.Method1BarUnionType bar, boolean baz) {
    return method1(Js.<Child.Method1FooUnionType>uncheckedCast(foo), bar, baz);
  }

  @JsOverlay
  public final Object method1(String foo, String bar, boolean baz) {
    return method1(
        Js.<Child.Method1FooUnionType>uncheckedCast(foo),
        Js.<Child.Method1BarUnionType>uncheckedCast(bar),
        baz);
  }

  @JsOverlay
  public final Object method1(String foo, boolean bar, boolean baz) {
    return method1(
        Js.<Child.Method1FooUnionType>uncheckedCast(foo),
        Js.<Child.Method1BarUnionType>uncheckedCast(bar),
        baz);
  }

  @JsOverlay
  public final Object method1(String foo, double bar, boolean baz) {
    return method1(
        Js.<Child.Method1FooUnionType>uncheckedCast(foo),
        Js.<Child.Method1BarUnionType>uncheckedCast(bar),
        baz);
  }

  @JsOverlay
  public final Object method1(double foo, Child.Method1BarUnionType bar, boolean baz) {
    return method1(Js.<Child.Method1FooUnionType>uncheckedCast(foo), bar, baz);
  }

  @JsOverlay
  public final Object method1(double foo, String bar, boolean baz) {
    return method1(
        Js.<Child.Method1FooUnionType>uncheckedCast(foo),
        Js.<Child.Method1BarUnionType>uncheckedCast(bar),
        baz);
  }

  @JsOverlay
  public final Object method1(double foo, boolean bar, boolean baz) {
    return method1(
        Js.<Child.Method1FooUnionType>uncheckedCast(foo),
        Js.<Child.Method1BarUnionType>uncheckedCast(bar),
        baz);
  }

  @JsOverlay
  public final Object method1(double foo, double bar, boolean baz) {
    return method1(
        Js.<Child.Method1FooUnionType>uncheckedCast(foo),
        Js.<Child.Method1BarUnionType>uncheckedCast(bar),
        baz);
  }

  @JsOverlay
  public final Child.Method2UnionType method2(Child foo) {
    return method2(Js.<Child.Method2FooUnionType>uncheckedCast(foo));
  }

  public native Child.Method2UnionType method2(Child.Method2FooUnionType foo);

  @JsOverlay
  public final Child.Method2UnionType method2(String foo) {
    return method2(Js.<Child.Method2FooUnionType>uncheckedCast(foo));
  }

  @JsOverlay
  public final Child.Method2UnionType method2(double foo) {
    return method2(Js.<Child.Method2FooUnionType>uncheckedCast(foo));
  }

  @JsOverlay
  public final void method3(Foo<Child.Method3FooFooTypeParameterUnionType, String> foo) {
    method3(Js.<Child.Method3FooUnionType>uncheckedCast(foo));
  }

  @JsOverlay
  public final void method3(Child.Method3FooArrayUnionType[] foo) {
    method3(Js.<Child.Method3FooUnionType>uncheckedCast(foo));
  }

  public native void method3(Child.Method3FooUnionType foo);

  @JsOverlay
  public final void method3(String foo) {
    method3(Js.<Child.Method3FooUnionType>uncheckedCast(foo));
  }

  public native <T, V> V method4(
      Child.Method4FooUnionType<T, V> foo,
      Child.Method4BarCallbackFn<? super T, ? super V> barCallback);

  public native Object parentMethod(ParentInterface.ParentMethodFooUnionType foo);

  public native Object parentMethod2(
      ParentInterface.ParentMethod2FooUnionType foo, Child.ParentMethod2BarUnionType bar);

  @JsOverlay
  public final Object parentMethod2(ParentInterface.ParentMethod2FooUnionType foo, String bar) {
    return parentMethod2(foo, Js.<Child.ParentMethod2BarUnionType>uncheckedCast(bar));
  }

  @JsOverlay
  public final Object parentMethod2(ParentInterface.ParentMethod2FooUnionType foo, boolean bar) {
    return parentMethod2(foo, Js.<Child.ParentMethod2BarUnionType>uncheckedCast(bar));
  }

  public native Object parentMethod2(ParentInterface.ParentMethod2FooUnionType foo);

  @JsOverlay
  public final Object parentMethod2(String foo, Child.ParentMethod2BarUnionType bar) {
    return parentMethod2(Js.<ParentInterface.ParentMethod2FooUnionType>uncheckedCast(foo), bar);
  }

  @JsOverlay
  public final Object parentMethod2(String foo, String bar) {
    return parentMethod2(
        Js.<ParentInterface.ParentMethod2FooUnionType>uncheckedCast(foo),
        Js.<Child.ParentMethod2BarUnionType>uncheckedCast(bar));
  }

  @JsOverlay
  public final Object parentMethod2(String foo, boolean bar) {
    return parentMethod2(
        Js.<ParentInterface.ParentMethod2FooUnionType>uncheckedCast(foo),
        Js.<Child.ParentMethod2BarUnionType>uncheckedCast(bar));
  }

  @JsOverlay
  public final Object parentMethod2(double foo, Child.ParentMethod2BarUnionType bar) {
    return parentMethod2(Js.<ParentInterface.ParentMethod2FooUnionType>uncheckedCast(foo), bar);
  }

  @JsOverlay
  public final Object parentMethod2(double foo, String bar) {
    return parentMethod2(
        Js.<ParentInterface.ParentMethod2FooUnionType>uncheckedCast(foo),
        Js.<Child.ParentMethod2BarUnionType>uncheckedCast(bar));
  }

  @JsOverlay
  public final Object parentMethod2(double foo, boolean bar) {
    return parentMethod2(
        Js.<ParentInterface.ParentMethod2FooUnionType>uncheckedCast(foo),
        Js.<Child.ParentMethod2BarUnionType>uncheckedCast(bar));
  }
}
