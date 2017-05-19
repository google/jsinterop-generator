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

package jsinterop.generator.helper;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import jsinterop.generator.model.JavaTypeReference;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;

/** Registry providing logic to keep the link between a native type and a java type. */
public abstract class AbstractTypeRegistry<T> {
  /** Context for type references. */
  public enum ReferenceContext {
    REGULAR,
    IN_TYPE_ARGUMENTS,
    IN_HERITAGE_CLAUSE,
  }

  private final Map<T, Type> javaTypesById;
  private final T nativeArrayTypeKey;
  private Type globalType;
  private final Map<Type, Type> extensionTypesByParent = new HashMap<>();

  protected AbstractTypeRegistry(T nativeArrayTypeKey) {
    this(new HashMap<>(), nativeArrayTypeKey);
  }

  protected AbstractTypeRegistry(Map<T, Type> registry, T nativeArrayTypeKey) {
    this.javaTypesById = registry;
    this.nativeArrayTypeKey = nativeArrayTypeKey;
  }

  protected Optional<TypeReference> getNativeArrayTypeReference() {
    checkState(!javaTypesById.isEmpty(), "Type registry not initialized yet.");

    if (containsJavaTypeByKey(nativeArrayTypeKey)) {
      Type arrayType = getJavaTypeByKey(nativeArrayTypeKey);
      return Optional.of(new JavaTypeReference(arrayType));
    }
    return Optional.empty();
  }

  protected boolean containsJavaTypeByKey(T nativeTypeKey) {
    return javaTypesById.containsKey(nativeTypeKey);
  }

  protected Type getJavaTypeByKey(T nativeTypeKey) {
    Type type = javaTypesById.get(nativeTypeKey);

    checkNotNull(type, "Unknown type [%s]", nativeTypeKey);

    return type;
  }

  protected void registerJavaTypeByKey(Type type, T nativeTypeKey) {
    Type previousType = javaTypesById.put(nativeTypeKey, type);
    checkState(previousType == null, "A type already exists with key [%s]", nativeTypeKey);
  }

  public boolean containsJavaGlobalType() {
    return globalType != null;
  }

  public Type getGlobalJavaType() {
    return checkNotNull(globalType, "The global type is unknown");
  }

  public void registerJavaGlobalType(Type type, T nativeTypeKey) {
    checkState(globalType == null, "The global type already exists");

    globalType = type;

    // TODO(b/34278243): Clean that up for typescript when bug is fixed.
    if (nativeTypeKey != null) {
      registerJavaTypeByKey(type, nativeTypeKey);
    }
  }

  public boolean containsExtensionType(Type parent) {
    return extensionTypesByParent.containsKey(parent);
  }

  public Type getExtensionType(Type parent) {
    Type extensionType = extensionTypesByParent.get(parent);
    checkNotNull(extensionType, "No extension type exists for %s", parent);
    return extensionType;
  }

  /**
   * In typescript and closure, a source type definition file can add API to a type that is defined
   * by a dependency. We model that by creating a class extending the original third party class.
   * This method create a link between the original class <code>parent</code> and the new extension
   * point <code>child</code>.
   */
  public void registerExtensionType(Type parent, Type child) {
    Type previousExtensionType = extensionTypesByParent.put(parent, child);
    checkState(previousExtensionType == null, "An extension type already exists for %s", parent);
  }
}
