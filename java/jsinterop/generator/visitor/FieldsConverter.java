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
import static jsinterop.generator.model.AccessModifier.DEFAULT;
import static jsinterop.generator.model.AnnotationType.JS_OVERLAY;
import static jsinterop.generator.model.AnnotationType.JS_PROPERTY;
import static jsinterop.generator.model.AnnotationType.JS_TYPE;
import static jsinterop.generator.model.PredefinedTypeReference.BOOLEAN;
import static jsinterop.generator.model.PredefinedTypeReference.VOID;

import com.google.common.base.Preconditions;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.EntityKind;
import jsinterop.generator.model.Field;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;

/**
 * Correct fields defined on classes and interfaces.
 *
 * <p>Converts static constant fields of classes and interfaces by creating static final JsOverlay
 * fields. Those fields values are initialized by referring to non-final static field that expose
 * the native fields.
 *
 * <p>Converts non-static fields defined on an interface to a pair of getter and setter. If the
 * field is read-only, only the getter is generated. As non static interface fields are converted to
 * methods, this visitor take care of adding method implementations on a class that implements the
 * interface.
 */
public class FieldsConverter extends AbstractModelVisitor {
  private final boolean useBeanConvention;
  // When we visit a class, we ensure that implemented interfaces are visited first. Keep a track
  // of visited interface in order to avoid visiting them several times.
  private final Set<Type> alreadyVisitedType = new HashSet<>();
  private Program program;

  public FieldsConverter(boolean useBeanConvention) {
    this.useBeanConvention = useBeanConvention;
  }

  @Override
  public boolean visit(Program program) {
    this.program = program;
    return true;
  }

  @Override
  public boolean visit(Type type) {
    if (!alreadyVisitedType.add(type)) {
      return false;
    }
    if (!type.hasAnnotation(JS_TYPE)) {
      // nothing to fix but the type might contain inner types to fix
      return true;
    }

    processFinalStaticFields(type);

    if (type.isInterface()) {
      // convert non static fields of interface to pair of getter/setter.
      List<Field> nonStaticFields =
          type.getFields().stream().filter(not(Field::isStatic)).collect(Collectors.toList());

      nonStaticFields.forEach(
          field -> {
            // getter method to access field
            type.addMethod(createAccessorMethod(field, false, useBeanConvention));
            if (!field.isNativeReadOnly()) {
              // setter method to access field
              type.addMethod(createAccessorMethod(field, true, useBeanConvention));
            }
          });

      type.getFields().removeAll(nonStaticFields);

    } else if (type.isClass()) {
      // Classes can implement interfaces with non static fields. Now that these fields have been
      // replaced with getters/setters, classes have to provide an implementation for those.

      List<Type> parentInterfaces = getParentInterfaces(type, true);
      // ensure implemented interfaces have been visited
      accept(parentInterfaces);

      // ensure implementations for newly created accessors.
      parentInterfaces
          .stream()
          .flatMap(t -> t.getMethods().stream())
          .filter(m -> m.hasAnnotation(JS_PROPERTY))
          .forEach(m -> type.addMethod(Method.from(m)));
    }

    return true;
  }

  /**
   * We convert native static fields by defining an extra native JsType that will contain private
   * static fields that target the javascript constant symbols. Then we reexpose those fields on the
   * original type by using JsOverlay static final field.
   */
  private void processFinalStaticFields(Type originalType) {
    if (originalType.isInterface()) {
      // We don't support non constant static fields on interface because interface cannot have
      // static native method.
      Preconditions.checkState(
          originalType.getFields().stream().noneMatch(f -> f.isStatic() && !f.isNativeReadOnly()),
          "Non constant static fields are not supported on interface");
    }

    Type constantWrapper = new Type(EntityKind.CLASS);
    constantWrapper.setAccessModifier(DEFAULT);
    constantWrapper.setPackageName(originalType.getPackageName());
    constantWrapper.setName(originalType.getName() + "__Constants");
    constantWrapper.setExtern(originalType.isExtern());

    Annotation jsTypeAnnotation = originalType.getAnnotation(JS_TYPE);
    if (jsTypeAnnotation.getNameAttribute() == null) {
      jsTypeAnnotation = jsTypeAnnotation.withNameAttribute(originalType.getName());
    }
    constantWrapper.addAnnotation(jsTypeAnnotation);

    originalType
        .getFields()
        .stream()
        .filter(f -> f.isStatic() && f.isNativeReadOnly())
        .forEach(
            f -> {
              Field copy = Field.from(f);
              copy.setAccessModifier(DEFAULT);
              constantWrapper.addField(copy);

              f.addAnnotation(Annotation.builder().type(JS_OVERLAY).build());
              f.setFinal(true);
              f.setInitialValue(constantWrapper.getName() + "." + f.getName());
            });

    if (!constantWrapper.getFields().isEmpty()) {
      program.addType(constantWrapper);
    }
  }

  private Method createAccessorMethod(Field field, boolean setter, boolean useBeanConvention) {
    String fieldName = field.getName();
    TypeReference fieldType = field.getType();
    String fieldNameUpperCamelCase = toCamelUpperCase(fieldName);
    boolean fieldNameIsLowerCamelCase = !fieldName.equals(fieldNameUpperCamelCase);

    Method accessor = new Method();

    if (useBeanConvention) {
      String methodPrefix = setter ? "set" : BOOLEAN == fieldType ? "is" : "get";
      accessor.setName(methodPrefix + fieldNameUpperCamelCase);
    } else {
      accessor.setName(fieldName);
    }

    if (setter) {
      accessor.addParameter(new Method.Parameter(fieldName, fieldType, false, false));
      accessor.setReturnType(VOID);
    } else {
      accessor.setReturnType(fieldType);
    }

    accessor.setStatic(field.isStatic());

    Annotation.Builder jsPropertyAnnotation = Annotation.builder().type(JS_PROPERTY);
    if (!useBeanConvention || !fieldNameIsLowerCamelCase) {
      jsPropertyAnnotation.nameAttribute(fieldName);
    }
    accessor.addAnnotation(jsPropertyAnnotation.build());

    return accessor;
  }
}
