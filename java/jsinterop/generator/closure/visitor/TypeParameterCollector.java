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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.javascript.rhino.jstype.FunctionType;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.TemplateType;
import com.google.javascript.rhino.jstype.TemplatizedType;
import com.google.javascript.rhino.jstype.UnionType;
import com.google.javascript.rhino.jstype.Visitor;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
          Iterables.getOnlyElement(extensionType.getExtendedTypes());
      checkState(currentExtensionReference instanceof JavaTypeReference);
      extensionType.getExtendedTypes().clear();
      extensionType
          .getExtendedTypes()
          .add(new ParametrizedTypeReference(currentExtensionReference, generics));
    }

    return true;
  }

  @Override
  protected boolean visitMethod(FunctionType method, boolean isStatic) {
    List<TypeReference> methodTemplateTypes = convertTypeParametersDefinition(method);

    if (!methodTemplateTypes.isEmpty()) {
      Method m = getJavaMethod(extractName(method.getDisplayName()), isStatic);
      m.getTypeParameters().addAll(methodTemplateTypes);
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

  private List<TypeReference> convertTypeParametersDefinition(FunctionType type) {
    // List of templates appearing in the type declared by the @this annotation.
    //
    // It seems to be a convention that when using the @this annotation to change the type, the
    // template variables used have the same names (and meanings) as the ones in the enclosing
    // class definition. The @this annotation is used in Closure to allow the method to be
    // called on an instance of a class that is not necessarily related to the class where
    // the method is declared. Since in Java there is no way to redefine the type of the receiver
    // we take the heuristic to revert to the type of the enclosing class, assuming the template
    // variables of the same name have the same meaning.
    //
    //
    // ex:
    // /**
    //  * @constructor
    //  * @template T
    //  */
    // function Array(var_args) {}
    //
    //
    // // Allow calling Array.pop on IArrayLike<T> instances not just Array<T>.
    // /**
    //  * @return {T}
    //  * @this {IArrayLike<T>}
    //  * @modifies {this}
    //  * @template T
    //  */
    // Array.prototype.pop = function() {};
    //
    //
    // Note that this is just an heuristic and is not necessarily safe. Developers could use
    // template variable names that do not match the ones used in the enclosing class.
    // In such case the extern definition would need to be fixed for the generator to produce
    // reasonable code.
    Set<String> thisTemplateTypeValue =
        TemplateTypeCollector.getTemplateTypes(type.getTypeOfThis());

    return type.getTemplateTypeMap().getTemplateKeys().stream()
        .filter(t -> !thisTemplateTypeValue.contains(t.getReferenceName()))
        .map(t -> getJavaTypeRegistry().createTypeReference(t, IN_TYPE_ARGUMENTS))
        .collect(Collectors.toList());
  }

  /** Collects Template Types defined in any JsType. */
  private static class TemplateTypeCollector extends Visitor.WithDefaultCase<Void> {

    private static Set<String> getTemplateTypes(JSType type) {
      TemplateTypeCollector templateTypeFinder = new TemplateTypeCollector();
      type.visit(templateTypeFinder);
      return templateTypeFinder.getTemplateTypeValues();
    }

    private ImmutableSet.Builder<String> templateTypes = ImmutableSet.builder();

    Set<String> getTemplateTypeValues() {
      return templateTypes.build();
    }

    @Override
    protected Void caseDefault(JSType type) {
      return null;
    }

    @Override
    public Void caseTemplatizedType(TemplatizedType type) {
      type.getTemplateTypeMap().getTemplateKeys().stream()
          .map(k -> type.getTemplateTypeMap().getResolvedTemplateType(k))
          .filter(Objects::nonNull)
          .forEach(t -> t.visit(this));
      return null;
    }

    @Override
    public Void caseTemplateType(TemplateType templateType) {
      templateTypes.add(templateType.getReferenceName());
      return null;
    }

    @Override
    public Void caseUnionType(UnionType type) {
      type.getAlternates().forEach(t -> t.visit(this));

      return null;
    }
  }
}
