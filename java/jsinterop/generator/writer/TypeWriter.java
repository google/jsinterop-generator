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
 */
package jsinterop.generator.writer;

import static com.google.common.collect.Iterables.transform;
import static java.util.stream.Collectors.toList;
import static jsinterop.generator.model.EntityKind.CONSTRUCTOR;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Ordering;
import java.util.function.Function;
import jsinterop.generator.model.Entity;
import jsinterop.generator.model.Field;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Method.Parameter;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;

/** TypeWriter generates java code for {@link Type} instances. */
public class TypeWriter {

  private static final Function<Parameter, String> PARAM_STRINGIFIER =
      p -> p.getType().getTypeName();

  private static final Function<Method, String> METHOD_STRINGIFIER =
      m ->
          (m.getKind() == CONSTRUCTOR ? "" : m.getName())
              + "("
              + Joiner.on(" ").join(transform(m.getParameters(), PARAM_STRINGIFIER::apply))
              + ")";

  private static final Ordering<Method> METHOD_ORDERING =
      Ordering.natural().onResultOf(METHOD_STRINGIFIER::apply);

  private static final Ordering<TypeReference> TYPE_REFERENCE_ORDERING =
      Ordering.natural().onResultOf(TypeReference::getJavaTypeFqn);

  private static final Ordering<Type> TYPE_ORDERING =
      Ordering.natural().onResultOf(Entity::getName);

  private static final Ordering<Field> FIELD_ORDERING =
      Ordering.natural().onResultOf(Entity::getName);

  public static void emit(Type type, CodeWriter writer) {
    if (type.getEnclosingType() == null) {
      writer.setPackage(type.getPackageName());
    }

    writer
        .emitAnnotations(type.getAnnotations())
        .emit(type.getAccessModifier())
        .emit(" ")
        .emit(type.isStatic() && !type.isInterface() ? "static " : "")
        .emit(type.isInterface() ? "interface " : "class ")
        .emit(type.getName())
        .emitGenerics(type.getTypeParameters(), true);

    if (!type.getInheritedTypes().isEmpty()) {
      writer
          .emit(" extends ")
          .emitTypeReferences(
              type.getInheritedTypes().stream().sorted(TYPE_REFERENCE_ORDERING).collect(toList()));
    }

    if (!type.getImplementedTypes().isEmpty()) {
      writer
          .emit(" implements ")
          .emitTypeReferences(
              type.getImplementedTypes()
                  .stream()
                  .sorted(TYPE_REFERENCE_ORDERING)
                  .collect(toList()));
    }

    writer.emit("{").emitNewLine();

    // inner types
    type.getInnerTypes()
        .stream()
        .sorted(TYPE_ORDERING)
        .forEach(t -> emit(t, writer));

    // static fields first
    type.getFields()
        .stream()
        .filter(Field::isStatic)
        .sorted(FIELD_ORDERING)
        .distinct()
        .forEach(f -> FieldWriter.emit(f, writer, type));

    // static methods
    type.getMethods()
        .stream()
        .filter(Method::isStatic)
        .sorted(METHOD_ORDERING)
        .distinct()
        .forEach(m -> MethodWriter.emit(m, writer, type));

    // non-static fields
    type.getFields()
        .stream()
        .filter(Predicates.not(Field::isStatic))
        .sorted(FIELD_ORDERING)
        .distinct()
        .forEach(f -> FieldWriter.emit(f, writer, type));

    // constructors
    type.getConstructors()
        .stream()
        .sorted(METHOD_ORDERING)
        .distinct()
        .forEach(c -> MethodWriter.emitConstructor(c, writer, type));

    // non-static methods
    type.getMethods()
        .stream()
        .filter(Predicates.not(Method::isStatic))
        .sorted(METHOD_ORDERING)
        .distinct()
        .forEach(m -> MethodWriter.emit(m, writer, type));

    writer.emit("}").emitNewLine();
  }
}
