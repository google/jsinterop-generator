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

import static jsinterop.generator.model.AnnotationType.JS_METHOD;
import static jsinterop.generator.model.AnnotationType.JS_PROPERTY;
import static jsinterop.generator.model.EntityKind.METHOD;
import static jsinterop.generator.model.PredefinedTypes.OBJECT;
import static jsinterop.generator.model.PredefinedTypes.VOID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jsinterop.generator.helper.ModelHelper;
import jsinterop.generator.model.AbstractVisitor;
import jsinterop.generator.model.AccessModifier;
import jsinterop.generator.model.AnnotationType;
import jsinterop.generator.model.Entity;
import jsinterop.generator.model.Field;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.ModelVisitor;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;
import jsinterop.generator.model.TypeVariableReference;

/**
 * Visitor doing some cleaning tasks on Type in order to ensure that generated java types are
 * compilable.
 *
 * <p>Typescript uses a separate namespace for static and instance members. It repeats static fields
 * on the instance side of a class to make them available on an instance. The resulting java class
 * has a static and instance field with the same name and it's not valid in Java. This visitor
 * remove the duplicated instance fields.
 *
 * <p>If a instance member has the same name that another static member, rename the static one to
 * avoid a clash.
 *
 * <p>Use <code>Object</code> instead of <code>void</code> for field type.
 *
 * <p>As we convert optional parameter to method overloads, some generics defined at method level
 * are not used in the parameter. This visitor will remove the unused generics defined on methods
 *
 * <p>Remove single public default constructor since it doesn't need to be explicit. If we need it
 * for extension purpose, it will be recreated in {@link ConstructorVisitor}
 */
public class MembersClassCleaner implements ModelVisitor {
  private interface EntityStringifier<T extends Entity> {
    String toString(T entity);
  }

  private static class FieldStringifier implements EntityStringifier<Field> {
    @Override
    public String toString(Field entity) {
      return entity.getName();
    }
  }

  private static class MethodStringifier implements EntityStringifier<Method> {
    @Override
    public String toString(Method entity) {

      return entity.getName()
          + "("
          + entity.getParameters().stream()
              .map(input -> input.getType().getJniSignature())
              .collect(Collectors.joining(","))
          + ")";
    }
  }

  private Set<TypeReference> generics;

  @Override
  public void applyTo(Program program) {
    program.accept(
        new AbstractVisitor() {
          @Override
          public boolean enterType(Type type) {
            if (type.isInterface()) {
              return true;
            }

            removeSingleDefaultConstructor(type);
            removeDuplicatedFields(type);
            renameSameEntities(type.getFields(), new FieldStringifier());
            renameSameEntities(type.getMethods(), new MethodStringifier());

            return true;
          }

          @Override
          public boolean enterField(Field field) {
            if (field.getType().isReferenceTo(VOID)) {
              field.setType(OBJECT.getReference());
            }

            return true;
          }

          @Override
          public boolean enterMethod(Method method) {
            generics = new HashSet<>();
            return true;
          }

          @Override
          public boolean enterTypeVariableReference(TypeVariableReference typeReference) {
            if (generics != null) {
              generics.add(typeReference);
            }
            return true;
          }

          @Override
          public void exitMethod(Method method) {
            List<TypeReference> methodGenerics = new ArrayList<>();

            for (TypeReference typeReference : method.getTypeParameters()) {
              if (generics.contains(typeReference)) {
                methodGenerics.add(typeReference);
              }
            }

            method.setTypeParameters(methodGenerics);

            generics = null;
          }

          private <T extends Entity> void renameSameEntities(
              Collection<T> entities, EntityStringifier<T> stringifier) {
            Set<String> nonStaticEntities = new HashSet<>();

            for (T entity : entities) {
              if (!entity.isStatic()) {
                nonStaticEntities.add(stringifier.toString(entity));
              }
            }

            if (nonStaticEntities.isEmpty() || nonStaticEntities.size() == entities.size()) {
              return;
            }

            for (T entity : entities) {
              if (entity.isStatic() && nonStaticEntities.contains(stringifier.toString(entity))) {
                String name = entity.getName();
                entity.setName(name + "_STATIC");

                // TODO(dramaix): add a javadoc above the field explaining why it was renamed
                AnnotationType annotationType =
                    entity.getKind() == METHOD ? JS_METHOD : JS_PROPERTY;

                ModelHelper.addAnnotationNameAttributeIfNotEmpty(
                    entity, name, annotationType, true);
              }
            }
          }
        });
  }

  private static void removeSingleDefaultConstructor(Type type) {
    if (type.getConstructors().size() == 1) {
      Method singleConstructor = type.getConstructors().get(0);
      if (singleConstructor.getParameters().isEmpty()
          && singleConstructor.getAccessModifier() == AccessModifier.PUBLIC) {
        type.removeMethod(singleConstructor);
      }
    }
  }

  private static void removeDuplicatedFields(Type type) {
    Set<String> staticFieldKeys = getStaticFieldKeys(type);

    for (Field field : type.getFields()) {
      if (!field.isStatic() && staticFieldKeys.contains(getKey(field))) {
        field.removeFromParent();
      }
    }
  }

  private static Set<String> getStaticFieldKeys(Type type) {
    Set<String> staticFields = new HashSet<>();

    for (Field field : type.getFields()) {
      if (field.isStatic()) {
        staticFields.add(getKey(field));
      }
    }

    return staticFields;
  }

  private static String getKey(Field field) {
    return field.getType().getJniSignature() + "|" + field.getName();
  }
}
