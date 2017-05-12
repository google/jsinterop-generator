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
import static jsinterop.generator.model.EntityKind.CONSTRUCTOR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jsinterop.generator.model.Entity;
import jsinterop.generator.model.Field;
import jsinterop.generator.model.JavaTypeReference;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;

/**
 * The generator generates interfaces for abstracting some concepts of typescript/closure like
 * function types, union types or literal types. By creating a different Java interfaces each time
 * we abstract one of these concepts, we could make APIs incompatible wrt. their parent contract.
 *
 * <pre>
 *  ex:
 *   Typescript code:
 *
 *   interface Foo {
 *     foo(bar: {baz: string}): void;
 *   }
 *
 *   class FooImpl implements Foo {
 *     foo(bar: {baz: string}): void;
 *   }
 *
 *   generated java code (jsinterop annotations omitted):
 *   interface Foo {
 *     interface FooBarType {
 *       String getBaz();
 *     }
 *
 *     void foo(Foo.FooBarType bar);
 *   }
 *
 *   class FooImpl implements Foo {
 *     interface FooBarType {
 *       String getBaz();
 *     }
 *
 *     void foo(FooImpl.FooBarType bar);
 *   }
 * </pre>
 *
 * The example clearly shows that the generated class FooImpl doesn't implement correctly the api of
 * the interface Foo.
 *
 * <p>This visitor will try to detect that case and reuse the type defined on parent interfaces.
 */
public class DuplicatedTypesUnifier extends AbstractModelVisitor {
  private static final String TYPE_DELIMITER_PATTERN = "([{(,|<})>]|\\b)";

  private static String createTypeKey(Type type, String keyPrefix) {
    String typeKey = type.getNativeFqn();

    int i = 0;
    for (TypeReference typeParameter : type.getTypeParameters()) {
      // Because class Foo<T> { T field;) is structurally equivalent to class Foo<U> {U field;}.
      // We cannot use the name of type parameters to generate a unique key a type.  We are creating
      // an unique key for each type parameter that depends on their declaration index in the type
      // parameters declaration and replace each type parameter by this index.
      // EX: if we have an interface Foo<U,V> abstracting the native type function(U): V
      // the resulting key is function(%0): %1
      String key = "%" + i++;
      typeKey =
          typeKey.replaceAll(
              TYPE_DELIMITER_PATTERN + typeParameter.getTypeName() + TYPE_DELIMITER_PATTERN,
              "$1" + key + "$2");
    }

    return keyPrefix + "@" + typeKey;
  }

  private final boolean useBeanConvention;
  private String currentKeyContext;
  private final Map<Type, Map<String, Type>> syntheticTypesByEnclosingType = new HashMap<>();
  private Set<Type> currentSyntheticTypesSet;

  public DuplicatedTypesUnifier(boolean useBeanConvention) {
    this.useBeanConvention = useBeanConvention;
  }

  @Override
  public boolean visit(Type type) {
    // Don't visit:
    //  - types that have been already visited.
    //  - types that are synthetic type (because they don't implement any interfaces).
    if (syntheticTypesByEnclosingType.containsKey(type) || type.isSynthetic()) {
      return false;
    }

    // Synthetic type can be used in generics. In order to avoid a false positive match reset
    // the keyContext
    currentKeyContext = type.getJavaFqn();

    syntheticTypesByEnclosingType.put(type, getParentSyntheticTypes(type));

    currentSyntheticTypesSet =
        type.getInnerTypes().stream().filter(Type::isSynthetic).collect(toSet());

    // Don't need to traverse the type if there are no synthetic types defined in this type.
    return !currentSyntheticTypesSet.isEmpty();
  }

  private Map<String, Type> getParentSyntheticTypes(Type type) {
    List<Type> parentInterfaces = getParentInterfaces(type);
    // visit implemented/extended interfaces first
    accept(parentInterfaces);

    Map<String, Type> syntheticTypesByKey = new HashMap<>();

    // Add existing synthetic types from parent interfaces
    for (Type parentInterface : parentInterfaces) {
      syntheticTypesByKey.putAll(syntheticTypesByEnclosingType.get(parentInterface));
    }

    return syntheticTypesByKey;
  }

  @Override
  public boolean visit(Method method) {
    currentKeyContext = method.getKind() == CONSTRUCTOR ? "<constructor>" : method.getName();
    currentKeyContext = getKeyContextIfStatic(currentKeyContext, method);
    return true;
  }

  @Override
  public boolean visit(Field field) {
    // If an interface defines a field, the class implementing the interface contains the field
    // definition and the getter and setter. In this case we will try to use the same synthetic type
    // for the field and for the accessor methods.
    currentKeyContext = getKeyContextIfStatic(getGetterName(field), field);

    return true;
  }

  private static String getKeyContextIfStatic(String currentKey, Entity member) {
    if (member.isStatic()) {
      return "%s%" + currentKey;
    }
    return currentKey;
  }

  private String getGetterName(Field field) {
    if (useBeanConvention) {
      return "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
    }
    return field.getName();
  }

  @Override
  public boolean visit(TypeReference typeReference) {
    if (typeReference instanceof JavaTypeReference) {
      maybeFixSyntheticTypeReference(((JavaTypeReference) typeReference));
    }
    return true;
  }

  @SuppressWarnings("ReferenceEquality")
  private void maybeFixSyntheticTypeReference(JavaTypeReference typeReference) {
    if (!typeReference.getJavaType().isSynthetic()) {
      // typeReference is not a reference to an synthetic type.
      return;
    }

    Type syntheticType = typeReference.getJavaType();

    String typeKey = createTypeKey(syntheticType, currentKeyContext);

    Map<String, Type> syntheticTypesByKey =
        syntheticTypesByEnclosingType.get(syntheticType.getParent());

    Type existingSyntheticType = syntheticTypesByKey.get(typeKey);

    if (existingSyntheticType != null && existingSyntheticType != syntheticType) {
      // A type with the same structure is already generated.
      syntheticType.removeFromParent();
      // A synthetic type can have reference to other synthetic type like a function type in a
      // union type: (function(V):U|string).
      // Because a synthetic types are only referred one time (where they are used), so we are sure
      // they are not used outside the current synthetic type we are deleting.
      // Delete synthetic types referenced in another synthetic type.
      cleanInnerSyntheticTypes(syntheticType);
      typeReference.setJavaType(existingSyntheticType);
    } else {
      syntheticTypesByKey.put(typeKey, syntheticType);
    }
  }

  private static void cleanInnerSyntheticTypes(Type mainType) {
    (new AbstractModelVisitor() {
          @Override
          public boolean visit(TypeReference typeReference) {
            if (typeReference instanceof JavaTypeReference) {
              Type javaType = ((JavaTypeReference) typeReference).getJavaType();
              if (javaType.isSynthetic() && javaType.getParent().equals(mainType.getParent())) {
                javaType.removeFromParent();
              }
            }
            return true;
          }
        })
        .accept(mainType);
  }
}
