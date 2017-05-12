/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package jsinterop.generator.option;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/** Options used by the generator. */
@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
public class GeneratorOptions {
  @JsProperty
  public native String getOutputDirectory();

  @JsProperty
  public native String getPackagePrefix();

  @JsProperty
  public native String getCopyright();

  @JsProperty
  public native boolean isDebugEnabled();

  @JsProperty(name = "useBeanConvention")
  public native boolean useBeanConvention();

  @JsProperty(name = "withTypescriptLib")
  public native boolean withTypescriptLib();

  @JsProperty(name = "fromClutz")
  public native boolean fromClutz();

  @JsProperty(name = "depsTypesMapping")
  public native String[] depsTypesMapping();
}
