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
 */

package jsinterop.generator.closure.visitor;

import static jsinterop.generator.helper.AbstractTypeRegistry.ReferenceContext.IN_TYPE_ARGUMENTS;
import static jsinterop.generator.model.AccessModifier.PRIVATE;
import static jsinterop.generator.model.AnnotationType.JS_OVERLAY;
import static jsinterop.generator.model.AnnotationType.JS_PROPERTY;
import static jsinterop.generator.model.PredefinedTypeReference.OBJECT;

import com.google.common.collect.ImmutableList;
import com.google.javascript.rhino.jstype.EnumType;
import com.google.javascript.rhino.jstype.Property;
import jsinterop.generator.closure.helper.GenerationContext;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.Field;
import jsinterop.generator.model.LiteralExpression;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.MethodInvocation;
import jsinterop.generator.model.PredefinedTypeReference;
import jsinterop.generator.model.ReturnStatement;
import jsinterop.generator.model.TypeQualifier;
import jsinterop.generator.model.TypeReference;

/**
 * Convert closure Enum members to static final java fields and add an instance method getValue()
 * that returns the value of the enum member at runtime.
 *
 * <p>ex:</p>
 *
 * <pre>
 *   /**
 *    * &#64;enum {number}
 *    *&#x2F;
 *   TriState = {
 *    TRUE: 1,
 *    FALSE: -1,
 *    MAYBE: 0
 *   };
 * </pre>
 *
 * <p>is converted to:</p>
 *
 * <pre>
 *  &#64;JsType(isNative = true)
 *  public final class TriState {
 *    &#64;JsOverlay
 *    public static final TriState TRUE = TriState__Constants.TRUE;
 *    &#64;JsOverlay
 *    public static final TriState FALSE = TriState__Constants.FALSE;
 *    &#64;JsOverlay
 *    public static final TriState MAYBE = TriState__Constants.MAYBE;
 *
 *    &#64;JsOverlay
 *    public double getValue() {
 *      return Js.uncheckCast(this);
 *    }
 *
 *    private TriState() {} // avoid instantiation
 *  }
 * </pre>
 */
public class EnumMemberCollector extends AbstractClosureVisitor {

  public EnumMemberCollector(GenerationContext ctx) {
    super(ctx);
  }

  @Override
  protected boolean visitEnumType(EnumType type) {
    // Create a method getValue() that returns the real value of the enum member at runtime.
    Method m = new Method();
    m.setName("getValue");
    m.setFinal(true);
    m.addAnnotation(Annotation.builder().type(JS_OVERLAY).build());
    TypeReference returnType =
        getJavaTypeRegistry().createTypeReference(type.getElementsType().getPrimitiveType());
    TypeReference uncheckedCastTypeParameter =
        getJavaTypeRegistry()
            .createTypeReference(type.getElementsType().getPrimitiveType(), IN_TYPE_ARGUMENTS);
    m.setReturnType(returnType);
    m.setBody(
        new ReturnStatement(
            new MethodInvocation(
                new TypeQualifier(PredefinedTypeReference.JS),
                "uncheckedCast",
                ImmutableList.of(OBJECT),
                ImmutableList.of(LiteralExpression.THIS),
                ImmutableList.of(uncheckedCastTypeParameter))));

    getCurrentJavaType().addMethod(m);

    // add a private constructor to disallow instantiation
    Method privateConstructor = Method.newConstructor();
    privateConstructor.setAccessModifier(PRIVATE);
    getCurrentJavaType().addMethod(privateConstructor);

    return true;
  }

  @Override
  protected boolean visitEnumMember(Property enumMember) {
    Field enumField = new Field();
    enumField.setName(enumMember.getName());
    enumField.setType(getJavaTypeRegistry().createTypeReference(enumMember.getType()));
    enumField.setStatic(true);
    enumField.setNativeReadOnly(true);
    enumField.addAnnotation(
        Annotation.builder()
            .type(JS_PROPERTY)
            .nameAttribute(enumMember.getName())
            .namespaceAttribute(enumMember.getType().getDisplayName())
            .build());
    getCurrentJavaType().addField(enumField);
    return true;
  }
}
