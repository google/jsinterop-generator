package jsinterop.generator.externs.structuraltypes;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "window", namespace = JsPackage.GLOBAL)
public class Global {
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface FooBarType {
    @JsProperty
    String getBar();

    @JsProperty
    void setBar(String bar);
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface Method1FooType {
    @JsFunction
    public interface FooCallbackFn {
      @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
      public interface P0Type {
        @JsProperty
        String getBar();

        @JsProperty
        void setBar(String bar);
      }

      void onInvoke(Global.Method1FooType.FooCallbackFn.P0Type p0);
    }

    @JsProperty
    Global.Method1FooType.FooCallbackFn getFoo();

    @JsProperty
    void setFoo(Global.Method1FooType.FooCallbackFn foo);
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface Method2FooType {
    @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
    public interface BarFieldType {
      @JsProperty
      String getFoo();

      @JsProperty
      void setFoo(String foo);
    }

    @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
    public interface FooFieldType {
      @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
      public interface BazFieldType {
        @JsProperty
        String getInsane();

        @JsProperty
        void setInsane(String insane);
      }

      @JsProperty
      Global.Method2FooType.FooFieldType.BazFieldType getBaz();

      @JsProperty
      void setBaz(Global.Method2FooType.FooFieldType.BazFieldType baz);
    }

    @JsProperty
    Global.Method2FooType.BarFieldType getBar();

    @JsProperty
    Global.Method2FooType.FooFieldType getFoo();

    @JsProperty
    void setBar(Global.Method2FooType.BarFieldType bar);

    @JsProperty
    void setFoo(Global.Method2FooType.FooFieldType foo);
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface Method3BarType {
    @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
    public interface BarFieldType {
      @JsProperty
      String getFoo();

      @JsProperty
      void setFoo(String foo);
    }

    @JsProperty
    Global.Method3BarType.BarFieldType getBar();

    @JsProperty
    void setBar(Global.Method3BarType.BarFieldType bar);
  }

  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  public interface Method3FooType {
    @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
    public interface BarFieldType {
      @JsProperty
      String getFoo();

      @JsProperty
      void setFoo(String foo);
    }

    @JsProperty
    Global.Method3FooType.BarFieldType getBar();

    @JsProperty
    String getFoo();

    @JsProperty
    void setBar(Global.Method3FooType.BarFieldType bar);

    @JsProperty
    void setFoo(String foo);
  }

  public static native void foo(Global.FooBarType bar);

  public static native void method1(Global.Method1FooType foo);

  public static native void method2(Global.Method2FooType foo);

  public static native void method3(Global.Method3FooType foo, Global.Method3BarType bar);
}
