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

package jsinterop.generator.visitor;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.AnnotationType;
import jsinterop.generator.model.Entity;
import jsinterop.generator.model.Expression;
import jsinterop.generator.model.Field;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Method.Parameter;
import jsinterop.generator.model.ModelVisitor;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.Statement;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;
import jsinterop.generator.model.Visitable;

/** Abstract class for all visitors that aims to visit our Java model. */
public abstract class AbstractModelVisitor implements ModelVisitor {
  @Override
  public boolean visit(TypeReference typeReference) {
    return true;
  }

  @Override
  public boolean visit(Type type) {
    return true;
  }

  @Override
  public boolean visit(Method method) {
    return true;
  }

  @Override
  public boolean visit(Field field) {
    return true;
  }

  @Override
  public boolean visit(Method.Parameter parameter) {
    return true;
  }

  @Override
  public boolean visit(Program program) {
    return true;
  }

  @Override
  public void endVisit(Program program) {}

  @Override
  public void endVisit(Type type) {}

  @Override
  public void endVisit(Method method) {}

  @Override
  public void endVisit(Field field) {}

  @Override
  public boolean visit(Expression expression) {
    return true;
  }

  @Override
  public boolean visit(Statement statement) {
    return true;
  }

  @Override
  public void endVisit(Parameter parameter) {}

  @Override
  public TypeReference endVisit(TypeReference typeReference) {
    return typeReference;
  }

  @Override
  public <T extends Visitable<T>> T accept(T visitable) {
    return visitable.doVisit(this);
  }

  @Override
  public <T extends Visitable<T>> Collection<T> accept(Collection<T> visitables) {
    Collection<T> newVisitables = new ArrayList<>(visitables.size());
    ImmutableList.copyOf(visitables).forEach(v -> newVisitables.add(v.doVisit(this)));

    return newVisitables;
  }

  protected void addAnnotationNameAttributeIfNotEmpty(
      Entity entity, String originalName, AnnotationType annotationType, boolean createAnnotation) {
    Annotation annotation = entity.getAnnotation(annotationType);

    if (annotation != null && isNullOrEmpty(annotation.getNameAttribute())) {
      entity.removeAnnotation(annotationType);
      entity.addAnnotation(annotation.withNameAttribute(originalName));
    } else if (annotation == null && createAnnotation) {
      entity.addAnnotation(
          Annotation.builder().type(annotationType).nameAttribute(originalName).build());
    }
  }

  protected List<Type> getParentInterfaces(Type type) {
    return getParentInterfaces(type, false);
  }

  protected static List<Type> getParentInterfaces(Type type, boolean transitive) {
    Set<TypeReference> parentInterfaceReferences =
        type.isInterface() ? type.getExtendedTypes() : type.getImplementedTypes();

    return parentInterfaceReferences.stream()
        .map(TypeReference::getTypeDeclaration)
        .filter(Objects::nonNull)
        .flatMap(
            t -> {
              List<Type> types = Lists.newArrayList(t);
              if (transitive) {
                types.addAll(getParentInterfaces(t, true));
              }
              return types.stream();
            })
        .collect(Collectors.toList());
  }
}
