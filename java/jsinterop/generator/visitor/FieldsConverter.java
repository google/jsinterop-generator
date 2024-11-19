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

package jsinterop.generator.visitor;

import static com.google.common.base.Predicates.not;
import static jsinterop.generator.helper.GeneratorUtils.toCamelUpperCase;
import static jsinterop.generator.model.AnnotationType.DEPRECATED;
import static jsinterop.generator.model.AnnotationType.JS_PROPERTY;
import static jsinterop.generator.model.AnnotationType.JS_TYPE;
import static jsinterop.generator.model.PredefinedTypes.BOOLEAN;
import static jsinterop.generator.model.PredefinedTypes.VOID;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jsinterop.generator.helper.ModelHelper;
import jsinterop.generator.model.AbstractVisitor;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.Field;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.ModelVisitor;
import jsinterop.generator.model.Parameter;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;

/**
 * Converts non-static fields defined on an interface to a pair of getter and setter. If the field
 * is read-only, only the getter is generated. As non static interface fields are converted to
 * methods, this visitor take care of adding method implementations on a class that implements the
 * interface.
 */
public class FieldsConverter implements ModelVisitor {
  FieldsConverter() {}

  @Override
  public void applyTo(Program program) {
    program.accept(
        new AbstractVisitor() {
          // When we visit a class, we ensure that implemented interfaces are visited first. Keep a
          // track of visited interface in order to avoid visiting them several times.
          private final Set<Type> alreadyVisitedType = new HashSet<>();

          @Override
          public boolean enterType(Type type) {
            if (!alreadyVisitedType.add(type)) {
              return false;
            }
            if (!type.hasAnnotation(JS_TYPE)) {
              // nothing to fix but the type might contain inner types to fix
              return true;
            }

            if (type.isInterface()) {
              // convert non static fields of interface to pair of getter/setter.
              List<Field> nonStaticFields =
                  type.getFields().stream()
                      .filter(not(Field::isStatic))
                      .collect(Collectors.toList());

              nonStaticFields.forEach(
                  field -> {
                    // getter method to access field
                    type.addMethod(createAccessorMethod(field, false));
                    if (!field.isNativeReadOnly()) {
                      // setter method to access field
                      type.addMethod(createAccessorMethod(field, true));
                    }
                  });

              type.removeFields(nonStaticFields);

            } else if (type.isClass()) {
              // Classes can implement interfaces with non static fields. Now that these fields have
              // been replaced with getters/setters, classes have to provide an implementation for
              // those.

              List<Type> parentInterfaces = ModelHelper.getParentInterfaces(type, true);
              // ensure implemented interfaces have been visited
              parentInterfaces.forEach(i -> i.accept(this));

              // ensure implementations for newly created accessors.
              parentInterfaces.stream()
                  .flatMap(t -> t.getMethods().stream())
                  .filter(m -> m.hasAnnotation(JS_PROPERTY))
                  .forEach(m -> type.addMethod(Method.from(m)));
            }

            return true;
          }
        });
  }

  private static Method createAccessorMethod(Field field, boolean setter) {
    String fieldName = field.getName();
    TypeReference fieldType = field.getType();
    String fieldNameUpperCamelCase = toCamelUpperCase(fieldName);
    boolean fieldNameIsLowerCamelCase = !fieldName.equals(fieldNameUpperCamelCase);

    Method accessor = new Method();

    String methodPrefix = setter ? "set" : fieldType.isReferenceTo(BOOLEAN) ? "is" : "get";
    accessor.setName(methodPrefix + fieldNameUpperCamelCase);

    if (setter) {
      accessor.addParameter(Parameter.builder().setName(fieldName).setType(fieldType).build());
      accessor.setReturnType(VOID.getReference(false));
    } else {
      accessor.setReturnType(fieldType);
    }

    accessor.setStatic(field.isStatic());

    Annotation.Builder jsPropertyAnnotation = Annotation.builder().type(JS_PROPERTY);
    if (!fieldNameIsLowerCamelCase) {
      jsPropertyAnnotation.nameAttribute(fieldName);
    }

    if (field.getAnnotation(DEPRECATED) != null) {
      accessor.addAnnotation(Annotation.builder().type(DEPRECATED).build());
    }
    accessor.addAnnotation(jsPropertyAnnotation.build());

    return accessor;
  }
}
