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

import static java.util.stream.Collectors.toSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Type;

/**
 * Abstract class for visitor whose first goal is to create JsOverlays methods.
 *
 * <p>A JsOverlay method is final and cannot be overridden. This visitor takes care to define
 * JsOverlay method only on top parent type.
 *
 * <p>With well formed d.ts or extern files, classes only override methods from parent interfaces.
 * Even if in the future we process files where classes override methods from parent classes (could
 * be the case with implementation files), we should fix that in our java model by removing those
 * overridden methods before to create any JsOverlay method.
 *
 * <p>TODO(b/29986321): we should consider adding the @Override annotation methods once and test if
 * the annotation is present on the methods before to process it.
 */
public abstract class AbstractJsOverlayMethodCreator extends AbstractModelVisitor {
  private final Map<Type, Set<String>> parentInterfaceMethodsByType = new HashMap<>();

  @Override
  public boolean visit(Method method) {
    // if the method is defined on a parent interface, default JsOverlay methods will be created
    // on that interface. These methods cannot be overridden, so we don't need to do anything here.
    if (isMethodOverrideParentInterfaceMethod(method)) {
      return false;
    }

    return processMethod(method);
  }

  protected abstract boolean processMethod(Method method);

  protected boolean isMethodOverrideParentInterfaceMethod(Method method) {
    return isMethodOverrideParentInterfaceMethod(method, method.getEnclosingType());
  }

  protected boolean isMethodOverrideParentInterfaceMethod(Method method, Type owner) {
    if (!parentInterfaceMethodsByType.containsKey(owner)) {
      parentInterfaceMethodsByType.put(owner, getParentInterfacesMethods(owner));
    }

    return parentInterfaceMethodsByType.get(owner).contains(getOverrideKey(method));
  }

  private static Set<String> getParentInterfacesMethods(Type type) {
    return getParentInterfaces(type, true)
        .stream()
        .flatMap(t -> t.getMethods().stream())
        .map(AbstractJsOverlayMethodCreator::getOverrideKey)
        .collect(toSet());
  }

  private static String getOverrideKey(Method m) {
    // The only way a class in closure or typescript can redefine a method from a parent interface
    // is by adding optional parameters. Creating a key containing the name of the method and the
    // nbr of parameters is enough to check if a method exists on a parent interface.
    return m.getName() + "%" + m.getParameters().size();
  }
}
