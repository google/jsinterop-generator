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
import static com.google.common.collect.ImmutableList.toImmutableList;
import static jsinterop.generator.helper.AbstractTypeRegistry.ReferenceContext.IN_TYPE_ARGUMENTS;
import static jsinterop.generator.helper.AbstractTypeRegistry.ReferenceContext.REGULAR;
import static jsinterop.generator.model.PredefinedTypes.BOOLEAN;
import static jsinterop.generator.model.PredefinedTypes.BOOLEAN_OBJECT;
import static jsinterop.generator.model.PredefinedTypes.DOUBLE;
import static jsinterop.generator.model.PredefinedTypes.DOUBLE_OBJECT;
import static jsinterop.generator.model.PredefinedTypes.JS_BIGINT;
import static jsinterop.generator.model.PredefinedTypes.JS_CONSTRUCTOR_FN;
import static jsinterop.generator.model.PredefinedTypes.OBJECT;
import static jsinterop.generator.model.PredefinedTypes.STRING;
import static jsinterop.generator.model.PredefinedTypes.VOID;
import static jsinterop.generator.model.PredefinedTypes.VOID_OBJECT;

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
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import jsinterop.generator.helper.AbstractTypeRegistry;
import jsinterop.generator.model.ArrayTypeReference;
import jsinterop.generator.model.JavaTypeReference;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.PredefinedTypes;
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

  public void registerJavaType(JSType jsType, Type javaType) {
    registerJavaTypeByKey(jsType, javaType);
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
    return new TypeReferenceCreator(referenceContext, isNullable(jsType))
        .resolveTypeReference(jsType);
  }

  private static boolean isNullable(JSType jsType) {
    if (jsType instanceof UnionType) {
      return jsType.toMaybeUnionType().getAlternates().stream()
          .anyMatch(ClosureTypeRegistry::isNullable);
    }
    return jsType.isNullable()
        // TemplateType.isNullable() always returns true but a template type should only be
        // considered as nullable if they are marked as nullable:  `?T` (seen as T|null)
        && !(jsType instanceof TemplateType);
  }

  /**
   * Create a mapping between a template variable used as receiver type of a method (with @this
   * annotation) and the type defining the method.
   */
  public void registerThisTemplateType(TemplateType thisTemplateType, JSType jsType) {
    jsTypeByThisTemplateType.put(thisTemplateType, jsType);
  }

  private class TypeReferenceCreator extends Visitor.WithDefaultCase<TypeReference> {
    private final ReferenceContext referenceContext;
    private final boolean isNullable;

    TypeReferenceCreator(ReferenceContext referenceContext, boolean isNullable) {
      this.referenceContext = referenceContext;
      this.isNullable = isNullable;
    }

    @Override
    protected TypeReference caseDefault(JSType type) {
      return null;
    }

    private TypeReference resolveTypeReference(JSType type) {
      if (type.isVoidType() || type.isNullType()) {
        return type.visit(this);
      }
      return type.restrictByNotNullOrUndefined().visit(this);
    }

    private List<TypeReference> resolveTypeReferences(List<? extends JSType> types) {
      return types.stream().map(this::resolveTypeReference).collect(toImmutableList());
    }

    @Override
    public TypeReference caseNoType(NoType type) {
      return OBJECT.getReference(isNullable);
    }

    @Override
    public TypeReference caseEnumElementType(EnumElementType type) {
      return new JavaTypeReference(checkNotNull(getJavaType(type)), isNullable);
    }

    @Override
    public TypeReference caseAllType() {
      return OBJECT.getReference(isNullable);
    }

    @Override
    public TypeReference caseBooleanType() {
      return (referenceContext == REGULAR && !isNullable ? BOOLEAN : BOOLEAN_OBJECT)
          .getReference(isNullable);
    }

    @Override
    public TypeReference caseNoObjectType() {
      return OBJECT.getReference(isNullable);
    }

    @Override
    public TypeReference caseFunctionType(FunctionType type) {
      if (type.isConstructor() && type.getDisplayName() == null) {
        // We use upper bounded wildcard type because constructor function are always producers.
        return new ParametrizedTypeReference(
            JS_CONSTRUCTOR_FN.getReference(isNullable),
            ImmutableList.of(
                WildcardTypeReference.createWildcardUpperBound(
                    new TypeReferenceCreator(IN_TYPE_ARGUMENTS, false)
                        .resolveTypeReference(type.getTypeOfThis()))));
      }
      return new JavaTypeReference(checkNotNull(getJavaType(type)), isNullable);
    }

    @Override
    public TypeReference caseObjectType(ObjectType type) {
      String typeFqn = type.getNormalizedReferenceName();

      if (PredefinedTypes.isPredefinedType(typeFqn)) {
        return PredefinedTypes.getPredefinedType(typeFqn).getReference(isNullable);
      }

      return new JavaTypeReference(checkNotNull(getJavaType(type)), isNullable);
    }

    @Override
    public TypeReference caseUnknownType() {
      return OBJECT.getReference(isNullable);
    }

    @Override
    public TypeReference caseNamedType(NamedType type) {
      return resolveTypeReference(type.getReferencedType());
    }

    @Override
    public TypeReference caseProxyObjectType(ProxyObjectType type) {
      throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public TypeReference caseNumberType() {
      return (referenceContext == REGULAR && !isNullable ? DOUBLE : DOUBLE_OBJECT)
          .getReference(isNullable);
    }

    @Override
    public TypeReference caseBigIntType() {
      return JS_BIGINT.getReference(isNullable);
    }

    @Override
    public TypeReference caseStringType() {
      return STRING.getReference(isNullable);
    }

    @Override
    public TypeReference caseSymbolType() {
      // TODO(b/73255220): add support for symbol type.
      return OBJECT.getReference(isNullable);
    }

    @Override
    public TypeReference caseVoidType() {
      return (referenceContext == REGULAR && !isNullable ? VOID : VOID_OBJECT)
          .getReference(isNullable);
    }

    @Override
    public TypeReference caseUnionType(UnionType type) {
      return new UnionTypeReference(
          // In our type model the UnionTypeReference will carry the nullability information. The
          // alternates are always considered as non-nullable.
          new TypeReferenceCreator(referenceContext, false)
              .resolveTypeReferences(type.getAlternates()),
          isNullable);
    }

    @Override
    public TypeReference caseTemplatizedType(TemplatizedType type) {
      return createParametrizedTypeReference(type.getReferencedType(), type.getTemplateTypes());
    }

    @Override
    public TypeReference caseTemplateType(TemplateType templateType) {
      if (jsTypeByThisTemplateType.containsKey(templateType)) {
        // The templateType is used in @this annotations in order to allow method chaining.
        // Replace it by the type where the method is defined.
        return createMethodDefiningTypeReferenceFrom(templateType);
      }

      return new TypeVariableReference(templateType.getReferenceName(), null, isNullable);
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
        TypeReference mainTypeReference = resolveTypeReference(templateKeys.get(0));

        return new ArrayTypeReference(mainTypeReference, isNullable);
      } else {
        return createParametrizedTypeReference(jsType, templateKeys);
      }
    }

    private ParametrizedTypeReference createParametrizedTypeReference(
        JSType referencedType, List<? extends JSType> templatesTypes) {
      return new ParametrizedTypeReference(
          resolveTypeReference(referencedType),
          templatesTypes.stream()
              .map(t -> createTypeReference(t, IN_TYPE_ARGUMENTS))
              .collect(toImmutableList()));
    }
  }
}
