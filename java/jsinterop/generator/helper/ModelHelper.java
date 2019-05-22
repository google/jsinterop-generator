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
package jsinterop.generator.helper;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;
import static jsinterop.generator.helper.GeneratorUtils.createJavaPackage;
import static jsinterop.generator.helper.GeneratorUtils.extractName;
import static jsinterop.generator.helper.GeneratorUtils.extractNamespace;
import static jsinterop.generator.helper.GeneratorUtils.toCamelUpperCase;
import static jsinterop.generator.model.Annotation.builder;
import static jsinterop.generator.model.AnnotationType.DEPRECATED;
import static jsinterop.generator.model.AnnotationType.JS_FUNCTION;
import static jsinterop.generator.model.AnnotationType.JS_OVERLAY;
import static jsinterop.generator.model.AnnotationType.JS_TYPE;
import static jsinterop.generator.model.EntityKind.INTERFACE;
import static jsinterop.generator.model.EntityKind.NAMESPACE;
import static jsinterop.generator.model.PredefinedTypeReference.JS;
import static jsinterop.generator.model.PredefinedTypeReference.OBJECT;
import static jsinterop.generator.model.PredefinedTypeReference.VOID;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.EntityKind;
import jsinterop.generator.model.Expression;
import jsinterop.generator.model.ExpressionStatement;
import jsinterop.generator.model.JavaTypeReference;
import jsinterop.generator.model.LiteralExpression;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Method.Parameter;
import jsinterop.generator.model.MethodInvocation;
import jsinterop.generator.model.PredefinedTypeReference;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.ReturnStatement;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeQualifier;
import jsinterop.generator.model.TypeReference;

/** Helper methods around our java model. */
public class ModelHelper {
  private static final String GLOBAL_NAMESPACE = "<global>";

  public static Type createJavaType(
      String name,
      String namespace,
      String nativeFqn,
      EntityKind entityKind,
      boolean isJsFunction,
      String packagePrefix,
      Program program) {
    checkState(
        !isJsFunction || entityKind == INTERFACE, "JsFunction cannot be converted to a java class");

    Type type = new Type(entityKind);

    String javaName;
    String javaPackageName;

    if (program.isThirdPartyType(nativeFqn)) {
      // If the type is provided by a third-party lib, use the package name and type name provided
      // by the lib.
      // We have still to reconstruct the type because we will need information about its
      // constructors, fields, methods...
      String javaFqn = program.getThirdPartyTypeJavaFqn(nativeFqn);

      javaName = extractName(javaFqn);
      javaPackageName = extractNamespace(javaFqn, javaName);

      type.setExtern(true);
    } else {
      javaName = toCamelUpperCase(name);
      javaPackageName = createJavaPackage(namespace, packagePrefix);
    }

    type.setName(javaName);
    type.setPackageName(javaPackageName);
    type.setNativeNamespace(namespace);
    type.setNativeFqn(nativeFqn);

    if (isJsFunction) {
      type.addAnnotation(Annotation.builder().type(JS_FUNCTION).build());
    } else {
      Annotation.Builder annotationBuilder = builder().type(JS_TYPE).isNativeAttribute(true);

      if (!javaPackageName.equals(namespace)) {
        annotationBuilder.namespaceAttribute(namespace);
      }

      if (!javaName.equals(name)) {
        annotationBuilder.nameAttribute(name);
      }

      type.addAnnotation(annotationBuilder.build());
    }

    return type;
  }

  public static Type createApiExtensionType(
      Type typeToExtend,
      String packagePrefix,
      String extensionTypePrefix,
      String globalScopeClassName) {
    if (isGlobalType(typeToExtend)) {
      return createGlobalJavaType(packagePrefix, globalScopeClassName);
    }

    Type extendingType = new Type(typeToExtend.getKind());
    extendingType.setExtensionType(true);
    extendingType.setName(extensionTypePrefix + typeToExtend.getName());
    extendingType.setPackageName(
        createJavaPackage(typeToExtend.getNativeNamespace(), packagePrefix));

    extendingType.setNativeFqn(typeToExtend.getNativeFqn());
    extendingType.setNativeNamespace(typeToExtend.getNativeNamespace());

    extendingType.addExtendedType(new JavaTypeReference(typeToExtend));

    String nativeTypeName = typeToExtend.getAnnotation(JS_TYPE).getNameAttribute();
    if (nativeTypeName == null) {
      nativeTypeName = typeToExtend.getName();
    }
    extendingType.addAnnotation(
        Annotation.builder()
            .type(JS_TYPE)
            .isNativeAttribute(true)
            .nameAttribute(nativeTypeName)
            .namespaceAttribute(extendingType.getNativeNamespace())
            .build());

    // we add a of() casting method for easing casting to this type
    Method castMethod = new Method();
    castMethod.addAnnotation(Annotation.builder().type(JS_OVERLAY).build());
    castMethod.setStatic(true);
    castMethod.setReturnType(new JavaTypeReference(extendingType));
    castMethod.setName("of");
    castMethod.addParameter(new Parameter("o", new JavaTypeReference(typeToExtend), false, false));
    castMethod.setBody(
        new ReturnStatement(
            new MethodInvocation(
                new TypeQualifier(JS),
                "cast",
                ImmutableList.of(OBJECT),
                ImmutableList.of(new LiteralExpression("o")))));
    extendingType.addMethod(castMethod);

    return extendingType;
  }

  public static Type createGlobalJavaType(String packagePrefix, String globalScopeClassName) {
    Type type = new Type(NAMESPACE);
    type.setName(globalScopeClassName);
    type.setPackageName(packagePrefix);
    type.setNativeNamespace(GLOBAL_NAMESPACE);
    type.addAnnotation(
        builder()
            .type(JS_TYPE)
            .isNativeAttribute(true)
            .namespaceAttribute("")
            .nameAttribute("window")
            .build());
    return type;
  }

  public static boolean isGlobalType(Type type) {
    return GLOBAL_NAMESPACE.equals(type.getNativeNamespace());
  }

  /**
   * Callback object used to decide what argument to create for delegation when the type of a
   * parameter of the original method doesn't match the type of the corresponding parameter of an
   * overlay method.
   */
  public interface ArgumentRewriter {
    Expression rewriteArgument(Parameter originalParameter, Parameter overloadParameter);
  }

  /** Callback object used to rewrite parameter during method overlay creation. */
  public interface ParameterRewriter {
    Parameter rewriteParameter(int index, Parameter originalParameter);
  }

  public static Method createDelegatingOverlayMethod(
      Method originalMethod,
      ParameterRewriter parameterRewriter,
      ArgumentRewriter argumentRewriter) {

    Method jsOverlayMethod = Method.from(originalMethod);

    List<Parameter> overlayParameters = new ArrayList<>();
    int parameterIndex = 0;
    for (Parameter parameter : originalMethod.getParameters()) {
      overlayParameters.add(parameterRewriter.rewriteParameter(parameterIndex++, parameter));
    }

    jsOverlayMethod.setParameters(overlayParameters);

    if (jsOverlayMethod.equals(originalMethod)) {
      return null;
    }

    // If the method is a constructor, we don't need the JSOverlay annotation and native constructor
    // cannot define any body.
    if (originalMethod.getKind() != EntityKind.CONSTRUCTOR) {
      boolean defaultMethod = originalMethod.getEnclosingType().isInterface();

      jsOverlayMethod.getAnnotations().clear();
      jsOverlayMethod.addAnnotation(Annotation.builder().type(JS_OVERLAY).build());

      if (originalMethod.hasAnnotation(DEPRECATED)) {
        jsOverlayMethod.addAnnotation(Annotation.builder().type(DEPRECATED).build());
      }

      jsOverlayMethod.setDefault(defaultMethod);
      jsOverlayMethod.setFinal(!defaultMethod);
      createInvocationToOriginalMethod(originalMethod, jsOverlayMethod, argumentRewriter);
    }

    return jsOverlayMethod;
  }

  private static void createInvocationToOriginalMethod(
      Method original, Method overload, ArgumentRewriter onNonEqualParameters) {
    List<Expression> arguments = new ArrayList<>();

    for (int i = 0; i < original.getParameters().size(); i++) {
      Parameter originalParameter = original.getParameters().get(i);
      Parameter overloadParameter = overload.getParameters().get(i);

      if (originalParameter.getType().equals(overloadParameter.getType())) {
        // pass the parameter
        arguments.add(new LiteralExpression(overloadParameter.getName()));
      } else {
        arguments.add(onNonEqualParameters.rewriteArgument(originalParameter, overloadParameter));
      }
    }

    List<TypeReference> originalParameterTypes =
        original.getParameters().stream().map(Parameter::getType).collect(toList());

    Expression delegation =
        new MethodInvocation(null, original.getName(), originalParameterTypes, arguments);

    overload.setBody(
        overload.getReturnType() == VOID
            ? new ExpressionStatement(delegation)
            : new ReturnStatement(delegation));
  }

  public static Expression callUncheckedCast(
      Parameter originalParameter, Parameter overloadParameter) {
    // will generate: Js.<$originalParameter.type>uncheckedCast($overloadParameter.name)
    // We need to add the local type argument to ensure to call the original method.
    return new MethodInvocation(
        new TypeQualifier(PredefinedTypeReference.JS),
        "uncheckedCast",
        ImmutableList.of(OBJECT),
        ImmutableList.of(new LiteralExpression(overloadParameter.getName())),
        ImmutableList.of(originalParameter.getType()));
  }

  private ModelHelper() {}
}
