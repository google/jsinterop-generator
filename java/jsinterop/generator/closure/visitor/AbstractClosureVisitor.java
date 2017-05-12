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

import com.google.common.collect.Streams;
import com.google.javascript.jscomp.NodeUtil;
import com.google.javascript.jscomp.TypedScope;
import com.google.javascript.jscomp.TypedVar;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.StaticSourceFile;
import com.google.javascript.rhino.jstype.FunctionType;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.JSTypeRegistry;
import com.google.javascript.rhino.jstype.ObjectType;
import com.google.javascript.rhino.jstype.RecordType;
import com.google.javascript.rhino.jstype.StaticTypedSlot;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import jsinterop.generator.closure.helper.ClosureTypeRegistry;
import jsinterop.generator.closure.helper.GenerationContext;
import jsinterop.generator.helper.GeneratorUtils;
import jsinterop.generator.model.Type;

abstract class AbstractClosureVisitor {
  private static final Logger logger = Logger.getLogger(AbstractClosureVisitor.class.getName());

  private final GenerationContext context;
  private final Set<String> externFileNamesSet;
  private final Deque<Type> currentJavaTypeDeque = new LinkedList<>();

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

  private void accept(StaticTypedSlot<JSType> var, boolean isStatic) {
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
      // TODO Add support for enum type.
      throw new RuntimeException("Closure Enum are not supported");
    } else if (type.isConstructor()) {
      // variable defining a constructor function:
      // /**
      //  * @type {function(new:MutationObserver, function(Array<MutationRecord>))}
      //  */
      //  Window.prototype.MozMutationObserver;
      // TODO add support for constructor function.
    } else {
      acceptMember(var, isStatic);
    }
  }

  /** Returns {@code true} if var is a type alias (like var mozFooType = FooType). */
  private static boolean isTypeAlias(StaticTypedSlot<JSType> var) {
    return !GeneratorUtils.extractName(var.getName())
        .equals(GeneratorUtils.extractName(var.getType().getDisplayName()));
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

  private void acceptRecordType(RecordType type, String name) {
    pushCurrentJavaType(type);

    if (visitRecordType(name, type)) {
      acceptProperties(type, false);
    }

    endVisitRecordType(name, type);

    popCurrentJavaType();
  }

  private void acceptFunctionType(FunctionType type, String name) {
    pushCurrentJavaType(type);

    if (visitFunctionType(name, type)) {
      acceptType(type.getReturnType());
      acceptParameters(type);
    }

    endVisitFunctionType(name, type);

    popCurrentJavaType();
  }

  private void acceptTypedef(StaticTypedSlot<JSType> typedef) {
    // The type linked to symbol is not the type represented in the @typedef annotations.
    JSType realType = checkNotNull(getJsTypeRegistry().getType(typedef.getName()));

    if (realType.isRecordType()) {
      acceptRecordType(toRecordType(realType), typedef.getName());
    } else if (isAnonymousFunctionType(realType)) {
      acceptFunctionType(toFunctionType(realType), typedef.getName());
    } else {
      // we are in a case where typedef is used as an alias and doesnt define any RecordType or
      // FunctionType.
      // ex:  /** @typedef {string|number} */ var StringOrNumber;
      // The JsCompiler have already inlined all typedefs at call site, so we don't need to visit
      // the typedef definition.
      return;
    }
  }

  private void acceptModule(StaticTypedSlot<JSType> module) {
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
    pushCurrentJavaType(type.getInstanceType());

    if (visitClassOrInterface(type)) {
      if (type.isConstructor()) {
        acceptConstructor(type);
      }

      // static side
      acceptProperties(type, true);
      //instance side
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

  protected void acceptMember(StaticTypedSlot<JSType> member, boolean isStatic) {
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
    for (Node parameter : owner.getParameters()) {
      acceptParameter(parameter, owner, index++);
    }
  }

  private void acceptParameter(Node parameter, FunctionType owner, int index) {
    if (visitParameter(parameter, owner, index)) {
      acceptType(parameter.getJSType());
    }
  }

  protected boolean visitParameter(Node parameter, FunctionType owner, int index) {
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

  protected boolean visitField(StaticTypedSlot<JSType> property, boolean isStatic) {
    return true;
  }

  protected boolean visitClassOrInterface(FunctionType type) {
    return true;
  }

  protected void endVisitClassOrInterface(FunctionType type) {}

  protected boolean visitModule(StaticTypedSlot<JSType> module) {
    return true;
  }

  protected void endVisitModule(StaticTypedSlot<JSType> module) {}

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

  protected String getParameterName(
      FunctionType functionType, int parameterIndex, String parentFqn) {

    boolean generatedName = false;
    String originalName;
    if (functionType.getSource() == null) {
      // if functionType doesn't have source, it means it's a anonymous function type defined in
      // jsdoc.
      // ex: /** @type {function(Event):boolean} */ var eventCallback;
      // Parameter for anonymous FunctionType doesn't have name.
      originalName = "p" + parameterIndex;
      generatedName = true;
    } else {
      originalName =
          NodeUtil.getFunctionParameters(functionType.getSource())
              .getChildAtIndex(parameterIndex)
              .getString();
    }

    String parameterFqn = parentFqn + "." + originalName;

    if (context.getNameMapping().containsKey(parameterFqn)) {
      return context.getNameMapping().get(parameterFqn);
    }

    if (generatedName) {
      logger.warning(
          "We generated a default name for parameter "
              + parameterFqn
              + ". You can override the name by passing a name mapping file to the generator");
    }
    return originalName;
  }

  private boolean isDefinedInExternFiles(TypedVar symbol) {
    return externFileNamesSet.contains(symbol.getInputName());
  }

  private static boolean isTypedef(StaticTypedSlot<JSType> var) {
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

  private boolean maybePushCurrentExtensionType(StaticTypedSlot<JSType> member) {
    if (isApiExtension(member)) {
      Type extensionType = getJavaTypeRegistry().getExtensionType(getCurrentJavaType());
      pushCurrentJavaType(extensionType);
      return true;
    }
    return false;
  }

  protected boolean isApiExtension(StaticTypedSlot<JSType> member) {
    return getCurrentJavaType().isExtern()
        && !getContext()
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
