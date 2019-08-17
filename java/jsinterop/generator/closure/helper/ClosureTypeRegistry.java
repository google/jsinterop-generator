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
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import jsinterop.generator.helper.AbstractTypeRegistry;
import jsinterop.generator.model.ArrayTypeReference;
import jsinterop.generator.model.JavaTypeReference;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.PredefinedTypeReference;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;
import jsinterop.generator.model.TypeVariableReference;
import jsinterop.generator.model.UnionTypeReference;
import jsinterop.generator.model.WildcardTypeReference;

/** Implementation of {@link AbstractTypeRegistry} specific to closure. */
public class ClosureTypeRegistry extends AbstractTypeRegistry<JSType> {

  private final Map<TemplateType, JSType> jsTypeByThisTemplateType = new HashMap<>();

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
    return new TypeReferenceCreator(referenceContext).resolveTypeReference(jsType);
  }

  /**
   * Create a mapping between a template variable used as receiver type of a method (with @this
   * annotation) and the type defining the method.
   */
  public void registerThisTemplateType(TemplateType thisTemplateType, JSType jsType) {
    jsTypeByThisTemplateType.put(thisTemplateType, jsType);
  }

  private class TypeReferenceCreator extends AbstractNoOpVisitor<TypeReference> {
    private final ReferenceContext referenceContext;

    TypeReferenceCreator(ReferenceContext referenceContext) {
      this.referenceContext = referenceContext;
    }

    private TypeReference resolveTypeReference(JSType type) {
      if (type.isVoidType() || type.isNullType()) {
        return type.visit(this);
      }

      // Nullable type and optional type are represented in closure by an union type with
      // respectively Null and Undefined. We don't use this information (yet), so we remove Null
      // or Undefined before to visit the type.
      return type.restrictByNotNullOrUndefined().visit(this);
    }

    private List<TypeReference> resolveTypeReferences(List<? extends JSType> types) {
      return types.stream().map(this::resolveTypeReference).collect(toList());
    }

    @Override
    public TypeReference caseNoType(NoType type) {
      return OBJECT;
    }

    @Override
    public TypeReference caseEnumElementType(EnumElementType type) {
      return new JavaTypeReference(checkNotNull(getJavaType(type)));
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
        // We use upper bounded wildcard type because constructor function are always producers.
        return new ParametrizedTypeReference(
            PredefinedTypeReference.JS_CONSTRUCTOR_FN,
            ImmutableList.of(
                WildcardTypeReference.createWildcardUpperBound(
                    resolveTypeReference(type.getTypeOfThis()))));
      }
      return new JavaTypeReference(checkNotNull(getJavaType(type)));
    }

    @Override
    public TypeReference caseObjectType(ObjectType type) {
      String typeFqn = type.getNormalizedReferenceName();

      if (PredefinedTypeReference.isPredefinedType(typeFqn)) {
        return PredefinedTypeReference.getPredefinedType(typeFqn);
      }

      return new JavaTypeReference(checkNotNull(getJavaType(type)));
    }

    @Override
    public TypeReference caseUnknownType() {
      return OBJECT;
    }

    @Override
    public TypeReference caseNamedType(NamedType type) {
      // Reference to undefined types are wrapped in a NamedType with a reference to UnknownType.
      checkState(!type.isNoResolvedType(), "Type %s is unknown", type.getReferenceName());

      return resolveTypeReference(type.getReferencedType());
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
    public TypeReference caseSymbolType() {
      // TODO(b/73255220): add support for symbol type.
      return OBJECT;
    }

    @Override
    public TypeReference caseVoidType() {
      return referenceContext == REGULAR ? VOID : VOID_OBJECT;
    }

    @Override
    public TypeReference caseUnionType(UnionType type) {
      return new UnionTypeReference(resolveTypeReferences(type.getAlternates()));
    }

    @Override
    public TypeReference caseTemplatizedType(TemplatizedType type) {
      if (type.getReferencedType().isArrayType()) {
        TypeReference arrayType = PredefinedTypeReference.OBJECT;
        if (type.getTemplateTypes().size() == 1) {
          arrayType =
              new TypeReferenceCreator(
                      referenceContext == IN_HERITAGE_CLAUSE ? IN_TYPE_ARGUMENTS : REGULAR)
                  .resolveTypeReference(type.getTemplateTypes().get(0));
        }
        if (referenceContext == IN_HERITAGE_CLAUSE) {
          // In java you cannot extends classic array. In this case create a parametrized reference
          // to JsArray class.
          return new ParametrizedTypeReference(
              resolveTypeReference(type.getReferencedType()), newArrayList(arrayType));
        } else {
          // Convert array type to classic java array where it's valid.
          return new ArrayTypeReference(arrayType);
        }
      }

      return createParametrizedTypeReference(type.getReferencedType(), type.getTemplateTypes());
    }

    @Override
    public TypeReference caseTemplateType(TemplateType templateType) {
      if (jsTypeByThisTemplateType.containsKey(templateType)) {
        // The templateType is used in @this annotations in order to allow method chaining.
        // Replace it by the type where the method is defined.
        return createMethodDefiningTypeReferenceFrom(templateType);
      }

      return new TypeVariableReference(templateType.getReferenceName(), null);
    }

    private TypeReference createMethodDefiningTypeReferenceFrom(TemplateType templateType) {
      JSType jsType = jsTypeByThisTemplateType.get(templateType);
      checkNotNull(jsType, "%s is not used a method receiver.", templateType);

      TypeReference typeReference = resolveTypeReference(jsType);
      List<TemplateType> templateKeys =
          (jsType instanceof ObjectType ? ((ObjectType) jsType).getConstructor() : jsType)
              .getTemplateTypeMap()
              .getTemplateKeys();
      // Create a ParametrizedTypeReference if the replacing type has template type. The JSType
      // stored in the map is never a TemplatizedType because it's the type definition not a type
      // reference.
      if (templateKeys.isEmpty()) {
        return typeReference;
      } else if (jsType.isArrayType()) {
        checkState(templateKeys.size() == 1, templateKeys);
        return new ArrayTypeReference(resolveTypeReference(templateKeys.get(0)));
      } else {
        return createParametrizedTypeReference(jsType, templateKeys);
      }
    }

    private ParametrizedTypeReference createParametrizedTypeReference(
        JSType referencedType, List<? extends JSType> templatesTypes) {
      TypeReference templatizedType = resolveTypeReference(referencedType);

      List<TypeReference> templates =
          new TypeReferenceCreator(IN_TYPE_ARGUMENTS).resolveTypeReferences(templatesTypes);

      return new ParametrizedTypeReference(templatizedType, templates);
    }
  }
}
