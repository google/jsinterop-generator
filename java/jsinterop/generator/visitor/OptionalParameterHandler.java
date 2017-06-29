/*
 * Copyright 2016 Google Inc.
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

import jsinterop.generator.model.AnnotationType;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Method.Parameter;
import jsinterop.generator.model.Type;

/**
 * Creates method overloading for each optional parameter. ex:
 *
 * <pre>
 *   void foo(int param1, String param2, int optionalParam1, String optionalParam2)
 *
 *   is decomposed to:
 *
 *   void foo(int param1, String param2)
 *   void foo(int param1, String param2, int optionalParam1)
 *   void foo(int param1, String param2, int optionalParam1, String optionalParam2)
 * </pre>
 */
public class OptionalParameterHandler extends AbstractModelVisitor {
  @Override
  public boolean visit(Type type) {
    // don't create method overloading on functional interface.
    // TODO(b/36140220): create method overloading with default methods for JsFunction type
    return !type.hasAnnotation(AnnotationType.JS_FUNCTION);
  }

  @Override
  public boolean visit(Method method) {
    int parameterCount = method.getParameters().size();
    for (int i = parameterCount - 1; i >= 0; i--) {
      Parameter currentParameter = method.getParameters().get(i);
      if (currentParameter.isOptional()) {
        method.getEnclosingType().addMethod(createOverloadMethod(method, i));
      } else {
        // if a non optional parameter is found, the others are non optional too
        return false;
      }
    }

    return false;
  }

  private Method createOverloadMethod(Method method, int parameterCount) {
    Method overload = Method.from(method);

    overload.clearParameters();

    for (int i = 0; i < parameterCount; i++) {
      overload.addParameter(Parameter.from(method.getParameters().get(i)));
    }

    return overload;
  }
}
