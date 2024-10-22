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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static jsinterop.generator.helper.GeneratorUtils.extractName;

import com.google.common.collect.Streams;
import com.google.javascript.jscomp.NodeUtil;
import com.google.javascript.jscomp.TypedScope;
import com.google.javascript.jscomp.TypedVar;
import com.google.javascript.rhino.StaticSourceFile;
import com.google.javascript.rhino.jstype.EnumType;
import com.google.javascript.rhino.jstype.FunctionType;
import com.google.javascript.rhino.jstype.FunctionType.Parameter;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.JSTypeRegistry;
import com.google.javascript.rhino.jstype.ObjectType;
import com.google.javascript.rhino.jstype.Property;
import com.google.javascript.rhino.jstype.RecordType;
import com.google.javascript.rhino.jstype.StaticTypedSlot;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.stream.Collectors;
import jsinterop.generator.closure.helper.ClosureTypeRegistry;
import jsinterop.generator.closure.helper.GenerationContext;
import jsinterop.generator.model.PredefinedTypes;
import jsinterop.generator.model.Type;

abstract class AbstractClosureVisitor {
  private final GenerationContext context;
  private final Set<String> externFileNamesSet;
  private final Deque<Type> currentJavaTypeDeque = new ArrayDeque<>();

  AbstractClosureVisitor(GenerationContext context) {
    this.context = context;
    externFileNamesSet =
        Streams.concat(
                context.getExternDependencyFiles().stream(), context.getSourceFiles().stream())
            .map(StaticSourceFile::getName)
            .collect(Collectors.toSet());
  }

  public void accept(TypedScope scope) {
    pushCurrentJavaType(scope.getTypeOfThis());
    if (visitTopScope(scope)) {
      for (TypedVar symbol : scope.getVarIterable()) {
        if (isDefinedInExternFiles(symbol) && isNotNamespaced(symbol)) {
          accept(symbol, true);
        }
      }
    }
    popCurrentJavaType();
  }

  private static boolean isNotNamespaced(TypedVar symbol) {
    return !symbol.getName().contains(".");
  }

  private void accept(StaticTypedSlot var, boolean isStatic) {
    JSType type = var.getType();

    if ((type.isInterface() || type.isConstructor())
        && !type.isInstanceType()
        && toFunctionType(type).getSource() != null
        && !isTypeAlias(var)) {
      acceptClassOrInterface(toFunctionType(type));
    } else if (isModule(type)) {
      acceptModule(var);
    } else if (isTypedef(var)) {
      acceptTypedef(var);
    } else if (type.isEnumType()) {
      acceptEnumType(toEnumType(type));
    } else if (isTypeAlias(var)) {
      // We don't process type alias.
      //   /** @constructor */ function Foo() {};
      //   /** @const */ var Bar = Foo;
    } else {
      acceptMember(var, isStatic);
    }
  }

  /** Returns {@code true} if var is a type alias (like var mozFooType = FooType). */
  private static boolean isTypeAlias(StaticTypedSlot var) {
    if (var.getName() == null || var.getType() == null || var.getType().getDisplayName() == null) {
      return false;
    }

    return var.getType().isConstructor()
        && !extractName(var.getName()).equals(extractName(var.getType().getDisplayName()));
  }

  private void acceptType(JSType type) {
    type = type.restrictByNotNullOrUndefined();

    if (type.isRecordType()) {
      acceptRecordType(toRecordType(type), null);
    } else if (isAnonymousFunctionType(type)) {
      acceptFunctionType(toFunctionType(type), null);
    } else if (type.isTemplatizedType()) {
      type.toMaybeTemplatizedType().getTemplateTypes().forEach(this::acceptType);
    } else if (type.isUnionType()) {
      type.toMaybeUnionType().getAlternates().forEach(this::acceptType);
    }
  }

  private void acceptEnumType(EnumType type) {
    pushCurrentJavaType(type);

    if (visitEnumType(type)) {
      type.getOwnPropertyNames()
          .stream()
          .sorted()
          .forEach(propertyName -> acceptEnumMember(type.getOwnSlot(propertyName)));
    }

    endVisitEnumType(type);

    popCurrentJavaType();
  }

  private void acceptEnumMember(Property enumMember) {
    visitEnumMember(enumMember);
  }

  private void acceptRecordType(RecordType type, String name) {
    pushCurrentJavaType(type);

    if (visitRecordType(name, type)) {
      acceptProperties(type, false);
    }

    endVisitRecordType(name, type);

    popCurrentJavaType();
  }

  private void acceptFunctionType(FunctionType type, String name) {
    if (type.isConstructor()) {
      // constructor function {function(new:Foo)} are mapped to JsConstructorFn type and don't
      // generate any java type
      return;
    }

    pushCurrentJavaType(type);

    if (visitFunctionType(name, type)) {
      acceptType(type.getReturnType());
      acceptParameters(type);
    }

    endVisitFunctionType(name, type);

    popCurrentJavaType();
  }

  private void acceptTypedef(StaticTypedSlot typedef) {
    // The type linked to symbol is not the type represented in the @typedef annotations.
    JSType realType = null;
    String name = null;

    if (typedef instanceof Property) {
      // In the namespaced typedef scenario, use the fully qualified name (including namespace) of
      // the node to accurately retrieve the corresponding type information.
      Property namespacedTypeDef = (Property) typedef;
      realType = getJsTypeRegistry().getType(null, namespacedTypeDef.getNode().getQualifiedName());
      name = namespacedTypeDef.getNode().getQualifiedName();
    } else {
      realType = getJsTypeRegistry().getType(typedef.getScope(), typedef.getName());
      name = typedef.getName();
    }
    realType = realType.restrictByNotNullOrUndefined();

    if (realType.isRecordType()) {
      acceptRecordType(toRecordType(realType), name);
    } else if (isAnonymousFunctionType(realType)) {
      acceptFunctionType(toFunctionType(realType), name);
    } else {
      // we are in a case where typedef is used as an alias and doesnt define any RecordType or
      // FunctionType.
      // ex:  /** @typedef {string|number} */ var StringOrNumber;
      // The JsCompiler have already inlined all typedefs at call site, so we don't need to visit
      // the typedef definition.
      return;
    }
  }

  private void acceptModule(StaticTypedSlot module) {
    pushCurrentJavaType(module.getType());

    if (visitModule(module)) {
      acceptProperties((ObjectType) module.getType(), true);
    }

    endVisitModule(module);

    popCurrentJavaType();
  }

  private static boolean isModule(JSType type) {
    return type.isLiteralObject() && type.getJSDocInfo().isConstant();
  }

  private void acceptClassOrInterface(FunctionType type) {
    if (PredefinedTypes.isPredefinedType(type.getNormalizedReferenceName())) {
      // Don't traverse types that map to existing Java abstractions (e.g. JsPropertyMap from
      // JsInterop-base).
      return;
    }

    pushCurrentJavaType(type.getInstanceType());

    if (visitClassOrInterface(type)) {
      if (type.isConstructor()) {
        acceptConstructor(type);
      }

      // Static properties.
      acceptProperties(type, true);
      // Instance properties.
      acceptProperties(type.getPrototype(), false);
    }

    endVisitClassOrInterface(type);

    popCurrentJavaType();
  }

  private void acceptConstructor(FunctionType constructorFunction) {
    if (visitConstructor(constructorFunction)) {
      acceptParameters(constructorFunction);
    }
    endVisitConstructor(constructorFunction);
  }

  private void acceptProperties(ObjectType owner, boolean isStatic) {
    owner
        .getOwnPropertyNames()
        .stream()
        .sorted()
        .forEach(
            propertyName -> {
              if (!"prototype".equals(propertyName)) {
                accept(owner.getOwnSlot(propertyName), isStatic);
              }
            });
  }

  protected void acceptMember(StaticTypedSlot member, boolean isStatic) {
    JSType propertyType = member.getType();
    // Check if this member is an extension of the API of a type provided by an external third party
    // library. If it's the case, use the extension class if it has already been created.
    boolean extensionTypePushed = maybePushCurrentExtensionType(member);

    // We are visiting field or function belonging to a Type or a namespace.
    // For correctly converting functions, we have to differentiate 2 cases:
    //   /**
    //    * @type {function(Event):boolean}
    //    */
    //    Foo.prototype.callback;
    //
    //   /**
    //    * @param {Event} evt
    //    * @return {boolean}
    //    */
    //    Foo.prototype.callbackFunction = function(evt) {};
    //
    // The type of both symbols are function type but the first one need to be converted as a Field
    // with type a JsFunction interface. The second symbol need to to be converted as a Method.
    // The only way to differentiate these two cases are to test if the function type has a name or
    // not.
    if (propertyType.isOrdinaryFunction() && !isAnonymousFunctionType(propertyType)) {
      FunctionType method = toFunctionType(propertyType);
      if (visitMethod(method, isStatic)) {
        // can contain anonymous RecordType/FunctionType to visit
        acceptType(method.getReturnType());
        acceptParameters(method);
      }
      endVisitMethod(method);
    } else {
      if (visitField(member, isStatic)) {
        acceptType(propertyType);
      }
    }

    if (extensionTypePushed) {
      popCurrentJavaType();
    }
  }

  private void acceptParameters(FunctionType owner) {
    int index = 0;
    for (Parameter parameter : owner.getParameters()) {
      acceptParameter(parameter, owner, index++);
    }
  }

  private void acceptParameter(Parameter parameter, FunctionType owner, int index) {
    if (visitParameter(parameter, owner, index)) {
      acceptType(parameter.getJSType());
    }
  }

  protected boolean visitParameter(Parameter parameter, FunctionType owner, int index) {
    return true;
  }

  protected void endVisitFunctionType(String name, FunctionType type) {}

  protected boolean visitFunctionType(String name, FunctionType type) {
    return true;
  }

  protected boolean visitRecordType(String jsFqn, RecordType type) {
    return true;
  }

  protected void endVisitRecordType(String name, RecordType type) {}

  protected boolean visitConstructor(FunctionType constructor) {
    return true;
  }

  protected void endVisitConstructor(FunctionType constructor) {}

  protected boolean visitTopScope(TypedScope scope) {
    return true;
  }

  protected boolean visitMethod(FunctionType method, boolean isStatic) {
    return true;
  }

  protected void endVisitMethod(FunctionType method) {}

  protected boolean visitField(StaticTypedSlot property, boolean isStatic) {
    return true;
  }

  protected boolean visitClassOrInterface(FunctionType type) {
    return true;
  }

  protected void endVisitClassOrInterface(FunctionType type) {}

  protected boolean visitModule(StaticTypedSlot module) {
    return true;
  }

  protected void endVisitModule(StaticTypedSlot module) {}

  protected boolean visitEnumType(EnumType type) {
    return true;
  }

  protected void endVisitEnumType(EnumType type) {}

  protected boolean visitEnumMember(Property enumMember) {
    return true;
  }

  protected ClosureTypeRegistry getJavaTypeRegistry() {
    return context.getTypeRegistry();
  }

  protected GenerationContext getContext() {
    return context;
  }

  protected Type getCurrentJavaType() {
    checkState(!currentJavaTypeDeque.isEmpty());
    return currentJavaTypeDeque.peek();
  }

  protected static class ParameterNameInfo {
    private final String name;
    private final String fqn;

    ParameterNameInfo(String name, String fqn) {
      this.name = name;
      this.fqn = fqn;
    }

    /**
     * Return the fully qualified name of the parameter in java.
     *
     * <p>Ex: <code>
     *   interface Foo {
     *     void bar(String baz);
     *   }
     * </code> The java fqn of the parameter baz is: Foo.bar.baz;
     */
    public String getJavaFqn() {
      return fqn;
    }

    /**
     * Return the name of the parameter that will be used in the generated java code.
     *
     * <p>It could be generated if the closure code doesn't specify a name for the parameter: <code>
     *   /** @type {function(string):undefined} *\/
     *   var myFunction;     *
     * </code> The first parameter of the function myFunction will be generated to <code>p0</code>
     */
    public String getJavaName() {
      return name;
    }
  }

  protected ParameterNameInfo getParameterInfo(
      FunctionType functionType, int parameterIndex, String parentFqn) {

    if (functionType.getSource() != null) {
      String originalName =
          NodeUtil.getFunctionParameters(functionType.getSource())
              .getChildAtIndex(parameterIndex)
              .getString();
      return getParameterInfo(parentFqn, originalName, false);
    }

    // functionType doesn't have source, it's a anonymous function type.
    // e.g.: /** @type {function(Event):boolean} */ var eventCallback;
    // Parameters for anonymous FunctionType don't have names.
    return getParameterInfo(parentFqn, "p" + parameterIndex, true);
  }

  private ParameterNameInfo getParameterInfo(
      String parentFqn, String parameterName, boolean warnMissingName) {
    String parameterFqn = parentFqn + "." + parameterName;
    if (context.getNameMapping().containsKey(parameterFqn)) {
      return new ParameterNameInfo(context.getNameMapping().get(parameterFqn), parameterFqn);
    }
    if (warnMissingName) {
      context
          .getProblems()
          .info(
              "No name provided for parameter '%s'. Name can be specified in a name mapping file.",
              parameterFqn);
    }

    return new ParameterNameInfo(parameterName, parameterFqn);
  }

  private boolean isDefinedInExternFiles(TypedVar symbol) {
    return externFileNamesSet.contains(symbol.getInputName());
  }

  private static boolean isTypedef(StaticTypedSlot var) {
    return var.getJSDocInfo() != null && var.getJSDocInfo().hasTypedefType();
  }

  private JSTypeRegistry getJsTypeRegistry() {
    return getContext().getCompiler().getTypeRegistry();
  }

  protected void popCurrentJavaType() {
    currentJavaTypeDeque.pop();
  }

  protected void pushCurrentJavaType(JSType jsType) {
    pushCurrentJavaType(getJavaTypeRegistry().getJavaType(jsType));
  }

  protected void pushCurrentJavaType(Type javaType) {
    checkNotNull(javaType, "Cannot push a null java type");
    currentJavaTypeDeque.push(javaType);
  }

  private static FunctionType toFunctionType(JSType type) {
    return checkNotNull(type.toMaybeFunctionType());
  }

  private static RecordType toRecordType(JSType type) {
    return checkNotNull(type.toMaybeRecordType());
  }

  private static EnumType toEnumType(JSType type) {
    return checkNotNull(type.toMaybeEnumType());
  }

  private boolean maybePushCurrentExtensionType(StaticTypedSlot member) {
    if (isApiExtension(member)) {
      Type extensionType = getJavaTypeRegistry().getExtensionType(getCurrentJavaType());
      pushCurrentJavaType(extensionType);
      return true;
    }
    return false;
  }

  protected boolean isApiExtension(StaticTypedSlot member) {
    if (!getCurrentJavaType().isExtern()) {
      return false;
    }

    checkState(
        member.getDeclaration() != null,
        "No declaration found for member [%s]. Please fix the extern file. A class must implement "
            + "all methods from its super interfaces.",
        member);

    return !getContext()
        .getExternDependencyFiles()
        .contains(member.getDeclaration().getSourceFile());
  }

  /**
   * Returns true if the type is an anonymous function type defined in closure by <code>
   * {function(string):number}</code>. Returns false otherwise.
   */
  protected boolean isAnonymousFunctionType(JSType type) {
    return type.isFunctionType() && type.getDisplayName() == null;
  }
}
