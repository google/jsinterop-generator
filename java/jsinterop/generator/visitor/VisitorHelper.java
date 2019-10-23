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
 *
 */

package jsinterop.generator.visitor;

import java.util.List;
import java.util.Map;
import jsinterop.generator.helper.Problems;
import jsinterop.generator.model.Program;

/** Helper class that runs all java model visitors. */
public final class VisitorHelper {

  public static void finalizeJavaProgram(
      Program program,
      List<String> integerEntities,
      Map<String, String> wildcardTypes,
      Problems problems) {
    new ParametrizedObjectReferenceRewriter().applyTo(program);

    new EmptyNamespaceFilter().applyTo(program);

    new DictionaryTypeVisitor().applyTo(program);

    new FieldsConverter().applyTo(program);

    new ClosureOptionalParameterCleaner().applyTo(program);

    new OptionalParameterHandler().applyTo(program);

    new ValidJavaIdentifierVisitor().applyTo(program);

    new IntegerEntitiesConverter(integerEntities, problems).applyTo(program);

    new ConstantRewriter().applyTo(program);

    new UnionTypeMethodParameterHandler().applyTo(program);

    UnionTypeHelperTypeCreator unionTypeHelperTypeCreator = new UnionTypeHelperTypeCreator();
    unionTypeHelperTypeCreator.applyTo(program);

    // These 2 visitors below have to visit our model until no more changes are made.
    // This is because inner types can refer to other inner types. Fixing one inner type has impact
    // on other inner type that refer to it.
    // ex:
    //  interface Bar { T onInvoke(U foo);}
    //  interface Foo { V onInvoke(Bar bar);}
    // after the first pass:
    //  interface Bar<T,U> { T onInvoke(U foo);}
    //  interface Foo<V> { V onInvoke(Bar<T,U> bar);}
    // Need a second pass to now fix Foo:
    //  interface Bar<T,U> { T onInvoke(U foo);}
    //  interface Foo<V,T,U> { V onInvoke(Bar<T,U> bar);}
    while (true) {
      new FixTypeParametersOfSyntheticTypes().applyTo(program);

      FixTypeParametersOfReferencesToSyntheticTypes fixTypeParametersOfReferencesToSyntheticTypes =
          new FixTypeParametersOfReferencesToSyntheticTypes();
      fixTypeParametersOfReferencesToSyntheticTypes.applyTo(program);
      if (!fixTypeParametersOfReferencesToSyntheticTypes.isSyntheticTypeChanged()) {
        break;
      }
    }
    DuplicatedTypesUnifier duplicatedTypesUnifier = new DuplicatedTypesUnifier();
    duplicatedTypesUnifier.applyTo(program);

    new FixReferencesToDuplicatedTypes(duplicatedTypesUnifier.getTypesToReplace()).applyTo(program);

    new MembersClassCleaner().applyTo(program);

    new ConstructorVisitor().applyTo(program);

    new WildcardTypeCreator(unionTypeHelperTypeCreator.getUnionTypeHelperTypes(), wildcardTypes)
        .applyTo(program);

    new JsConstructorFnParameterJsOverlayCreator().applyTo(program);

    new JavaArrayParameterJsOverlayCreator().applyTo(program);

    new ObjectParameterJsOverlayCreator().applyTo(program);

    new NamespaceAttributeRewriter().applyTo(program);

    new FunctionalInterfaceAnnotator().applyTo(program);
  }

  private VisitorHelper() {}
}
