/*
 * Copyright 2017 Google Inc.
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

import static java.util.stream.Collectors.joining;
import static jsinterop.generator.helper.GeneratorUtils.toCamelUpperCase;
import static jsinterop.generator.model.AnnotationType.JS_FUNCTION;
import static jsinterop.generator.model.AnnotationType.JS_OVERLAY;
import static jsinterop.generator.model.AnnotationType.JS_TYPE;
import static jsinterop.generator.model.EntityKind.CONSTRUCTOR;
import static jsinterop.generator.model.EntityKind.INTERFACE;
import static jsinterop.generator.model.PredefinedTypes.BOOLEAN;
import static jsinterop.generator.model.PredefinedTypes.BOOLEAN_OBJECT;
import static jsinterop.generator.model.PredefinedTypes.DOUBLE;
import static jsinterop.generator.model.PredefinedTypes.DOUBLE_OBJECT;
import static jsinterop.generator.model.PredefinedTypes.INT;
import static jsinterop.generator.model.PredefinedTypes.OBJECT;
import static jsinterop.generator.model.PredefinedTypes.STRING;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import jsinterop.generator.model.AbstractRewriter;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.ArrayTypeReference;
import jsinterop.generator.model.CastExpression;
import jsinterop.generator.model.Field;
import jsinterop.generator.model.InstanceOfExpression;
import jsinterop.generator.model.JavaTypeReference;
import jsinterop.generator.model.LiteralExpression;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.MethodInvocation;
import jsinterop.generator.model.ModelVisitor;
import jsinterop.generator.model.Parameter;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.PredefinedTypes;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.ReturnStatement;
import jsinterop.generator.model.Statement;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeQualifier;
import jsinterop.generator.model.TypeReference;
import jsinterop.generator.model.TypeVariableReference;
import jsinterop.generator.model.UnionTypeReference;

/**
 * This visitor will create an helper type for all union types references and replace these
 * references with references to these helper types.
 */
public class UnionTypeHelperTypeCreator implements ModelVisitor {
  private final IdentityHashMap<UnionTypeReference, Type> typeHelperByUnionTypeReference =
      new IdentityHashMap<>();

  public Set<Type> getUnionTypeHelperTypes() {
    return ImmutableSet.copyOf(typeHelperByUnionTypeReference.values());
  }

  @Override
  public void applyTo(Program program) {
    program.accept(
        new AbstractRewriter() {
          private final Deque<String> currentNameStack = new ArrayDeque<>();

          @Override
          public boolean shouldProcessField(Field field) {
            currentNameStack.push(toCamelUpperCase(field.getName()));
            return true;
          }

          @Override
          public Field rewriteField(Field field) {
            currentNameStack.pop();
            return field;
          }

          @Override
          public boolean shouldProcessMethod(Method method) {
            // JsFunction type only have one method named onInvoke, don't use the method name
            // because it doesn't give us any much more information.
            if (!isCurrentTypeJsFunction()) {
              currentNameStack.push(
                  method.getKind() == CONSTRUCTOR
                      ? "Constructor"
                      : toCamelUpperCase(method.getName()));
            }
            return true;
          }

          @Override
          public Method rewriteMethod(Method method) {
            if (!isCurrentTypeJsFunction()) {
              currentNameStack.pop();
            }

            return method;
          }

          @Override
          public boolean shouldProcessParameter(Parameter parameter) {
            currentNameStack.push(toCamelUpperCase(parameter.getName()));
            return true;
          }

          @Override
          public Parameter rewriteParameter(Parameter parameter) {
            currentNameStack.pop();
            return parameter;
          }

          @Override
          public boolean shouldProcessParametrizedTypeReference(
              ParametrizedTypeReference typeReference) {
            if (isJsArrayReference(typeReference)) {
              currentNameStack.push("Array");
              return true;
            }

            // Manually visit the type parameter in order to know the index of the type parameter in
            // the list and create the name accordingly for a possible union type.
            List<TypeReference> newTypeArguments = new ArrayList<>();
            for (int i = 0; i < typeReference.getActualTypeArguments().size(); i++) {
              String unionTypeName = typeReference.getTypeName() + "TypeParameter";
              if (i > 0) {
                unionTypeName += i;
              }
              currentNameStack.push(unionTypeName);
              newTypeArguments.add(typeReference.getActualTypeArguments().get(i).accept(this));
              currentNameStack.pop();
            }
            typeReference.setActualTypeArguments(newTypeArguments);

            return false;
          }

          private boolean isJsArrayReference(ParametrizedTypeReference reference) {
            return reference.getMainType().getTypeDeclaration() != null
                && reference.getMainType().getTypeDeclaration().getNativeFqn().equals("Array");
          }

          @Override
          public boolean shouldProcessArrayTypeReference(ArrayTypeReference typeReference) {
            currentNameStack.push("Array");
            return true;
          }

          @Override
          public TypeReference rewriteUnionTypeReference(UnionTypeReference typeReference) {
            return new JavaTypeReference(
                createUnionTypeHelperType(typeReference), typeReference.isNullable());
          }

          @Override
          public TypeReference rewriteArrayTypeReference(ArrayTypeReference typeReference) {
            currentNameStack.pop();
            return typeReference;
          }

          @Override
          public TypeReference rewriteParametrizedTypeReference(ParametrizedTypeReference node) {
            if (isJsArrayReference(node)) {
              currentNameStack.pop();
            }

            return node;
          }

          private boolean isCurrentTypeJsFunction() {
            return getCurrentType().hasAnnotation(JS_FUNCTION);
          }

          private Type createUnionTypeHelperType(UnionTypeReference unionTypeReference) {
            if (typeHelperByUnionTypeReference.containsKey(unionTypeReference)) {
              // because we create methods overloading when a union type is present in parameters, a
              // same UnionTypeReference can be reused in those methods. In this case reuse the same
              // helper type.
              return typeHelperByUnionTypeReference.get(unionTypeReference);
            }

            Type helperType = new Type(INTERFACE);
            helperType.setSynthetic(true);
            helperType.setName(buildHelperTypeName());
            helperType.setNativeFqn(unionTypeReference.getJsDocAnnotationString());
            helperType.addAnnotation(
                Annotation.builder()
                    .type(JS_TYPE)
                    .isNativeAttribute(true)
                    .nameAttribute("?")
                    .build());

            // add a HelperType.of(Object)
            boolean isNullable = unionTypeReference.isNullable();
            helperType.addMethod(createOfMethod(helperType, isNullable));

            // create all asXXX methods
            unionTypeReference
                .getTypes()
                .forEach(t -> helperType.addMethod(createAsMethod(t, isNullable)));

            // create all isXXX methods
            unionTypeReference.getTypes().stream()
                .filter(TypeReference::isInstanceofAllowed)
                .forEach(t -> helperType.addMethod(createInstanceOfMethod(t)));

            typeHelperByUnionTypeReference.put(unionTypeReference, helperType);

            getCurrentType().addInnerType(helperType);

            return helperType;
          }

          private String buildHelperTypeName() {
            currentNameStack.push("UnionType");
            String name = Streams.stream(currentNameStack.descendingIterator()).collect(joining());
            currentNameStack.pop();

            return name;
          }
        });
  }

  /**
   * Create a default method that allow to check if the helper object is an instance of a specific
   * type of the union type:
   *
   * <pre>
   *   interface FooOrBar {
   *     default boolean isFoo() {
   *       return this instanceof Foo;
   *     }
   *   }
   * </pre>
   */
  private static Method createInstanceOfMethod(TypeReference typeReference) {
    Method instanceOfMethod = createMethod(false);
    instanceOfMethod.setName("is" + typeToName(typeReference));
    instanceOfMethod.setReturnType(BOOLEAN.getReference(false));
    TypeReference rightOperand = toInstanceOfType(typeReference);

    instanceOfMethod.setBody(
        new ReturnStatement(
            new InstanceOfExpression(
                new CastExpression(OBJECT.getReference(false), LiteralExpression.THIS),
                rightOperand)));
    return instanceOfMethod;
  }

  private static TypeReference toInstanceOfType(TypeReference typeReference) {
    if (typeReference instanceof ArrayTypeReference) {
      // TODO(b/34396450): This won't work with a array created on javascript side.
      return new ArrayTypeReference(OBJECT.getReference(false));
    }

    // remove Type parameters
    if (typeReference instanceof ParametrizedTypeReference) {
      return ((ParametrizedTypeReference) typeReference).getMainType().toNonNullableTypeReference();
    }

    // autoboxing primitives
    if (typeReference.isReferenceTo(BOOLEAN)) {
      return BOOLEAN_OBJECT.getReference(false);
    }

    if (typeReference.isReferenceTo(DOUBLE) || typeReference.isReferenceTo(INT)) {
      return DOUBLE_OBJECT.getReference(false);
    }

    return typeReference.toNonNullableTypeReference();
  }

  /**
   * Create a default method that allow to cast the helper object to a specific type of the union
   * type:
   *
   * <pre>
   *   interface FooOrBar {
   *     default Foo asFoo() {
   *       return Js.cast(this);
   *     }
   *   }
   * </pre>
   */
  private static Method createAsMethod(TypeReference typeReference, boolean isNullable) {
    Method castMethod = createMethod(false);
    castMethod.setName("as" + typeToName(typeReference));

    if (isNullable
        && !PredefinedTypes.isPrimitiveTypeReference(typeReference)
        && !(typeReference instanceof TypeVariableReference)) {
      typeReference = typeReference.toNullableTypeReference();
    }

    castMethod.setReturnType(typeReference);
    castMethod.setBody(createJsCastInvocation("this", typeReference));

    return castMethod;
  }

  /**
   * Create a default static method that allow to cast any object to the helper type:
   *
   * <pre>
   *   interface FooOrBar {
   *     static default FooOrBar of(Object o) {
   *       return Js.cast(o);
   *     }
   *   }
   * </pre>
   *
   * We need to accept Object as parameter because if we have an helper type BarOrBazOrFoo, one
   * could potentially want to cast type BarOrFoo or BarOrBaz etc. to BarOrBazOrFoo and we may not
   * know these types.
   */
  private static Method createOfMethod(Type helperType, boolean isNullable) {
    TypeReference returnTypeReference = new JavaTypeReference(helperType, isNullable);
    TypeReference parameterTypeReference = OBJECT.getReference(isNullable);
    Method builderMethod = createMethod(true);
    builderMethod.setName("of");
    builderMethod.setReturnType(returnTypeReference);
    builderMethod.addParameter(
        Parameter.builder().setName("o").setType(parameterTypeReference).build());
    builderMethod.addTypeParameter(parameterTypeReference);
    builderMethod.setBody(createJsCastInvocation("o", returnTypeReference));

    return builderMethod;
  }

  private static String typeToName(TypeReference type) {
    if (type instanceof ArrayTypeReference) {
      return typeToName(((ArrayTypeReference) type).getArrayType()) + "Array";
    }

    return toCamelUpperCase(type.getTypeName());
  }

  private static Method createMethod(boolean isStatic) {
    Method method = new Method();
    method.setDefault(!isStatic);
    method.setStatic(isStatic);
    method.addAnnotation(Annotation.builder().type(JS_OVERLAY).build());
    return method;
  }

  private static Statement createJsCastInvocation(String argumentName, TypeReference returnType) {
    String castMethodName;
    if (returnType.isReferenceTo(BOOLEAN)) {
      castMethodName = "asBoolean";
    } else if (returnType.isReferenceTo(DOUBLE)) {
      castMethodName = "asDouble";
    } else if (returnType.isReferenceTo(INT)) {
      castMethodName = "asInt";
    } else if (returnType.isReferenceTo(STRING)) {
      castMethodName = "asString";
    } else {
      castMethodName = "cast";
    }
    return new ReturnStatement(
        MethodInvocation.builder()
            .setInvocationTarget(new TypeQualifier(PredefinedTypes.JS.getReference(false)))
            .setMethodName(castMethodName)
            .setArgumentTypes(OBJECT.getReference(false))
            .setArguments(new LiteralExpression(argumentName))
            .build());
  }
}
