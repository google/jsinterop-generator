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

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.AnnotationType;
import jsinterop.generator.model.Entity;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;

/** Abstract class for all visitors that aims to visit our Java model. */
public abstract class AbstractModelVisitor {

  public abstract void applyTo(Program program);

  static void addAnnotationNameAttributeIfNotEmpty(
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

  static List<Type> getParentInterfaces(Type type) {
    return getParentInterfaces(type, false);
  }

  static List<Type> getParentInterfaces(Type type, boolean transitive) {
    List<TypeReference> parentInterfaceReferences =
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
