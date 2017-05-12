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
 */
package jsinterop.generator.closure.visitor;

import static com.google.common.base.Preconditions.checkState;
import static jsinterop.generator.helper.AbstractTypeRegistry.ReferenceContext.IN_TYPE_ARGUMENTS;
import static jsinterop.generator.helper.GeneratorUtils.extractName;
import static jsinterop.generator.model.AnnotationType.JS_METHOD;

import com.google.common.collect.Iterables;
import com.google.javascript.rhino.jstype.FunctionType;
import com.google.javascript.rhino.jstype.JSType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jsinterop.generator.closure.helper.GenerationContext;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.JavaTypeReference;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;

/** Visitor collecting TypeParameter and store them in our Java model. */
public class TypeParameterCollector extends AbstractClosureVisitor {

  public TypeParameterCollector(GenerationContext context) {
    super(context);
  }

  @Override
  protected boolean visitClassOrInterface(FunctionType type) {
    Type currentJavaType = getCurrentJavaType();
    List<TypeReference> generics = convertTypeParametersDefinition(type);

    currentJavaType.getTypeParameters().addAll(generics);

    // If the type is an external type and have extension point, we need to retrofit the generics on
    // the extension type as well
    if (currentJavaType.isExtern()
        && getJavaTypeRegistry().containsExtensionType(currentJavaType)) {
      Type extensionType = getJavaTypeRegistry().getExtensionType(currentJavaType);
      extensionType.getTypeParameters().addAll(generics);
      // parametrize the extension clause
      TypeReference currentExtensionReference =
          Iterables.getOnlyElement(extensionType.getInheritedTypes());
      checkState(currentExtensionReference instanceof JavaTypeReference);
      extensionType.getInheritedTypes().clear();
      extensionType
          .getInheritedTypes()
          .add(new ParametrizedTypeReference(currentExtensionReference, generics));
    }

    return true;
  }

  @Override
  protected boolean visitMethod(FunctionType method, boolean isStatic) {
    List<TypeReference> generics = convertTypeParametersDefinition(method);

    if (!generics.isEmpty()) {
      Method m = getJavaMethod(extractName(method.getDisplayName()), isStatic);
      m.getTypeParameters().addAll(generics);
    }
    return true;
  }

  private Method getJavaMethod(String methodName, boolean isStatic) {
    // At this point there is no method overloading, nor JsProperty or JsOverlay methods yet and
    // each method of a type has a unique name.
    Optional<Method> method =
        getCurrentJavaType()
            .getMethods()
            .stream()
            .filter(m -> m.isStatic() == isStatic && methodName.equals(getNativeMethodName(m)))
            .findFirst();

    if (!method.isPresent()) {
      throw new IllegalStateException(
          "Method with name "
              + methodName
              + " not found on type "
              + getCurrentJavaType().getJavaFqn());
    }

    return method.get();
  }

  private String getNativeMethodName(Method method) {
    if (method.hasAnnotation(JS_METHOD)) {
      Annotation jsMethod = method.getAnnotation(JS_METHOD);
      if (jsMethod.getNameAttribute() != null) {
        return jsMethod.getNameAttribute();
      }
    }

    return method.getName();
  }

  protected List<TypeReference> convertTypeParametersDefinition(JSType type) {
    return type.getTemplateTypeMap()
        .getTemplateKeys()
        .stream()
        .map(t -> getJavaTypeRegistry().createTypeReference(t, IN_TYPE_ARGUMENTS))
        .collect(Collectors.toList());
  }
}
