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
import static com.google.common.collect.Lists.newArrayList;
import static jsinterop.generator.helper.AbstractTypeRegistry.ReferenceContext.IN_HERITAGE_CLAUSE;
import static jsinterop.generator.helper.GeneratorUtils.createJavaPackage;
import static jsinterop.generator.helper.GeneratorUtils.extractName;
import static jsinterop.generator.helper.GeneratorUtils.extractNamespace;
import static jsinterop.generator.helper.GeneratorUtils.toCamelUpperCase;
import static jsinterop.generator.model.Annotation.builder;
import static jsinterop.generator.model.AnnotationType.JS_FUNCTION;
import static jsinterop.generator.model.AnnotationType.JS_OVERLAY;
import static jsinterop.generator.model.AnnotationType.JS_TYPE;
import static jsinterop.generator.model.EntityKind.INTERFACE;
import static jsinterop.generator.model.EntityKind.NAMESPACE;
import static jsinterop.generator.model.PredefinedTypeReference.JS;
import static jsinterop.generator.model.PredefinedTypeReference.OBJECT;

import com.google.common.collect.ImmutableList;
import java.util.Optional;
import jsinterop.generator.helper.AbstractTypeRegistry.ReferenceContext;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.ArrayTypeReference;
import jsinterop.generator.model.EntityKind;
import jsinterop.generator.model.JavaTypeReference;
import jsinterop.generator.model.LiteralExpression;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Method.Parameter;
import jsinterop.generator.model.MethodInvocation;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.ReturnStatement;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeQualifier;
import jsinterop.generator.model.TypeReference;

/** Helper methods around our java model. */
public class ModelHelper {

  private static final String GLOBAL_SCOPE_CLASS_NAME = "Global";
  private static final String GLOBAL_NAMESPACE = "<global>";

  public static TypeReference createArrayTypeReference(
      TypeReference valueType,
      ReferenceContext referenceContext,
      Optional<TypeReference> nativeArrayType) {
    if (referenceContext == IN_HERITAGE_CLAUSE) {
      if (!nativeArrayType.isPresent()) {
        throw new RuntimeException("Array type is not defined.");
      }
      // In java you cannot extends classic array. In this case create a parametrized reference
      // to JsArray class.
      return new ParametrizedTypeReference(nativeArrayType.get(), newArrayList(valueType));
    } else {
      // Convert array type to classic java array where it's valid.
      return new ArrayTypeReference(valueType);
    }
  }

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
      Type typeToExtend, String packagePrefix, String extensionTypePrefix) {
    if (isGlobalType(typeToExtend)) {
      return createGlobalJavaType(packagePrefix, extensionTypePrefix + typeToExtend.getName());
    }

    Type extendingType = new Type(typeToExtend.getKind());
    extendingType.setExtensionType(true);
    extendingType.setName(extensionTypePrefix + typeToExtend.getName());
    extendingType.setPackageName(
        createJavaPackage(typeToExtend.getNativeNamespace(), packagePrefix));

    extendingType.setNativeFqn(typeToExtend.getNativeFqn());
    extendingType.setNativeNamespace(typeToExtend.getNativeNamespace());

    extendingType.addInheritedType(new JavaTypeReference(typeToExtend));

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

  public static Type createGlobalJavaType(String packagePrefix) {
    return createGlobalJavaType(packagePrefix, GLOBAL_SCOPE_CLASS_NAME);
  }

  private static Type createGlobalJavaType(String packagePrefix, String globalScopeClassName) {
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

  private ModelHelper() {}
}
