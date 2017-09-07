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
package jsinterop.generator.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;
import static jsinterop.generator.model.EntityKind.CLASS;
import static jsinterop.generator.model.EntityKind.CONSTRUCTOR;
import static jsinterop.generator.model.EntityKind.INTERFACE;
import static jsinterop.generator.model.EntityKind.METHOD;
import static jsinterop.generator.model.EntityKind.NAMESPACE;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Models Java classes or interfaces. */
public class Type extends Entity implements HasTypeParameters, Visitable<Type> {
  public static Type from(Type type) {
    Type clonedType = new Type(type.getKind());

    // copy properties from Entity class
    copyEntityProperties(type, clonedType);

    clonedType.setEnclosingType(type.getEnclosingType());
    clonedType.setPackageName(type.getPackageName());
    clonedType.setNativeNamespace(type.getNativeNamespace());
    clonedType.setSynthetic(type.isSynthetic());
    clonedType.setExtern(type.isExtern());

    for (Method m : type.getMethods()) {
      clonedType.addMethod(Method.from(m));
    }

    for (Method m : type.getConstructors()) {
      clonedType.addConstructor(Method.from(m));
    }

    for (Field field : type.getFields()) {
      clonedType.addField(Field.from(field));
    }

    for (Type innerType : type.getInnerTypes()) {
      clonedType.addInnerType(Type.from(innerType));
    }

    for (TypeReference typeReference : type.getInheritedTypes()) {
      clonedType.addInheritedType(typeReference);
    }

    for (TypeReference typeReference : type.getImplementedTypes()) {
      clonedType.addImplementedType(typeReference);
    }

    for (TypeReference typeReference : type.getTypeParameters()) {
      clonedType.addTypeParameter(typeReference);
    }

    return clonedType;
  }

  private String packageName;
  private Set<TypeReference> inheritedTypes = new LinkedHashSet<>();
  private Set<TypeReference> implementedTypes = new LinkedHashSet<>();
  private Set<TypeReference> typeParameters = new LinkedHashSet<>();
  private List<Field> fields = new ArrayList<>();
  private List<Method> methods = new ArrayList<>();
  private List<Method> constructors = new ArrayList<>();
  private List<Type> innerTypes = new ArrayList<>();
  private boolean extern;
  private boolean extensionType;
  private boolean synthetic;
  private String nativeNamespace;
  private String nativeFqn;

  public Type(EntityKind classOrInterfaceOrNamespace) {
    if (classOrInterfaceOrNamespace != CLASS
        && classOrInterfaceOrNamespace != INTERFACE
        && classOrInterfaceOrNamespace != NAMESPACE) {
      throw new IllegalStateException("Type can be only CLASS, INTERFACE, NAMESPACE entity");
    }

    setKind(classOrInterfaceOrNamespace);
  }

  public Set<TypeReference> getInheritedTypes() {
    return inheritedTypes;
  }

  public void setInheritedTypes(Collection<TypeReference> inheritedTypes) {
    this.inheritedTypes = new LinkedHashSet<>(inheritedTypes);
  }

  public void setImplementedTypes(Collection<TypeReference> implementedTypes) {
    this.implementedTypes = new LinkedHashSet<>(implementedTypes);
  }

  public void setTypeParameters(Collection<TypeReference> typeParameters) {
    this.typeParameters = new LinkedHashSet<>(typeParameters);
  }

  public Set<TypeReference> getImplementedTypes() {
    return implementedTypes;
  }

  public List<Field> getFields() {
    return ImmutableList.copyOf(fields);
  }

  public List<Method> getMethods() {
    return ImmutableList.copyOf(methods);
  }

  public List<Method> getConstructors() {
    return ImmutableList.copyOf(constructors);
  }

  @Override
  public Collection<TypeReference> getTypeParameters() {
    return typeParameters;
  }

  public List<Type> getInnerTypes() {
    return ImmutableList.copyOf(innerTypes);
  }

  public String getPackageName() {
    if (getEnclosingType() != null) {
      // as the name of the Parent can be modified latter, we cannot store the parent.getJavaFqn()
      // as package name of inner class. Compute it each time, we call getPackageName method.
      return getEnclosingType().getJavaFqn();
    }

    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public boolean isClass() {
    return getKind() == CLASS;
  }

  public boolean isInterface() {
    return getKind() == INTERFACE;
  }

  public boolean isNamespace() {
    return getKind() == NAMESPACE;
  }

  public void addField(Field field) {
    checkArgument(field.getEnclosingType() == null, "%s is not an orphan field.", field);

    fields.add(field);
    field.setEnclosingType(this);
  }

  public void addMethods(List<Method> methods) {
    methods.forEach(this::addMethod);
  }

  public void addMethod(Method method) {
    checkArgument(method.getEnclosingType() == null, "%s is not an orphan method.", method);

    if (method.getKind() == CONSTRUCTOR) {
      constructors.add(method);
    } else {
      methods.add(method);
    }

    method.setEnclosingType(this);
  }

  public void addConstructor(Method constructor) {
    checkArgument(
        constructor.getKind() == CONSTRUCTOR, "Method %s is not a constructor.", constructor);
    addMethod(constructor);
  }

  public void addInnerType(Type innerType) {
    checkArgument(innerType.getEnclosingType() == null, "%s is not an orphan type.", innerType);
    innerTypes.add(innerType);

    innerType.setEnclosingType(this);

    // inner types are automatically static
    innerType.setStatic(true);
  }

  @Override
  public void addTypeParameter(TypeReference typeReference) {
    typeParameters.add(typeReference);
  }

  public void addInheritedType(TypeReference typeReference) {
    inheritedTypes.add(typeReference);
  }

  public void addImplementedType(TypeReference typeReference) {
    implementedTypes.add(typeReference);
  }

  public Type getTopLevelParentType() {
    return getEnclosingType() == null ? this : getEnclosingType().getTopLevelParentType();
  }

  public Set<String> getInnerTypesNames() {
    return newHashSet(transform(getInnerTypes(), Entity::getName));
  }

  public String getNativeNamespace() {
    return nativeNamespace;
  }

  public void setNativeNamespace(String nativeNamespace) {
    this.nativeNamespace = nativeNamespace;
  }

  public String getNativeFqn() {
    return nativeFqn;
  }

  public void setNativeFqn(String nativeFqn) {
    this.nativeFqn = nativeFqn;
  }

  @Override
  public String getJavaFqn() {
    String packageName = getPackageName();

    if (Strings.isNullOrEmpty(packageName)) {
      return getName();
    }

    return packageName + "." + getName();
  }

  public String getJavaRelativeQualifiedTypeName() {
    return getJavaFqn().substring(getTopLevelParentType().getPackageName().length() + 1);
  }

  @Override
  public Type doVisit(ModelVisitor visitor) {
    if (visitor.visit(this)) {
      setInheritedTypes(visitor.accept(inheritedTypes));
      setImplementedTypes(visitor.accept(implementedTypes));

      visitor.accept(innerTypes);
      visitor.accept(fields);
      visitor.accept(constructors);
      visitor.accept(methods);
    }
    visitor.endVisit(this);
    return this;
  }

  public void removeFields(List<Field> fieldsToRemove) {
    fieldsToRemove.forEach(this::removeField);
  }

  public void removeField(Field field) {
    checkParent(field);
    fields.remove(field);
    field.setEnclosingType(null);
  }

  public void removeMethod(Method toRemove) {
    checkParent(toRemove);
    if (toRemove.getKind() == METHOD) {
      methods.remove(toRemove);
    } else {
      constructors.remove(toRemove);
    }
    toRemove.setEnclosingType(null);
  }

  public void removeInnerType(Type innerType) {
    checkParent(innerType);
    innerTypes.remove(innerType);
    innerType.setEnclosingType(null);
  }

  @SuppressWarnings("ReferenceEquality")
  private void checkParent(Entity entity) {
    checkArgument(entity.getEnclosingType() == this, "%s is not the parent of %s", this, entity);
  }

  public boolean isSynthetic() {
    return synthetic;
  }

  public void setSynthetic(boolean synthetic) {
    this.synthetic = synthetic;
  }

  public boolean isExtern() {
    return extern;
  }

  public void setExtern(boolean extern) {
    this.extern = extern;
  }

  public boolean isExtensionType() {
    return extensionType;
  }

  public void setExtensionType(boolean extensionType) {
    this.extensionType = extensionType;
  }

  public void removeFromParent() {
    checkState(getEnclosingType() != null, "Type is not an inner type.");
    getEnclosingType().removeInnerType(this);
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }

    return Objects.equals(getPackageName(), ((Type) o).getPackageName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), packageName);
  }

  @Override
  public String toString() {
    return (isInterface() ? "Interface " : "Class ") + getJavaFqn();
  }
}
