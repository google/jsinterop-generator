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
import static jsinterop.generator.helper.GeneratorUtils.extractNamespace;
import static jsinterop.generator.helper.ModelHelper.createGlobalJavaType;
import static jsinterop.generator.model.EntityKind.CLASS;
import static jsinterop.generator.model.EntityKind.ENUM;
import static jsinterop.generator.model.EntityKind.INTERFACE;
import static jsinterop.generator.model.EntityKind.NAMESPACE;

import com.google.javascript.jscomp.TypedScope;
import com.google.javascript.rhino.jstype.EnumType;
import com.google.javascript.rhino.jstype.FunctionType;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.Property;
import com.google.javascript.rhino.jstype.RecordType;
import com.google.javascript.rhino.jstype.StaticTypedSlot;
import jsinterop.generator.closure.helper.GenerationContext;
import jsinterop.generator.helper.ModelHelper;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.AnnotationType;
import jsinterop.generator.model.EntityKind;
import jsinterop.generator.model.Type;

/** Collect type information. */
public class TypeCollector extends AbstractClosureVisitor {

  private final String packagePrefix;
  private final String extensionTypePrefix;
  private final String globalScopeClassName;

  public TypeCollector(
      GenerationContext ctx,
      String packagePrefix,
      String extensionTypePrefix,
      String globalScopeClassName) {
    super(ctx);

    this.packagePrefix = packagePrefix;
    this.extensionTypePrefix = extensionTypePrefix;
    this.globalScopeClassName = globalScopeClassName;
  }

  @Override
  protected void pushCurrentJavaType(JSType jsType) {
    // Because this visitor initializes the type registry and the java type linked to the visiting
    // js type could not exist yet, we override this method for doing nothing
  }

  @Override
  protected void popCurrentJavaType() {
    // Because by default we don't push java type in the stack, we don't have to pop one.
    // This visitor will call super.popCurrentJavaType if it specifically needs to pop one.
  }

  @Override
  protected void acceptMember(StaticTypedSlot member, boolean isStatic) {
    // Check if the member is an extension of the API of a type provided by an external third
    // party library. In this case, we create a new type extending the provided type. We will
    // collect all new members and synthetic types on this new type to make them available.
    if (isApiExtension(member)
        && !getJavaTypeRegistry().containsExtensionType(getCurrentJavaType())) {
      createApiExtensionType(getCurrentJavaType());
    }
  }

  @Override
  protected boolean visitTopScope(TypedScope scope) {
    Type globalJavaType = createGlobalJavaType(packagePrefix, globalScopeClassName);

    if (!getContext().getExternDependencyFiles().isEmpty()) {
      // We don't add the global type to the dependency mapping file so we cannot use that
      // information to decide if there is already a global scope defined in the dependencies.
      // It's simply safe to assume that if there are dependencies, a global scope is already
      // defined. The goal of the current type is to collect global scope members defined by
      // dependencies and will never be emitted.
      // If the current sources define function/variable on the global scope, a specific
      // extension type will be created for the global scope.
      globalJavaType.setExtern(true);
      // Change the name in order to not conflict with the real type that can be created later.
      globalJavaType.setName("__Global_Dependencies");
    }

    getJavaTypeRegistry().registerJavaGlobalType(scope.getTypeOfThis(), globalJavaType);
    getContext().getJavaProgram().addType(globalJavaType);
    // push the current java type to be able to create an extension type later.
    super.pushCurrentJavaType(globalJavaType);

    return true;
  }

  @Override
  protected boolean visitClassOrInterface(FunctionType type) {
    Type javaType =
        createJavaType(type.getDisplayName(), type.isInterface() ? INTERFACE : CLASS, false);
    javaType.setStructural(type.isStructuralInterface());

    if (type.getJSDocInfo() != null && type.getJSDocInfo().isDeprecated()) {
      javaType.addAnnotation(Annotation.builder().type(AnnotationType.DEPRECATED).build());
    }
    getJavaTypeRegistry().registerJavaType(type.getInstanceType(), javaType);

    // keep the current java type to be able to create an extension type later.
    super.pushCurrentJavaType(javaType);

    return true;
  }

  @Override
  protected void endVisitClassOrInterface(FunctionType type) {
    super.popCurrentJavaType();
  }

  @Override
  protected boolean visitEnumType(EnumType type) {
    Type enumType = createJavaType(type.getDisplayName(), ENUM, false);

    // In closure, the type used for the enum and the type for the enum members are different.
    // In our model, it's the same java type. We need to register the java type two times.
    getJavaTypeRegistry().registerJavaType(type, enumType);
    getJavaTypeRegistry().registerJavaType(type.getElementsType(), enumType);

    return false;
  }

  @Override
  protected boolean visitModule(StaticTypedSlot module) {
    String jsFqn =
        module instanceof Property
            ? ((Property) module).getNode().getOriginalQualifiedName()
            : module.getName();

    Type javaType = createJavaType(jsFqn, NAMESPACE, false);
    getJavaTypeRegistry().registerJavaType(module.getType(), javaType);
    super.pushCurrentJavaType(javaType);
    return true;
  }

  @Override
  protected void endVisitModule(StaticTypedSlot module) {
    super.popCurrentJavaType();
  }

  @Override
  protected boolean visitRecordType(String jsFqn, RecordType type) {
    if (jsFqn == null) {
      // If the name of the RecordType is null that means that is a anonymous record type:
      // /** @type {{foo:string}} */ var Foo;
      // This kind of type is processed in AnonymousTypeCollector
      return false;
    }

    Type javaType = createJavaType(jsFqn, INTERFACE, false);
    javaType.setStructural(true);
    getJavaTypeRegistry().registerJavaType(type, javaType);
    return false;
  }

  @Override
  protected boolean visitFunctionType(String jsFqn, FunctionType type) {
    if (jsFqn == null) {
      // If the name of the FunctionType is null that means that is a anonymous function type:
      // /** @type {function(boolean)} */ var Foo;
      // This kind of type is processed in AnonymousTypeCollector
      return false;
    }

    Type javaType = createJavaType(jsFqn, INTERFACE, true);
    javaType.setStructural(true);
    getJavaTypeRegistry().registerJavaType(type, javaType);
    return false;
  }

  private Type createJavaType(String jsFqn, EntityKind entityKind, boolean isJsFunction) {
    String name = extractName(jsFqn);
    String namespace = extractNamespace(jsFqn, name);

    Type javaType =
        ModelHelper.createJavaType(
            name,
            namespace,
            jsFqn,
            entityKind,
            isJsFunction,
            packagePrefix,
            getContext().getJavaProgram());

    getContext().getJavaProgram().addType(javaType);

    return javaType;
  }

  private void createApiExtensionType(Type typeToExtend) {
    Type extensionType =
        ModelHelper.createApiExtensionType(
            typeToExtend, packagePrefix, extensionTypePrefix, globalScopeClassName);

    getJavaTypeRegistry().registerExtensionType(typeToExtend, extensionType);
    getContext().getJavaProgram().addType(extensionType);
  }
}
