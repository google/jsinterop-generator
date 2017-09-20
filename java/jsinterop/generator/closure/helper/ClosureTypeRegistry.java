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
package jsinterop.generator.closure.helper;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static jsinterop.generator.helper.AbstractTypeRegistry.ReferenceContext.IN_HERITAGE_CLAUSE;
import static jsinterop.generator.helper.AbstractTypeRegistry.ReferenceContext.IN_TYPE_ARGUMENTS;
import static jsinterop.generator.helper.AbstractTypeRegistry.ReferenceContext.REGULAR;
import static jsinterop.generator.model.PredefinedTypeReference.BOOLEAN;
import static jsinterop.generator.model.PredefinedTypeReference.BOOLEAN_OBJECT;
import static jsinterop.generator.model.PredefinedTypeReference.DOUBLE;
import static jsinterop.generator.model.PredefinedTypeReference.DOUBLE_OBJECT;
import static jsinterop.generator.model.PredefinedTypeReference.OBJECT;
import static jsinterop.generator.model.PredefinedTypeReference.STRING;
import static jsinterop.generator.model.PredefinedTypeReference.VOID;
import static jsinterop.generator.model.PredefinedTypeReference.VOID_OBJECT;

import com.google.common.collect.ImmutableList;
import com.google.javascript.rhino.jstype.EnumElementType;
import com.google.javascript.rhino.jstype.FunctionType;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.NamedType;
import com.google.javascript.rhino.jstype.NoType;
import com.google.javascript.rhino.jstype.ObjectType;
import com.google.javascript.rhino.jstype.ProxyObjectType;
import com.google.javascript.rhino.jstype.TemplateType;
import com.google.javascript.rhino.jstype.TemplatizedType;
import com.google.javascript.rhino.jstype.UnionType;
import com.google.javascript.rhino.jstype.Visitor;
import java.util.IdentityHashMap;
import java.util.List;
import jsinterop.generator.helper.AbstractTypeRegistry;
import jsinterop.generator.model.ArrayTypeReference;
import jsinterop.generator.model.JavaTypeReference;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.PredefinedTypeReference;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;
import jsinterop.generator.model.TypeVariableReference;
import jsinterop.generator.model.UnionTypeReference;

/** Implementation of {@link AbstractTypeRegistry} specific to closure. */
public class ClosureTypeRegistry extends AbstractTypeRegistry<JSType> {

  public ClosureTypeRegistry() {
    // JsCompiler considers two different RecordType with the same structure as equivalent in terms
    // of equals() and hashcode() even if they are associated to two different symbols.
    // In our case we want to be able to differentiate those RecordType because they are associated
    // with two different Java interfaces. As JsCompiler creates one JSType instance by type
    // definition and reuse the instance each time they reach a reference to the type, it's safe to
    // use a IdentityHashMap to create our mapping between JsType and Java type.
    super(new IdentityHashMap<>());
  }

  public void registerJavaType(Type javaType, JSType jsType) {
    registerJavaTypeByKey(javaType, jsType);
  }

  public Type getJavaType(JSType type) {
    return getJavaTypeByKey(type);
  }

  public boolean containsJavaType(JSType type) {
    return containsJavaTypeByKey(type);
  }

  public TypeReference createTypeReference(JSType jsType) {
    return createTypeReference(jsType, REGULAR);
  }

  public TypeReference createTypeReference(JSType jsType, ReferenceContext referenceContext) {
    return new TypeReferenceCreator(referenceContext).visit(jsType);
  }

  private class TypeReferenceCreator implements Visitor<TypeReference> {
    private final ReferenceContext referenceContext;

    TypeReferenceCreator(ReferenceContext referenceContext) {
      this.referenceContext = referenceContext;
    }

    private TypeReference visit(JSType type) {
      if (type.isVoidType() || type.isNullType()) {
        return type.visit(this);
      }

      // Nullable type and optional type are represented in closure by an union type with
      // respectively Null and Undefined. We don't use this information (yet), so we remove Null
      // or Undefined before to visit the type.
      return type.restrictByNotNullOrUndefined().visit(this);
    }

    @Override
    public TypeReference caseNoType(NoType type) {
      return OBJECT;
    }

    @Override
    public TypeReference caseEnumElementType(EnumElementType type) {
      throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public TypeReference caseAllType() {
      return OBJECT;
    }

    @Override
    public TypeReference caseBooleanType() {
      return referenceContext == REGULAR ? BOOLEAN : BOOLEAN_OBJECT;
    }

    @Override
    public TypeReference caseNoObjectType() {
      return OBJECT;
    }

    @Override
    public TypeReference caseFunctionType(FunctionType type) {
      if (type.isConstructor() && type.getDisplayName() == null) {
        return new ParametrizedTypeReference(
            PredefinedTypeReference.JS_CONSTRUCTOR_FN,
            ImmutableList.of(visit(type.getTypeOfThis())));
      }
      return new JavaTypeReference(checkNotNull(getJavaType(type)));
    }

    @Override
    public TypeReference caseObjectType(ObjectType type) {
      if (type.isNativeObjectType() && "Object".equals(type.getReferenceName())) {
        // Reference to js built-in Object type is converted to java.lang.Object
        return OBJECT;
      }
      return new JavaTypeReference(checkNotNull(getJavaType(type)));
    }

    @Override
    public TypeReference caseUnknownType() {
      return OBJECT;
    }

    @Override
    public TypeReference caseNullType() {
      return null;
    }

    @Override
    public TypeReference caseNamedType(NamedType type) {
      // Reference to undefined types are wrapped in a NamedType with a reference to UnknownType.
      checkState(!type.isUnknownType(), "Type %s is unknown", type.getReferenceName());

      return visit(type.getReferencedType());
    }

    @Override
    public TypeReference caseProxyObjectType(ProxyObjectType type) {
      throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public TypeReference caseNumberType() {
      return referenceContext == REGULAR ? DOUBLE : DOUBLE_OBJECT;
    }

    @Override
    public TypeReference caseStringType() {
      return STRING;
    }

    @Override
    public TypeReference caseVoidType() {
      return referenceContext == REGULAR ? VOID : VOID_OBJECT;
    }

    @Override
    public TypeReference caseUnionType(UnionType type) {
      return new UnionTypeReference(
          type.getAlternates().stream().map(this::visit).collect(toList()));
    }

    @Override
    public TypeReference caseTemplatizedType(TemplatizedType type) {
      if (type.getReferencedType().isArrayType()) {
        TypeReference arrayType = PredefinedTypeReference.OBJECT;
        if (type.getTemplateTypes().size() == 1) {
          arrayType =
              new TypeReferenceCreator(
                      referenceContext == IN_HERITAGE_CLAUSE ? IN_TYPE_ARGUMENTS : REGULAR)
                  .visit(type.getTemplateTypes().get(0));
        }
        if (referenceContext == IN_HERITAGE_CLAUSE) {
          // In java you cannot extends classic array. In this case create a parametrized reference
          // to JsArray class.
          return new ParametrizedTypeReference(
              visit(type.getReferencedType()), newArrayList(arrayType));
        } else {
          // Convert array type to classic java array where it's valid.
          return new ArrayTypeReference(arrayType);
        }
      }

      TypeReference templatizedType = visit(type.getReferencedType());

      TypeReferenceCreator typeArgumentsReferenceCreator =
          new TypeReferenceCreator(IN_TYPE_ARGUMENTS);
      List<TypeReference> templates =
          type.getTemplateTypes()
              .stream()
              .map(typeArgumentsReferenceCreator::visit)
              .collect(toList());

      return new ParametrizedTypeReference(templatizedType, templates);
    }

    @Override
    public TypeReference caseTemplateType(TemplateType templateType) {
      return new TypeVariableReference(templateType.getReferenceName(), null);
    }
  }
}
