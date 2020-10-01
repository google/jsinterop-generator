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
import static jsinterop.generator.model.AnnotationType.JS_TYPE;
import static jsinterop.generator.model.EntityKind.CLASS;
import static jsinterop.generator.model.EntityKind.CONSTRUCTOR;
import static jsinterop.generator.model.EntityKind.ENUM;
import static jsinterop.generator.model.EntityKind.INTERFACE;
import static jsinterop.generator.model.EntityKind.METHOD;
import static jsinterop.generator.model.EntityKind.NAMESPACE;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.j2cl.common.visitor.Context;
import com.google.j2cl.common.visitor.Processor;
import com.google.j2cl.common.visitor.Visitable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Models Java classes or interfaces. */
@Visitable
@Context
public class Type extends Entity implements HasTypeParameters {
  public static Type from(Type type) {
    Type clonedType = new Type(type.getKind());

    // copy properties from Entity class
    copyEntityProperties(type, clonedType);

    clonedType.setEnclosingType(type.getEnclosingType());
    clonedType.setPackageName(type.getPackageName());
    clonedType.setSynthetic(type.isSynthetic());
    clonedType.setStructural(type.isStructural());
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

    for (TypeReference typeReference : type.getExtendedTypes()) {
      clonedType.addExtendedType(typeReference);
    }

    for (TypeReference typeReference : type.getImplementedTypes()) {
      clonedType.addImplementedType(typeReference);
    }

    for (TypeReference typeReference : type.getTypeParameters()) {
      clonedType.addTypeParameter(typeReference);
    }

    return clonedType;
  }

  @Visitable List<TypeReference> extendedTypes = new ArrayList<>();
  @Visitable List<TypeReference> implementedTypes = new ArrayList<>();
  @Visitable List<Type> innerTypes = new ArrayList<>();
  @Visitable List<Field> fields = new ArrayList<>();
  @Visitable List<Method> constructors = new ArrayList<>();
  @Visitable List<Method> methods = new ArrayList<>();

  private String packageName;
  private Set<TypeReference> typeParameters = new LinkedHashSet<>();
  private boolean extern;
  private boolean extensionType;
  private boolean synthetic;
  private boolean structural;
  private String nativeFqn;

  public Type(EntityKind entityKind) {
    if (entityKind != CLASS
        && entityKind != INTERFACE
        && entityKind != NAMESPACE
        && entityKind != ENUM) {
      throw new IllegalStateException("Type can be only CLASS, INTERFACE, NAMESPACE entity");
    }

    setKind(entityKind);
  }

  public List<TypeReference> getExtendedTypes() {
    return extendedTypes;
  }

  public TypeReference getSuperClass() {
    if (isInterface() || getExtendedTypes().isEmpty()) {
      return null;
    }

    return Iterables.getOnlyElement(getExtendedTypes());
  }

  void setExtendedTypes(List<TypeReference> extendedTypes) {
    this.extendedTypes = extendedTypes;
  }

  void setImplementedTypes(List<TypeReference> implementedTypes) {
    this.implementedTypes = implementedTypes;
  }

  public void setTypeParameters(Collection<TypeReference> typeParameters) {
    this.typeParameters = new LinkedHashSet<>(typeParameters);
  }

  public List<TypeReference> getImplementedTypes() {
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

  public boolean isEnum() {
    return getKind() == ENUM;
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

  public void addExtendedType(TypeReference typeReference) {
    extendedTypes.add(typeReference);
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

  public String getNativeFqn() {
    return nativeFqn;
  }

  public void setNativeFqn(String nativeFqn) {
    this.nativeFqn = nativeFqn;
  }

  public String getJavaFqn() {
    String packageName = getPackageName();

    if (Strings.isNullOrEmpty(packageName)) {
      return getName();
    }

    return packageName + "." + getName();
  }

  @Override
  public String getConfigurationIdentifier() {
    return getJavaFqn();
  }

  public String getJavaRelativeQualifiedTypeName() {
    return getJavaFqn().substring(getTopLevelParentType().getPackageName().length() + 1);
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

  public boolean isStructural() {
    return structural;
  }

  public void setStructural(boolean structural) {
    this.structural = structural;
  }

  public void removeFromParent() {
    checkState(getEnclosingType() != null, "Type is not an inner type.");
    getEnclosingType().removeInnerType(this);
  }

  public boolean isNativeInterface() {
    return hasAnnotation(JS_TYPE) && getAnnotation(JS_TYPE).getIsNativeAttribute() && isInterface();
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

  @Override
  public Node accept(Processor processor) {
    return Visitor_Type.visit(processor, this);
  }
}
