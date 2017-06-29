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
 */

package jsinterop.generator.visitor;

import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jsinterop.generator.model.AnnotationType;
import jsinterop.generator.model.ArrayTypeReference;
import jsinterop.generator.model.DelegableTypeReference;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;
import jsinterop.generator.model.TypeVariableReference;

/**
 * This visitor avoids that a sub-type overrides a final JsOverlay method.
 *
 * <p>Patterns like IndexSignature are converted to final JsOverlay methods. Several types on the
 * same hierarchy can have the same index signature definition. In this case if the return type is
 * the same, just remove the method, otherwise rename one of the method.
 *
 * <p>TODO(b/29146313): clarify the role of IObject interface.
 */
public class JsOverlayMethodsCleaner extends AbstractModelVisitor {
  private static final Predicate<Method> IS_JS_OVERLAY_METHOD =
      new Predicate<Method>() {
        @Override
        public boolean apply(Method method) {
          return method.hasAnnotation(AnnotationType.JS_OVERLAY);
        }
      };

  private final Set<Type> alreadyVisitedType = new HashSet<>();

  @Override
  public boolean visit(Type type) {
    if (typeMustBeVisited(type)) {
      // ensure that parent have been visited first in order to resolve potential conflicting
      // methods upstream.
      for (TypeReference parentReference : type.getInheritedTypes()) {
        Type inheritedType = getTypeOfInheritedTypeReference(parentReference);
        if (inheritedType != null) {
          accept(inheritedType);
        }
      }

      // let's now visit the method's type
      Map<Method, String> knownOverlayMethodsWithReturnType = getKnownJsOverlayMethods(type);

      for (Method method : new ArrayList<>(type.getMethods())) {
        if (IS_JS_OVERLAY_METHOD.apply(method)) {
          if (addOverlayMethod(method, knownOverlayMethodsWithReturnType)) {
            // method doesn't collide with any known overlay methods.
            return false;
          }

          // Collision: try adding simple name for return.
          String originalName = method.getName();
          String simpleNamePostfix =
              getReturnTypeAsString(method.getReturnType(), false /* useFqn */);
          method.setName(originalName + "As" + simpleNamePostfix);

          if (addOverlayMethod(method, knownOverlayMethodsWithReturnType)) {
            return false;
          }

          // Still collides; try adding qualified name for return.
          String qualifiedNamePostifx =
              getReturnTypeAsString(method.getReturnType(), true /* useFqn */);
          method.setName(originalName + "As" + qualifiedNamePostifx);

          checkState(addOverlayMethod(method, knownOverlayMethodsWithReturnType));
        }
      }

      alreadyVisitedType.add(type);

      return true;
    }

    return false;
  }

  private boolean typeMustBeVisited(Type type) {
    // We only check classes because we don't support default method on interfaces yet.
    return !type.isInterface() && !alreadyVisitedType.contains(type);
  }

  /**
   * Add a method to the set of existing known overlay methods if it doesn't collide with any of
   * these methods. If a same method (same return type, same name, same paramters) already exists,
   * the method will be removed from the type.
   *
   * @return {@code true} if the method doesn't collide with any, return {@code false} otherwise.
   */
  private boolean addOverlayMethod(Method method, Map<Method, String> overlayMethods) {
    String returnType = overlayMethods.get(method);
    if (returnType == null) {
      // no collision: add the method to the existing one set.
      overlayMethods.put(method, method.getReturnType().getJniSignature());
      return true;
    }

    // there is a possible collision, check if it's same method.
    if (returnType.equals(method.getReturnType().getJniSignature())) {
      method.removeFromParent();
      // there is no conflict anymore
      return true;
    }
    // collision with another method.
    return false;
  }

  private String getReturnTypeAsString(TypeReference returnType, boolean fqn) {
    if (returnType instanceof ArrayTypeReference) {
      return getReturnTypeAsString(((ArrayTypeReference) returnType).getArrayType(), fqn) + "Array";
    }

    if (returnType instanceof TypeVariableReference) {
      return "";
    }

    if (returnType instanceof DelegableTypeReference) {
      return getReturnTypeAsString(((DelegableTypeReference) returnType).getDelegate(), fqn);
    }

    return toCamelUpperCase(fqn ? returnType.getJavaTypeFqn() : returnType.getTypeName());
  }

  private Map<Method, String> getKnownJsOverlayMethods(Type currentType) {
    Map<Method, String> knownOverlayMethodsWithReturnType = new HashMap<>();

    addParentJsOverlayMethods(currentType, knownOverlayMethodsWithReturnType);

    return knownOverlayMethodsWithReturnType;
  }

  private void addParentJsOverlayMethods(Type type, Map<Method, String> parentsJsOverlayMethods) {
    Preconditions.checkArgument(!type.isInterface());

    if (!type.getInheritedTypes().isEmpty()) {
      Type parent = getTypeOfInheritedTypeReference(type.getInheritedTypes().iterator().next());
      if (parent != null) {
        for (Method method : getJsOverlayMethods(parent)) {
          parentsJsOverlayMethods.put(method, method.getReturnType().getJniSignature());
        }

        addParentJsOverlayMethods(parent, parentsJsOverlayMethods);
      }
    }
  }

  private List<Method> getJsOverlayMethods(Type type) {
    return newArrayList(filter(type.getMethods(), IS_JS_OVERLAY_METHOD));
  }

  private static String toCamelUpperCase(String javaName) {
    return LOWER_UNDERSCORE.to(UPPER_CAMEL, javaName.replaceAll("\\.", "_"));
  }
}
