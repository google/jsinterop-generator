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
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;
import static jsinterop.generator.helper.GeneratorUtils.createJavaPackage;
import static jsinterop.generator.helper.GeneratorUtils.extractName;
import static jsinterop.generator.helper.GeneratorUtils.extractNamespace;
import static jsinterop.generator.helper.GeneratorUtils.toCamelUpperCase;
import static jsinterop.generator.model.AnnotationType.DEPRECATED;
import static jsinterop.generator.model.AnnotationType.JS_ENUM;
import static jsinterop.generator.model.AnnotationType.JS_FUNCTION;
import static jsinterop.generator.model.AnnotationType.JS_OVERLAY;
import static jsinterop.generator.model.AnnotationType.JS_TYPE;
import static jsinterop.generator.model.EntityKind.ENUM;
import static jsinterop.generator.model.EntityKind.INTERFACE;
import static jsinterop.generator.model.EntityKind.NAMESPACE;
import static jsinterop.generator.model.PredefinedTypeReference.JS;
import static jsinterop.generator.model.PredefinedTypeReference.OBJECT;
import static jsinterop.generator.model.PredefinedTypeReference.VOID;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.AnnotationType;
import jsinterop.generator.model.ArrayTypeReference;
import jsinterop.generator.model.Entity;
import jsinterop.generator.model.EntityKind;
import jsinterop.generator.model.Expression;
import jsinterop.generator.model.ExpressionStatement;
import jsinterop.generator.model.JavaTypeReference;
import jsinterop.generator.model.LiteralExpression;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.MethodInvocation;
import jsinterop.generator.model.Parameter;
import jsinterop.generator.model.PredefinedTypeReference;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.ReturnStatement;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeQualifier;
import jsinterop.generator.model.TypeReference;

/** Helper methods around our java model. */
public class ModelHelper {
  private static final String GLOBAL_TYPE = "<global>";

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
    type.setNativeFqn(nativeFqn);

    if (isJsFunction) {
      type.addAnnotation(Annotation.builder().type(JS_FUNCTION).build());
    } else {
      AnnotationType annotationType = entityKind == ENUM ? JS_ENUM : JS_TYPE;
      Annotation.Builder annotationBuilder =
          Annotation.builder().type(annotationType).isNativeAttribute(true);

      if (!javaName.equals(nativeFqn)) {
        annotationBuilder.nameAttribute(nativeFqn);
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
        createJavaPackage(extractNamespace(typeToExtend.getNativeFqn()), packagePrefix));

    extendingType.setNativeFqn(typeToExtend.getNativeFqn());

    extendingType.addExtendedType(new JavaTypeReference(typeToExtend));

    String annotationTypeName = typeToExtend.getAnnotation(JS_TYPE).getNameAttribute();
    if (annotationTypeName == null) {
      annotationTypeName = typeToExtend.getNativeFqn();
    }

    extendingType.addAnnotation(
        Annotation.builder()
            .type(JS_TYPE)
            .isNativeAttribute(true)
            .nameAttribute(annotationTypeName)
            .build());

    // we add a of() casting method for easing casting to this type
    Method castMethod = new Method();
    castMethod.addAnnotation(Annotation.builder().type(JS_OVERLAY).build());
    castMethod.setStatic(true);
    castMethod.setReturnType(new JavaTypeReference(extendingType));
    castMethod.setName("of");
    castMethod.addParameter(
        Parameter.builder().setName("o").setType(new JavaTypeReference(typeToExtend)).build());
    castMethod.setBody(
        new ReturnStatement(
            MethodInvocation.builder()
                .setInvocationTarget(new TypeQualifier(JS))
                .setMethodName("cast")
                .setArgumentTypes(OBJECT)
                .setArguments(new LiteralExpression("o"))
                .build()));

    extendingType.addMethod(castMethod);

    return extendingType;
  }

  public static Type createGlobalJavaType(String packagePrefix, String globalScopeClassName) {
    Type type = new Type(NAMESPACE);
    type.setName(globalScopeClassName);
    type.setPackageName(packagePrefix);
    type.setNativeFqn(GLOBAL_TYPE);
    type.addAnnotation(
        Annotation.builder()
            .type(JS_TYPE)
            .isNativeAttribute(true)
            .nameAttribute("goog.global")
            .build());
    return type;
  }

  public static boolean isGlobalType(Type type) {
    return GLOBAL_TYPE.equals(type.getNativeFqn());
  }

  public static void addAnnotationNameAttributeIfNotEmpty(
      Entity entity, String originalName, AnnotationType annotationType, boolean createAnnotation) {
    Annotation annotation = entity.getAnnotation(annotationType);

    if (annotation != null && isNullOrEmpty(annotation.getNameAttribute())) {
      entity.removeAnnotation(annotationType);
      entity.addAnnotation(annotation.withNameAttribute(originalName));
    } else if (annotation == null && createAnnotation) {
      entity.addAnnotation(
          Annotation.builder().type(annotationType).nameAttribute(originalName).build());
    }
  }

  public static List<Type> getParentInterfaces(Type type) {
    return getParentInterfaces(type, false);
  }

  public static List<Type> getParentInterfaces(Type type, boolean transitive) {
    List<TypeReference> parentInterfaceReferences =
        type.isInterface() ? type.getExtendedTypes() : type.getImplementedTypes();

    return parentInterfaceReferences.stream()
        .map(TypeReference::getTypeDeclaration)
        .filter(Objects::nonNull)
        .flatMap(
            t -> {
              List<Type> types = Lists.newArrayList(t);
              if (transitive) {
                types.addAll(getParentInterfaces(t, true));
              }
              return types.stream();
            })
        .collect(toList());
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
        MethodInvocation.builder()
            .setMethodName(original.getName())
            .setArgumentTypes(originalParameterTypes)
            .setArguments(arguments)
            .build();

    overload.setBody(
        overload.getReturnType() == VOID
            ? new ExpressionStatement(delegation)
            : new ReturnStatement(delegation));
  }

  public static Expression callUncheckedCast(
      Parameter originalParameter, Parameter overloadParameter) {
    // will generate: Js.<$originalParameter.type>uncheckedCast($overloadParameter.name)
    // We need to add the local type argument to ensure to call the original method.
    return MethodInvocation.builder()
        .setInvocationTarget(new TypeQualifier(PredefinedTypeReference.JS))
        .setMethodName("uncheckedCast")
        .setArgumentTypes(OBJECT)
        .setArguments(new LiteralExpression(overloadParameter.getName()))
        .setLocalTypeArguments(
            originalParameter.isVarargs()
                ? new ArrayTypeReference(originalParameter.getType())
                : originalParameter.getType())
        .build();
  }

  private ModelHelper() {}
}
