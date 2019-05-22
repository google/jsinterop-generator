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
package jsinterop.generator.closure.visitor;

import static java.util.stream.StreamSupport.stream;
import static jsinterop.generator.helper.AbstractTypeRegistry.ReferenceContext.IN_HERITAGE_CLAUSE;

import com.google.javascript.rhino.jstype.FunctionType;
import com.google.javascript.rhino.jstype.ObjectType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jsinterop.generator.closure.helper.GenerationContext;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeReference;

/** Visit and convert inheritance clause. */
public class InheritanceVisitor extends AbstractClosureVisitor {

  public InheritanceVisitor(GenerationContext context) {
    super(context);
  }

  @Override
  protected boolean visitClassOrInterface(FunctionType type) {
    Type javaType = getCurrentJavaType();

    if (type.isInterface() && type.getExtendedInterfacesCount() > 0) {
      javaType.getExtendedTypes().addAll(createTypeReferences(type.getExtendedInterfaces()));
    } else if (type.isConstructor()) {
      getSuperType(type).ifPresent(t -> javaType.getExtendedTypes().add(createTypeReference(t)));
      javaType
          .getImplementedTypes()
          .addAll(createTypeReferences(type.getOwnImplementedInterfaces()));
    }

    return false;
  }

  private List<TypeReference> createTypeReferences(Iterable<ObjectType> types) {
    return stream(types.spliterator(), false)
        .map(this::createTypeReference)
        .collect(Collectors.toList());
  }

  private TypeReference createTypeReference(ObjectType type) {
    return getJavaTypeRegistry().createTypeReference(type, IN_HERITAGE_CLAUSE);
  }

  private Optional<ObjectType> getSuperType(FunctionType type) {
    ObjectType proto = type.getPrototype();
    if (proto == null) {
      return Optional.empty();
    }

    ObjectType implicitProto = proto.getImplicitPrototype();
    if (implicitProto == null) {
      return Optional.empty();
    }

    return "Object".equals(implicitProto.getDisplayName())
        ? Optional.empty()
        : Optional.of(implicitProto);
  }
}
