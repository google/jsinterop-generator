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
import jsinterop.generator.model.Program;

/** Helper class that runs all java model visitors. */
public final class VisitorHelper {

  public static void finalizeJavaProgram(
      Program program, boolean useBeanConvention, List<String> integerEntities) {
    new EmptyNamespaceFilter().accept(program);

    new DictionaryTypeVisitor().accept(program);

    new FieldsConverter(useBeanConvention).accept(program);

    new ClosureOptionalParameterCleaner().accept(program);

    new OptionalParameterHandler().accept(program);

    new ValidJavaIdentifierVisitor().accept(program);

    new IntegerEntitiesConverter(integerEntities).accept(program);

    new UnionTypeMethodParameterHandler().accept(program);

    new UnionTypeHelperTypeCreator().accept(program);

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
      new FixTypeParametersOfSyntheticTypes().accept(program);

      FixTypeParametersOfReferencesToSyntheticTypes fixTypeParametersOfReferencesToSyntheticTypes =
          new FixTypeParametersOfReferencesToSyntheticTypes();
      fixTypeParametersOfReferencesToSyntheticTypes.accept(program);
      if (!fixTypeParametersOfReferencesToSyntheticTypes.isSyntheticTypeChanged()) {
        break;
      }
    }
    DuplicatedTypesUnifier duplicatedTypesUnifier = new DuplicatedTypesUnifier(useBeanConvention);
    duplicatedTypesUnifier.accept(program);

    new FixReferencesToDuplicatedTypes(duplicatedTypesUnifier.getTypesToReplace()).accept(program);

    new MembersClassCleaner().accept(program);

    new ConstructorVisitor().accept(program);

    new NamespaceAttributeRewriter().accept(program);
  }

  private VisitorHelper() {}
}
