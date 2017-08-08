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
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;
import static jsinterop.generator.model.LiteralExpression.NULL;
import static jsinterop.generator.model.PredefinedTypeReference.OBJECT;

import com.google.common.base.Splitter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import jsinterop.generator.model.AccessModifier;
import jsinterop.generator.model.Annotation;
import jsinterop.generator.model.ArrayTypeReference;
import jsinterop.generator.model.CastExpression;
import jsinterop.generator.model.Expression;
import jsinterop.generator.model.ExpressionStatement;
import jsinterop.generator.model.InstanceOfExpression;
import jsinterop.generator.model.JavaTypeReference;
import jsinterop.generator.model.LiteralExpression;
import jsinterop.generator.model.MethodInvocation;
import jsinterop.generator.model.ParametrizedTypeReference;
import jsinterop.generator.model.ReturnStatement;
import jsinterop.generator.model.Statement;
import jsinterop.generator.model.Type;
import jsinterop.generator.model.TypeQualifier;
import jsinterop.generator.model.TypeReference;
import jsinterop.generator.model.TypeVariableReference;

/** CodeWriter is used to generate basic piece of java code. */
// TODO(b/34251635): rewrite the code writing process as a visitor that visit the model. That will
// simplify the code and avoid all these instanceof expressions.
public class CodeWriter {
  // TODO(dramaix): remove this. Users can use external formatter in order to format
  // the generated code.
  private static final int JAVADOC_MAX_LENGTH = 100;

  private final Type mainType;
  private final StringBuilder builder = new StringBuilder();
  private String packageName;
  private Map<String, TypeReference> importedType = new HashMap<>();

  public CodeWriter(Type mainType) {
    this.mainType = mainType;
    // Add main type as an import eagarly to avoid name clashes.
    addImport(new JavaTypeReference(mainType));
  }

  public CodeWriter emit(String s) {
    builder.append(s);
    return this;
  }

  public CodeWriter emitNewLine() {
    emit("\n");
    return this;
  }

  public String asString() {
    return builder.toString();
  }

  public boolean addImport(TypeReference typeReference) {
    String typeNameToImport = getImportedTypeName(typeReference);

    TypeReference alreadyImportedType = importedType.get(typeNameToImport);

    if (alreadyImportedType == null) {
      importedType.put(typeNameToImport, typeReference);
      return true;
    }

    return Objects.equals(alreadyImportedType.getImport(), typeReference.getImport());
  }

  private String getImportedTypeName(TypeReference typeReference) {
    String typeReferenceImport = typeReference.getImport();
    if (typeReferenceImport == null) {
      return null;
    }

    int index = typeReferenceImport.lastIndexOf('.');

    return typeReferenceImport.substring(index + 1);
  }

  public CodeWriter emitAnnotations(List<Annotation> annotations) {
    annotations.forEach(a -> AnnotationWriter.emit(a, this));
    return this;
  }

  public CodeWriter emitGenerics(Collection<TypeReference> typeGenerics, boolean emitConstraint) {
    return emitTypeReferences(typeGenerics.iterator(), "<", ">", emitConstraint);
  }

  public CodeWriter emitTypeReferences(Collection<TypeReference> typeReferences) {
    return emitTypeReferences(typeReferences.iterator(), "", "", false);
  }

  private CodeWriter emitTypeReferences(
      Iterator<TypeReference> typeReferences, String start, String end, boolean emitConstraint) {

    if (typeReferences.hasNext()) {
      emit(start);
      emitTypeReference(typeReferences.next(), emitConstraint);
      while (typeReferences.hasNext()) {
        emit(", ");
        emitTypeReference(typeReferences.next(), emitConstraint);
      }
      emit(end);
    }

    return this;
  }

  public void setPackage(String aPackageName) {
    // set the package name only once.
    if (packageName != null) {
      throw new IllegalStateException("Package name already set.");
    }

    packageName = aPackageName;
  }

  public CodeWriter emitJavadoc(String javadoc) {
    if (isNullOrEmpty(javadoc)) {
      return this;
    }

    emit("/**");
    emitNewLine();

    List<String> javadocLines = Splitter.on('\n').splitToList(javadoc);

    for (String line : javadocLines) {
      List<String> words = Splitter.on(' ').splitToList(line);

      StringBuilder lineBuilder = new StringBuilder();

      for (String word : words) {
        if (lineBuilder.length() + word.length() > JAVADOC_MAX_LENGTH) {
          emit(" * ").emit(lineBuilder.toString());
          emitNewLine();
          lineBuilder = new StringBuilder();
        }

        lineBuilder.append(" ").append(word);
      }
      emit(" * ").emit(lineBuilder.toString());
      emitNewLine();
    }

    emit(" */");
    emitNewLine();
    return this;
  }

  public CodeWriter emitSingleLineComment(String comment) {
    if (isNullOrEmpty(comment)) {
      return this;
    }

    emit("/* ");
    emit(comment);
    emit(" */ ");

    return this;
  }

  public CodeWriter emitTypeReference(TypeReference type) {
    return emitTypeReference(type, false);
  }

  public CodeWriter emitTypeReference(TypeReference typeReference, boolean emitConstraint) {
    emitSingleLineComment(typeReference.getComment());

    if (typeReference instanceof ArrayTypeReference) {
      emitTypeReference(((ArrayTypeReference) typeReference).getArrayType());
      emit("[]");
    } else if (typeReference instanceof TypeVariableReference) {
      emit(typeReference.getTypeName());

      TypeReference constraint = ((TypeVariableReference) typeReference).getUpperBound();
      if (emitConstraint && !OBJECT.getJavaTypeFqn().equals(constraint.getJavaTypeFqn())) {
        emit(" extends ").emitTypeReference(constraint);
      }
    } else if (typeReference instanceof ParametrizedTypeReference) {
      ParametrizedTypeReference parametrizedTypeReference =
          (ParametrizedTypeReference) typeReference;

      emitTypeReference(parametrizedTypeReference.getMainType());

      if (!parametrizedTypeReference.getActualTypeArguments().isEmpty()) {
        emitGenerics(parametrizedTypeReference.getActualTypeArguments(), emitConstraint);
      }
    } else {
      // Due to a bug in javac with import of inner type of inner type, we don't create import for
      // inner types.
      if (addImport(getTopLevelParentTypeReference(typeReference))) {
        emit(typeReference.getJavaRelativeQualifiedTypeName());
      } else {
        emit(typeReference.getJavaTypeFqn());
      }
    }
    return this;
  }

  private TypeReference getTopLevelParentTypeReference(TypeReference typeReference) {
    if (typeReference instanceof JavaTypeReference) {
      return new JavaTypeReference(
          ((JavaTypeReference) typeReference).getJavaType().getTopLevelParentType());
    }

    // the notion of ToplevelParentType doesn't exist for the other typeReference kind.
    return typeReference;
  }

  public String generateCode() {
    StringBuilder content = new StringBuilder();

    if (!isNullOrEmpty(packageName)) {
      content.append("package ").append(packageName).append(";\n");
    }

    writeImports(content);

    // print the content of the file emitted during the generation
    content.append(builder.toString());

    return content.toString();
  }

  private void writeImports(StringBuilder content) {
    Set<String> imports = newHashSet(transform(importedType.values(), TypeReference::getImport));

    String currentTypeImport = mainType.getJavaFqn();

    imports
        .stream()
        .filter(i -> !isNullOrEmpty(i) && !i.equals(currentTypeImport))
        .map(i -> "import " + i + ";\n")
        .forEach(content::append);
  }

  public CodeWriter emit(AccessModifier accessModifier) {
    emit(accessModifier.getLitteral());
    return this;
  }

  public CodeWriter emitStatement(Statement statement) {
    if (statement.getLeadingComment() != null) {
      emit("// ").emit(statement.getLeadingComment()).emitNewLine();
    }

    if (statement instanceof ReturnStatement) {
      emit("return ").emitExpression(((ReturnStatement) statement).getExpression());
    } else if (statement instanceof ExpressionStatement) {
      emitExpression(((ExpressionStatement) statement).getExpression());
    } else {
      throw new RuntimeException("Unknown Statement");
    }

    emit(";").emitNewLine();

    return this;
  }

  public CodeWriter emitExpression(Expression expression) {
    if (expression instanceof MethodInvocation) {
      return emitMethodInvocation(((MethodInvocation) expression));
    }

    if (expression instanceof TypeQualifier) {
      return emitTypeQualifier(((TypeQualifier) expression));
    }

    if (expression instanceof LiteralExpression) {
      return emit(((LiteralExpression) expression).getLiteral());
    }

    if (expression instanceof InstanceOfExpression) {
      InstanceOfExpression instanceOfExpression = (InstanceOfExpression) expression;
      return emitExpression(instanceOfExpression.getLeftOperand())
          .emit(" instanceof ")
          .emitTypeReference(instanceOfExpression.getRightOperand());
    }

    if (expression instanceof CastExpression) {
      CastExpression castExpression = (CastExpression) expression;
      return emit("(")
          .emitTypeReference(castExpression.getType())
          .emit(")")
          .emitExpression(castExpression.getExpression());
    }

    throw new RuntimeException("Unknown Expression");
  }

  private CodeWriter emitTypeQualifier(TypeQualifier typeQualifier) {
    return emitTypeReference(typeQualifier.getType());
  }

  private CodeWriter emitMethodInvocation(MethodInvocation methodInvocation) {
    if (methodInvocation.getInvocationTarget() != null) {
      emitExpression(methodInvocation.getInvocationTarget()).emit(".");
    }

    if (!methodInvocation.getLocalTypeArguments().isEmpty()) {
      emitGenerics(methodInvocation.getLocalTypeArguments(), false);
    }

    emit(methodInvocation.getMethodName()).emit("(");

    for (int i = 0; i < methodInvocation.getArguments().size(); i++) {
      if (i > 0) {
        emit(",");
      }
      Expression argumentExpression = methodInvocation.getArguments().get(i);

      // to avoid ambiguity, if the argument is the literal "null", add a cast operation
      if (NULL.equals(argumentExpression)) {
        emit("(").emitTypeReference(methodInvocation.getArgumentTypes().get(i)).emit(")");
      }

      emitExpression(argumentExpression);
    }

    emit(")");

    return this;
  }
}
