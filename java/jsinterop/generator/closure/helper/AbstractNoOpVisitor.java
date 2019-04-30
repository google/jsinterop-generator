/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jsinterop.generator.closure.helper;

import com.google.javascript.rhino.jstype.EnumElementType;
import com.google.javascript.rhino.jstype.FunctionType;
import com.google.javascript.rhino.jstype.NamedType;
import com.google.javascript.rhino.jstype.NoType;
import com.google.javascript.rhino.jstype.ObjectType;
import com.google.javascript.rhino.jstype.ProxyObjectType;
import com.google.javascript.rhino.jstype.TemplateType;
import com.google.javascript.rhino.jstype.TemplatizedType;
import com.google.javascript.rhino.jstype.UnionType;
import com.google.javascript.rhino.jstype.Visitor;

/**
 * Nominal implementation of Visitor<T> returning null for every methods. The purpose of this class
 * is to reduce boiler plate code when we are interested to visit small number of kind of types.
 */
public abstract class AbstractNoOpVisitor<T> implements Visitor<T> {
  @Override
  public T caseNoType(NoType type) {
    return null;
  }

  @Override
  public T caseEnumElementType(EnumElementType type) {
    return null;
  }

  @Override
  public T caseAllType() {
    return null;
  }

  @Override
  public T caseBooleanType() {
    return null;
  }

  @Override
  public T caseNoObjectType() {
    return null;
  }

  @Override
  public T caseFunctionType(FunctionType type) {
    return null;
  }

  @Override
  public T caseObjectType(ObjectType type) {
    return null;
  }

  @Override
  public T caseUnknownType() {
    return null;
  }

  @Override
  public T caseNullType() {
    return null;
  }

  @Override
  public T caseNamedType(NamedType type) {
    return null;
  }

  @Override
  public T caseProxyObjectType(ProxyObjectType type) {
    return null;
  }

  @Override
  public T caseNumberType() {
    return null;
  }

  @Override
  public T caseStringType() {
    return null;
  }

  @Override
  public T caseSymbolType() {
    return null;
  }

  @Override
  public T caseVoidType() {
    return null;
  }

  @Override
  public T caseUnionType(UnionType type) {
    return null;
  }

  @Override
  public T caseTemplatizedType(TemplatizedType type) {
    return null;
  }

  @Override
  public T caseTemplateType(TemplateType templateType) {
    return null;
  }
}
