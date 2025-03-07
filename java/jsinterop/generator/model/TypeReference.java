/*
 * Copyright 2015 Google Inc.
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

package jsinterop.generator.model;

import static jsinterop.generator.model.LiteralExpression.NULL;

import com.google.j2cl.common.visitor.Processor;
import com.google.j2cl.common.visitor.Visitable;

/** A usage-site reference to a type. */
@Visitable
public abstract class TypeReference {

  private final boolean isNullable;

  protected TypeReference(boolean isNullable) {
    this.isNullable = isNullable;
  }

  public boolean isNullable() {
    return isNullable;
  }

  /** Returns the non nullable version of the referenced type. */
  public abstract TypeReference toNonNullableTypeReference();

  /** Returns the nullable version of the referenced type. */
  public abstract TypeReference toNullableTypeReference();

  public abstract String getTypeName();

  public abstract String getImport();

  public String getComment() {
    return null;
  }

  /**
   * Returns the representation of the type reference in jsDoc annotation format.
   *
   * <p>The native fqn is composed of
   *
   * <ul>
   *   <li>For {@link JavaTypeReference}: the JsDoc annotation of the native type.
   *       <p>Ex:
   *       <ul>
   *         <li>{@code com.foo.Bar} for a type
   *         <li>{@code {bar:string}} for a type literal
   *         <li>{@code function(string):boolean} for a function type
   *       </ul>
   *   <li>For {@link ArrayTypeReference}: the JsDoc Annotation String of the array type followed by
   *       []. Ex: com.foo.Bar[]
   *   <li>For {@link ParametrizedTypeReference}: the JsDoc Annotation String of the underlying type
   *       followed by the coma separated list of JsDoc Annotation String of type parameters
   *       surrounded by &lt; and &gt; Ex: com.foo.Bar&lt;string,com.foo.Foo&gt;
   *   <li>For {@link TypeVariableReference}: the name of the type variable. Ex: T
   *   <li>For {@link UnionTypeReference}: a | separated list of JsDoc Annotation String of each
   *       type reference involved in the union type surrounded by parenthesis. Ex:
   *       (com.foo.Bar|com.foo.Foo|string)
   * </ul>
   */
  public abstract String getJsDocAnnotationString();

  /**
   * Returns the java fully qualified name of the referenced type.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se7/html/jls-6.html#jls-6.7">fqn
   *     specification</a>
   */
  public abstract String getJavaTypeFqn();

  /**
   * Returns the java qualified name relative to the top level parent.
   *
   * <p>
   *
   * <pre>
   *   Ex:
   *     class Foo {
   *       interface FooInner {
   *         interface FooInnerInner {}
   *       }
   *     }
   *
   *   fooReference.getJavaRelativeQualifiedTypeName() returns Foo
   *   fooInnerReference.getJavaRelativeQualifiedTypeName() returns Foo.FooInner
   *   fooInnerInnerReference.getJavaRelativeQualifiedTypeName() returns Foo.FooInner.FooInnerInner
   * </pre>
   */
  public abstract String getJavaRelativeQualifiedTypeName();

  /**
   * Returns the java type signature of the referenced type as specified in the java specification.
   *
   * @see <a
   *     href="http://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html#type_signatures">
   *     type signature specification</a>
   */
  public abstract String getJniSignature();

  public TypeReference accept(Processor processor) {
    return acceptInternal(processor);
  }

  TypeReference acceptInternal(Processor processor) {
    return Visitor_TypeReference.visit(processor, this);
  }

  public Expression getDefaultValue() {
    return NULL;
  }

  /**
   * Returns true if the type reference is a reference to a type that can be legally used in
   * instanceof clause
   */
  public boolean isInstanceofAllowed() {
    return true;
  }

  public Type getTypeDeclaration() {
    // by default, a type reference doesn't have access to the type declaration
    return null;
  }

  public boolean isReferenceTo(PredefinedTypes predefinedType) {
    return false;
  }
}
