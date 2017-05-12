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

import static com.google.common.base.Strings.isNullOrEmpty;

import jsinterop.generator.model.AccessModifier;
import jsinterop.generator.model.Field;
import jsinterop.generator.model.Type;

/** FieldWriter generates java code for {@link Field} instances. */
public class FieldWriter {

  public static void emit(Field field, CodeWriter writer, Type parentType) {
    writer.emitAnnotations(field.getAnnotations());

    AccessModifier accessModifier = field.getAccessModifier();

    if (!parentType.isInterface() || accessModifier == AccessModifier.PROTECTED) {
      writer.emit(accessModifier).emit(" ");
    }

    if (!parentType.isInterface() && field.isStatic()) {
      writer.emit("static ");
    }

    if (!parentType.isInterface() && field.isFinal()) {
      writer.emit("final ");
    }

    writer.emitTypeReference(field.getType()).emit(" ").emit(field.getName());

    if (!isNullOrEmpty(field.getInitialValue())) {
      writer.emit("=").emit(field.getInitialValue());
    }

    writer.emit(";").emitNewLine();
  }

  private FieldWriter() {}
}
