/*
 * Copyright 2017 Google Inc.
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

package jsinterop.generator.visitor;

import jsinterop.generator.helper.ModelHelper;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Parameter;
import jsinterop.generator.model.PredefinedTypeReference;

/**
 * Creates a JsOverlay method that will accept java.lang.Object as parameter and delegate to
 * existing native method that accepts JavaScript object so the API becomes Java friendly.
 *
 * <p>We cannot convert native js Object reference to java.lang.Object because java.lang.Object
 * reference is transpiled by J2CL to any type '*'. That breaks closure type checking.
 */
public class ObjectParameterJsOverlayCreator extends AbstractJsOverlayMethodCreator {

  @Override
  protected boolean processMethod(Method method) {
    Method jsOverlayMethod =
        ModelHelper.createDelegatingOverlayMethod(
            method,
            ObjectParameterJsOverlayCreator::toJavaLangObject,
            ModelHelper::callUncheckedCast);

    if (jsOverlayMethod != null) {
      method.getEnclosingType().addMethod(jsOverlayMethod);
    }

    return false;
  }

  private static Parameter toJavaLangObject(int unusedIndex, Parameter parameter) {
    if ("Object".equals(parameter.getType().getJsDocAnnotationString())) {
      return parameter.toBuilder().setType(PredefinedTypeReference.OBJECT).build();
    }
    return parameter;
  }
}
