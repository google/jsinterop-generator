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
 */

package jsinterop.generator.closure.visitor;

import static jsinterop.generator.helper.GeneratorUtils.addSuffix;
import static jsinterop.generator.helper.GeneratorUtils.extractName;
import static jsinterop.generator.helper.GeneratorUtils.toCamelUpperCase;
import static jsinterop.generator.helper.GeneratorUtils.toSafeTypeName;
import static jsinterop.generator.model.AnnotationType.JS_FUNCTION;
import static jsinterop.generator.model.AnnotationType.JS_TYPE;

import com.google.common.collect.ImmutableList;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.jstype.FunctionType;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.JSType.Nullability;
import com.google.javascript.rhino.jstype.RecordType;
import com.google.javascript.rhino.jstype.StaticTypedSlot;
import java.util.regex.Pattern;
import jsinterop.generator.closure.helper.GenerationContext;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.EntityKind;
import jsinterop.generator.model.Type;

/** A visitor that collects anonymous record types and creates a java interface for each one. */
public class AnonymousTypeCollector extends AbstractClosureVisitor {
  private static final ImmutableList<String> RECORD_TYPE_SUFFIXES_TO_REMOVE =
      ImmutableList.of("type");
  private static final ImmutableList<String> FUNCTION_TYPE_SUFFIXES_TO_REMOVE =
      ImmutableList.of("fn");
  private static final Pattern THIS_TYPE_DEFINITION = Pattern.compile("\\(this:[a-zA-Z_]+, *");

  private String currentNameForRecordType;
  private String currentNameForFunctionType;

  public AnonymousTypeCollector(GenerationContext context) {
    super(context);
  }

  @Override
  protected void pushCurrentJavaType(JSType jsType) {
    if (jsType.isRecordType() || isAnonymousFunctionType(jsType)) {
      // We are collecting these types and there is no java type associated to them yet.
      // after creation, the visitor will push the type by calling
      // pushCurrentJavaType(Type javaType)
      return;
    }

    super.pushCurrentJavaType(jsType);
  }

  @Override
  protected boolean visitMethod(FunctionType method, boolean isStatic) {
    // the return type will be visited first. The parameters will reset the name afterwards.
    String methodName = extractName(method.getDisplayName());
    initNameForRecordType(methodName, "ReturnType");
    initNameForFunctionType(methodName);
    return true;
  }

  @Override
  protected boolean visitField(StaticTypedSlot property, boolean isStatic) {
    initNameForRecordType(property.getName(), isStatic ? "Type" : "FieldType");
    initNameForFunctionType(property.getName());
    return true;
  }

  @Override
  protected boolean visitRecordType(String jsFqn, RecordType recordType) {
    return visitRecordOrFunctionType(
        jsFqn,
        recordType,
        currentNameForRecordType,
        Annotation.builder()
            .type(JS_TYPE)
            .isNativeAttribute(true)
            .nameAttribute("?")
            .build());
  }

  @Override
  protected boolean visitFunctionType(String name, FunctionType type) {
    if (visitRecordOrFunctionType(
        name, type, currentNameForFunctionType, Annotation.builder().type(JS_FUNCTION).build())) {
      // Just like for methods, the return type will be visited first and the parameters will reset
      // the names. A function type will be converted to an interface with one method named
      // onInvoke. There is no added value to add onInvoke on the name of synthetic types defined
      // inside this function type.
      initNameForRecordType("", "ReturnType");
      initNameForFunctionType("");
      return true;
    }

    return false;
  }

  private boolean visitRecordOrFunctionType(
      String typeName, JSType recordOrFunctionType, String currentName, Annotation annotation) {
    if (getJavaTypeRegistry().containsJavaType(recordOrFunctionType)) {
      // Named structural types (with a @typedef) have already been processed.
      // Visit children only if we are visiting the typedef definition (case where typeName is
      // not null) because they can define anonymous RecordTypes/FunctionType. We don't need to
      // visit members again when we reach a reference to the named types (case  where typeName
      // is null)
      super.pushCurrentJavaType(recordOrFunctionType);
      return typeName != null;
    }

    Type javaType = new Type(EntityKind.INTERFACE);
    javaType.setNativeFqn(
        cleanThisFromNativeFqn(recordOrFunctionType.toAnnotationString(Nullability.IMPLICIT)));
    javaType.addAnnotation(annotation);
    javaType.setSynthetic(true);
    javaType.setStructural(true);

    javaType.setName(
        toSafeTypeName(toCamelUpperCase(currentName), getCurrentJavaType().getInnerTypesNames()));

    // If the synthetic type is part of an UnionType, we won't add the type as inner type of
    // the UnionType helper type (created later) because if someone introduces a UnionType to an
    // existing method or field, the package name of the synthetic type will change and that
    // will introduce a breaking change.
    getCurrentJavaType().addInnerType(javaType);
    getJavaTypeRegistry().registerJavaType(recordOrFunctionType, javaType);

    pushCurrentJavaType(javaType);

    return true;
  }

  /**
   * TODO(b/35712194): remove this when the bug is fixed. We need this for Promise. Promise redefine
   * callback function of IThenable interface by adding a "this" type definition. The callback types
   * don't match anymore and cannot be unified later on.
   */
  private String cleanThisFromNativeFqn(String nativeFqn) {
    return THIS_TYPE_DEFINITION.matcher(nativeFqn).replaceFirst("(");
  }

  @Override
  protected boolean visitParameter(Node parameter, FunctionType owner, int index) {
    String ownerName;
    String paramName;
    if (isAnonymousFunctionType(owner)) {
      // FunctionType case. There is only one method called "onInvoke" and adding method name
      // doesn't make it unique.
      ownerName = "";
      // The java method name will be onInvoke and we need to use it in order to find the parameter
      // name replacement if provided.
      paramName = getParameterNameByMethodName(owner, index, "onInvoke");
    } else {
      // Extract method name as the owner from the fully qualified method name.
      ownerName = extractName(owner.getDisplayName());
      paramName = getParameterNameByMethodName(owner, index, ownerName);
    }

    String baseName = ownerName + toCamelUpperCase(paramName);
    initNameForRecordType(baseName, "Type");
    initNameForFunctionType(baseName);
    return true;
  }

  private String getParameterNameByMethodName(FunctionType owner, int index, String methodName) {
    String methodFqn = getCurrentJavaType().getJavaFqn() + "." + methodName;
    String paramName = getParameterInfo(owner, index, methodFqn).getJavaName();
    // By convention, optional parameters are prefixed with "opt_", remove this prefix to clean up a
    // bit the name of the synthetic type.
    return paramName.startsWith("opt_") ? paramName.substring(4) : paramName;
  }

  private void initNameForRecordType(String baseName, String suffix) {
    currentNameForRecordType = addSuffix(baseName, suffix, RECORD_TYPE_SUFFIXES_TO_REMOVE);
  }

  private void initNameForFunctionType(String baseName) {
    currentNameForFunctionType = addSuffix(baseName, "Fn", FUNCTION_TYPE_SUFFIXES_TO_REMOVE);
  }
}
