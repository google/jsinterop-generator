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

import static jsinterop.generator.helper.GeneratorUtils.extractName;

import com.google.common.collect.Sets;
import com.google.javascript.jscomp.TypedScope;
import com.google.javascript.rhino.JSDocInfo;
import com.google.javascript.rhino.JSDocInfo.Visibility;
import com.google.javascript.rhino.jstype.FunctionType;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.Property;
import com.google.javascript.rhino.jstype.RecordType;
import com.google.javascript.rhino.jstype.StaticTypedSlot;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import jsinterop.generator.closure.helper.GenerationContext;
import jsinterop.generator.model.AccessModifier;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.AnnotationType;
import jsinterop.generator.model.Field;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Parameter;

/** Collect type information. */
public class MemberCollector extends AbstractClosureVisitor {

  // JsCompiler considers two RecordTypes as identical if they have the same structure.
  // In our case, we need to differentiate RecordType based on their definition and not based on
  // their structure.
  private final Set<JSType> visitedTypes = Sets.newIdentityHashSet();
  private final Deque<Method> currentJavaMethodDeque = new ArrayDeque<>();
  private final Map<String, String> parameterNameMapping;

  public MemberCollector(GenerationContext ctx) {
    super(ctx);
    // use of LinkedHashMap for always reporting unused entities in the same order
    parameterNameMapping = new LinkedHashMap<>(getContext().getNameMapping());
  }

  @Override
  public void accept(TypedScope scope) {
    super.accept(scope);

    if (!parameterNameMapping.isEmpty()) {
      getContext().getProblems().warning("Unused parameter name mapping: %s", parameterNameMapping);
    }
  }

  @Override
  protected boolean visitRecordType(String jsFqn, RecordType type) {
    // Named record type are visited several times (when we visit its definition and all
    // references to the FunctionType). We just need to visit them once.
    return visitedTypes.add(type);
  }

  @Override
  protected boolean visitFunctionType(String name, FunctionType type) {
    // Named FunctionType can be visited several times (when we visit its definition and all
    // references to the FunctionType). We just need to visit them once.
    if (visitedTypes.add(type)) {
      Method jsFunction = new Method();
      jsFunction.setName("onInvoke");
      jsFunction.setReturnType(getJavaTypeRegistry().createTypeReference(type.getReturnType()));
      getCurrentJavaType().addMethod(jsFunction);

      currentJavaMethodDeque.push(jsFunction);

      return true;
    }

    return false;
  }

  @Override
  @SuppressWarnings("ReferenceEquality")
  protected void endVisitFunctionType(String name, FunctionType type) {
    // Function type are converted to JsFunction interface with only one method
    // If we are visiting that method, pop it from the deque
    if (getCurrentJavaType().getMethods().get(0) == currentJavaMethodDeque.peek()) {
      currentJavaMethodDeque.pop();
    }
  }

  @Override
  protected boolean visitConstructor(FunctionType constructor) {
    Method constructorMethod = Method.newConstructor();
    if (constructor.getJSDocInfo().getVisibility() == Visibility.PRIVATE) {
      constructorMethod.setAccessModifier(AccessModifier.PRIVATE);
    }

    getCurrentJavaType().addMethod(constructorMethod);

    currentJavaMethodDeque.push(constructorMethod);
    return true;
  }

  @Override
  protected void endVisitConstructor(FunctionType constructor) {
    Method constructorMethod = currentJavaMethodDeque.pop();

    // if the type is currently extended, copy the constructor on the extension point
    if (getJavaTypeRegistry().containsExtensionType(getCurrentJavaType())) {
      getJavaTypeRegistry()
          .getExtensionType(getCurrentJavaType())
          .addConstructor(Method.from(constructorMethod));
    }
  }

  @Override
  protected boolean visitField(StaticTypedSlot jsField, boolean isStatic) {
    Field field =
        Field.create(
            extractName(jsField.getName()),
            getJavaTypeRegistry().createTypeReference(jsField.getType()),
            isConstant(jsField),
            isStatic);

    if (jsField.getJSDocInfo() != null && jsField.getJSDocInfo().isDeprecated()) {
      field.addAnnotation(Annotation.builder().type(AnnotationType.DEPRECATED).build());
    }

    getCurrentJavaType().addField(field);
    return true;
  }

  @Override
  protected boolean visitMethod(FunctionType method, boolean isStatic) {
    FunctionType jsMethod = method.toMaybeFunctionType();

    Method javaMethod = new Method();
    if (method.getJSDocInfo() != null && method.getJSDocInfo().isDeprecated()) {
      javaMethod.addAnnotation(Annotation.builder().type(AnnotationType.DEPRECATED).build());
    }
    javaMethod.setName(extractName(jsMethod.getDisplayName()));
    javaMethod.setStatic(isStatic);

    javaMethod.setReturnType(getJavaTypeRegistry().createTypeReference(jsMethod.getReturnType()));

    getCurrentJavaType().addMethod(javaMethod);

    currentJavaMethodDeque.push(javaMethod);
    return true;
  }

  @Override
  protected void endVisitMethod(FunctionType method) {
    currentJavaMethodDeque.pop();
  }

  @Override
  protected boolean visitParameter(
      FunctionType.Parameter parameter, FunctionType owner, int index) {
    Method currentMethod = currentJavaMethodDeque.peek();

    String parentFqn = getCurrentJavaType().getJavaFqn() + "." + currentMethod.getName();

    ParameterNameInfo parameterInfo = getParameterInfo(owner, index, parentFqn);

    currentMethod.addParameter(convertParameter(parameter, parameterInfo.getJavaName()));

    parameterNameMapping.remove(parameterInfo.getJavaFqn());

    return true;
  }

  private Parameter convertParameter(FunctionType.Parameter jsParameter, String parameterName) {
    return Parameter.builder()
        .setName(parameterName)
        .setType(getJavaTypeRegistry().createTypeReference(jsParameter.getJSType()))
        .setVarargs(jsParameter.isVariadic())
        .setOptional(jsParameter.isOptional())
        .build();
  }

  @Override
  protected boolean visitEnumMember(Property enumMember) {
    Field enumField = new Field();
    enumField.setType(getJavaTypeRegistry().createTypeReference(enumMember.getType()));
    enumField.setName(enumMember.getName());
    enumField.setEnumConstant(true);

    getCurrentJavaType().addField(enumField);
    return true;
  }

  private static boolean isConstant(StaticTypedSlot jsField) {
    JSDocInfo jsdoc = jsField.getJSDocInfo();
    return jsdoc != null && jsdoc.isConstant();
  }
}
