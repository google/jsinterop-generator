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

import static com.google.common.base.Preconditions.checkState;
import static jsinterop.generator.model.AnnotationType.JS_FUNCTION;
import static jsinterop.generator.model.AnnotationType.JS_OVERLAY;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import jsinterop.generator.model.ArrayTypeReference;
import jsinterop.generator.model.Field;
import jsinterop.generator.model.JavaTypeReference;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Method.Parameter;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;
import jsinterop.generator.model.WildcardTypeReference;

/**
 * Replaces generic JsFunction type references to bounded wildcard type references in method's
 * parameters where it's possible in order to improve API quality.
 *
 * <p>We follow the PECS principle standing for producer-extends, consumer-super for defining
 * bounds; if a parameterized type represents a T producer, we use {@code <? extends T>}; if it
 * represents a T consumer, we use {@code <? super T>} If it represents both or if we cannot
 * determine if it's a producer or consumer we use {@code <T>}.
 *
 * <p>E.g.</p>
 *
 * <pre>
 *    &#64;JsFunction
 *    interface FooCallback&lt;T,V&gt; {
 *      V onInvoke(T param);
 *    }
 * </pre>
 *
 * <p>The reference to this interface can use bounded wildcard type as following:</p>
 *
 * <pre>
 *    interface Bar&lt;T, V&gt; {
 *      void foo(FooCallback&lt;? super T, ? extends V&gt; callback);
 *    }
 * </pre>
 *
 * <p>We only apply this logic on methods parameters and fields. We never create wildcard types as
 * return type of a method. Rather than providing flexibility, that would force the users to use
 * wildcard types in client code.
 *
 * <p>In addition to the automatic mechanism, wildcard type creation can also be driven by
 * configuration file. The configuration file contains a list of key/value where the key is the
 * fully qualify name of the entity and the value is the kind of wildcard type to use.
 *
 * <p>Ex:</p>
 *
 * <pre>
 *     package bar;
 *
 *     class Foo {
 *       public void method(Bar&lt;T,V&gt; bar);
 *     }
 * </pre>
 *
 * <p>For configuring wildcard type for T and V, the config file can contain following entries:</p>
 *
 * <pre>
 *   bar.Foo.method.bar#0=SUPER
 *   bar.Foo.method.bar#1=EXTENDS
 * </pre>
 *
 * The resulting code will be: {@code public void method(Bar<? super T, ? extends V> bar);}
 *
 * <p>NONE can be used as value to remove a wildcard type added by the automatic mechanism.
 *
 * <p>Note that we don't support type variable in nested type parameter like {@code List<List<T>>}.
 */
public class WildcardTypeCreator extends AbstractModelVisitor {
  private enum WildcardType {
    SUPER,
    EXTENDS,
    NONE;

    static WildcardType from(String value) {
      try {
        return WildcardType.valueOf(value);
      } catch (IllegalArgumentException e) {
        throw new IllegalStateException(
            "Invalid wildcard type ["
                + value
                + "]. Must be "
                + Arrays.toString(WildcardType.values()));
      }
    }
  }

  private final Set<Type> unionTypeHelperTypes;
  private final Map<String, WildcardType> wildcardsByFqn;

  public WildcardTypeCreator(Set<Type> unionTypeHelperTypes, Map<String, String> wildcardsByFqn) {
    this.unionTypeHelperTypes = unionTypeHelperTypes;
    this.wildcardsByFqn = new HashMap<>(Maps.transformValues(wildcardsByFqn, WildcardType::from));
  }

  @Override
  public void endVisit(Program program) {
    checkState(wildcardsByFqn.isEmpty(), "Unused wildCard directives: %s", wildcardsByFqn);
  }

  @Override
  public boolean visit(Field field) {
    field.setType(maybeCreateWildcardType(field.getJavaFqn(), field.getType()));
    return false;
  }

  @Override
  public boolean visit(Method method) {
    for (Parameter parameter : method.getParameters()) {
      parameter.setType(maybeCreateWildcardType(parameter.getJavaFqn(), parameter.getType()));
    }
    return false;
  }

  private TypeReference maybeCreateWildcardType(String fqn, TypeReference typeReference) {
    if (!(typeReference instanceof ParametrizedTypeReference)) {
      checkState(
          !wildcardsByFqn.containsKey(fqn),
          "%s doesn't represent a parametrized type reference",
          fqn);
      return typeReference;
    }
    ParametrizedTypeReference parametrizedTypeReference = (ParametrizedTypeReference) typeReference;

    // Apply the automatic pattern detection of wildcard usage as defined in the javadoc of this
    // class.
    ParametrizedTypeReference resultTypeReference =
        maybeCreateWildcardForJsFunctionReference(parametrizedTypeReference);

    // Apply user configuration for wildcard.
    return applyWildcardFromUserConfiguration(resultTypeReference, fqn);
  }

  private ParametrizedTypeReference applyWildcardFromUserConfiguration(
      ParametrizedTypeReference originalTypeReference, String fqn) {
    List<TypeReference> newTypeArguments = new ArrayList<>();

    int typeArgumentIndex = 0;
    for (TypeReference typeArgument : originalTypeReference.getActualTypeArguments()) {
      WildcardType wildcardType = lookupWildcardType(fqn, typeArgumentIndex++);
      boolean isAlreadyWildcard = typeArgument instanceof WildcardTypeReference;
      TypeReference bound =
          isAlreadyWildcard ? ((WildcardTypeReference) typeArgument).getBound() : typeArgument;

      TypeReference newTypeArgument;
      if (wildcardType == WildcardType.NONE) {
        checkState(isAlreadyWildcard, "Cannot apply NONE to a non wildcard type.");
        newTypeArgument = bound;
      } else if (wildcardType == WildcardType.SUPER) {
        newTypeArgument = WildcardTypeReference.createWildcardLowerBound(bound);
      } else if (wildcardType == WildcardType.EXTENDS) {
        newTypeArgument = WildcardTypeReference.createWildcardUpperBound(bound);
      } else {
        newTypeArgument = typeArgument;
      }

      newTypeArguments.add(newTypeArgument);
    }

    return new ParametrizedTypeReference(originalTypeReference.getMainType(), newTypeArguments);
  }

  private WildcardType lookupWildcardType(String fqn, int typeArgumentIndex) {
    return wildcardsByFqn.remove(fqn + "#" + typeArgumentIndex);
  }

  private ParametrizedTypeReference maybeCreateWildcardForJsFunctionReference(
      ParametrizedTypeReference typeReference) {
    if (!isGenericJsFunctionTypeReference(typeReference)) {
      return typeReference;
    }

    Type jsFunctionType = getMainType(typeReference);
    Method jsFunctionMethod = getCallbackMethod(jsFunctionType);
    List<TypeReference> methodParameterTypes =
        jsFunctionMethod
            .getParameters()
            .stream()
            .map(Parameter::getType)
            .collect(Collectors.toList());

    List<TypeReference> actualTypeArguments = typeReference.getActualTypeArguments();
    List<TypeReference> newTypeArguments = new ArrayList<>();

    for (TypeReference typeParameter : jsFunctionType.getTypeParameters()) {
      TypeReference currentTypeArgument = actualTypeArguments.get(newTypeArguments.size());

      TypeReferenceCounter finder = new TypeReferenceCounter(typeParameter);

      int refsInReturnType =
          finder.countReferences(ImmutableList.of(jsFunctionMethod.getReturnType()));
      int refsInParameter = finder.countReferences(methodParameterTypes);

      if (refsInReturnType > 0
          && refsInParameter == 0
          && isDirectReference(typeParameter, jsFunctionMethod.getReturnType())) {
        // Type variable is only used as the return type of a callback function. The function
        // can be considered as a producer of this type (i.e. ? extends T).
        newTypeArguments.add(WildcardTypeReference.createWildcardUpperBound(currentTypeArgument));
      } else if (refsInReturnType == 0
          && refsInParameter > 0
          && areAllDirectReferences(typeParameter, methodParameterTypes, refsInParameter)) {
        // Type variable is only used as parameter type of callback function. The function
        // can be considered as a consumer of this type (i.e. ? super T).
        newTypeArguments.add(WildcardTypeReference.createWildcardLowerBound(currentTypeArgument));
      } else {
        // Impossible to know if the function is a consumer or producer of the type so cannot use
        // a wildcard type.
        newTypeArguments.add(currentTypeArgument);
      }
    }

    return new ParametrizedTypeReference(new JavaTypeReference(jsFunctionType), newTypeArguments);
  }

  private boolean isGenericJsFunctionTypeReference(ParametrizedTypeReference typeReference) {
    Type mainType = getMainType(typeReference);

    return mainType != null && mainType.hasAnnotation(JS_FUNCTION);
  }

  private boolean areAllDirectReferences(
      TypeReference target, List<TypeReference> references, int parameterReferenceCount) {

    return references.stream().filter((ref) -> isDirectReference(target, ref)).count()
        == parameterReferenceCount;
  }

  private boolean isDirectReference(TypeReference target, TypeReference reference) {
    // The reference is a 'direct reference' if it's not part of a type parameter of a parametrized
    // type reference. We also consider arrays as direct reference since unlike generic types
    // they are covariant and wildcard upgrade will result in complaint types.
    return reference.equals(target)
        || isArrayTypeReference(target, reference)
        || isUnionTypeHelperTypeReference(target, reference);
  }

  private boolean isUnionTypeHelperTypeReference(
      TypeReference target, TypeReference unionTypeHelperReference) {
    if (!(unionTypeHelperReference instanceof ParametrizedTypeReference)) {
      return false;
    }

    Type mainType = getMainType(unionTypeHelperReference);

    if (!unionTypeHelperTypes.contains(mainType)) {
      return false;
    }

    List<TypeReference> actualTypeArguments =
        ((ParametrizedTypeReference) unionTypeHelperReference).getActualTypeArguments();
    return actualTypeArguments.contains(target);
  }

  private boolean isArrayTypeReference(TypeReference target, TypeReference arrayTypeReference) {
    return arrayTypeReference instanceof ArrayTypeReference
        && ((ArrayTypeReference) arrayTypeReference).getArrayType().equals(target);
  }

  private Method getCallbackMethod(Type jsFunctionType) {
    // A JsFunction type should have only one method that is not annotated with JsOverlay annotation
    return jsFunctionType
        .getMethods()
        .stream()
        .filter(m -> !m.hasAnnotation(JS_OVERLAY))
        .findAny()
        .get();
  }

  private Type getMainType(TypeReference typeReference) {
    TypeReference mainType = ((ParametrizedTypeReference) typeReference).getMainType();

    if (mainType instanceof JavaTypeReference) {
      return ((JavaTypeReference) mainType).getJavaType();
    }

    return null;
  }

  private static class TypeReferenceCounter extends AbstractModelVisitor {
    private final TypeReference typeReferenceToFind;
    private int referenceCount;

    TypeReferenceCounter(TypeReference typeReferenceToFind) {
      this.typeReferenceToFind = typeReferenceToFind;
    }

    @Override
    public boolean visit(TypeReference typeReference) {
      if (typeReference.equals(typeReferenceToFind)) {
        referenceCount++;
      }

      return true;
    }

    public int countReferences(List<TypeReference> rootTypeReferences) {
      referenceCount = 0;
      accept(rootTypeReferences);
      return referenceCount;
    }
  }
}
