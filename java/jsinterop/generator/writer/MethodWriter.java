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
 */
package jsinterop.generator.writer;

import java.util.List;
import jsinterop.generator.model.AccessModifier;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Parameter;
import jsinterop.generator.model.Type;

/** MethodWriter generates java code for {@link Method} instances. */
public class MethodWriter {
  public static void emitConstructor(Method constructor, CodeWriter codeWriter, Type parentType) {
    if (parentType.isInterface()) {
      return;
    }

    codeWriter
        .emitAnnotations(constructor.getAnnotations())
        .emit(constructor.getAccessModifier())
        .emitGenerics(constructor.getTypeParameters(), true)
        .emit(" ")
        .emit(parentType.getName())
        .emit("(");

    emitParameters(constructor.getParameters(), codeWriter);

    codeWriter.emit("){");

    if (constructor.getBody() != null) {
      codeWriter.emitNewLine().emitStatement(constructor.getBody());
    }

    codeWriter.emit("}").emitNewLine();
  }

  public static void emit(Method method, CodeWriter codeWriter, Type parentType) {
    codeWriter.emitAnnotations(method.getAnnotations());

    AccessModifier accessModifier = method.getAccessModifier();

    if (!parentType.isInterface() || accessModifier == AccessModifier.PROTECTED) {
      codeWriter.emit(accessModifier).emit(" ");
    }

    if (method.isStatic()) {
      codeWriter.emit("static ");
    }

    if (method.isFinal()) {
      codeWriter.emit("final ");
    }

    if (method.isDefault()) {
      codeWriter.emit("default ");
    }

    if (!parentType.isInterface() && method.getBody() == null) {
      codeWriter.emit("native ");
    }

    codeWriter.emitGenerics(method.getTypeParameters(), true);

    codeWriter.emitTypeReference(method.getReturnType()).emit(" ").emit(method.getName()).emit("(");

    emitParameters(method.getParameters(), codeWriter);

    codeWriter.emit(")");

    if (method.getBody() != null) {
      codeWriter.emit("{").emitNewLine();
      codeWriter.emitStatement(method.getBody());
      codeWriter.emit("}");
    } else {
      codeWriter.emit(";");
    }

    codeWriter.emitNewLine();
  }

  private static void emitParameters(List<Parameter> parameters, CodeWriter codeWriter) {
    boolean first = true;
    for (Parameter parameter : parameters) {
      if (!first) {
        codeWriter.emit(",");
      }
      first = false;
      codeWriter.emitTypeReference(parameter.getType(), false);

      if (parameter.isVarargs()) {
        codeWriter.emit("...");
      }

      codeWriter.emit(" ").emit(parameter.getName());
    }
  }

  private MethodWriter() {}
}
