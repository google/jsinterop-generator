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
package jsinterop.generator.closure.visitor;

import com.google.javascript.rhino.jstype.FunctionType;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.TemplateType;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import jsinterop.generator.closure.helper.GenerationContext;

/**
 * Visit methods scanning for @this annotations that declare the receiver type to be just a template
 * variable defined in the method.
 *
 * <p>In the Closure type system, @this annotation can be used on a method to specify (or customize)
 * the type of the instance the method is called on. Because the template variable can be any type,
 * this pattern is useful for allowing seamless fluent style method even in subclasses.
 *
 * <p>This visitor is used to record these type variables so that they can be handled in a
 * meaningful way by the generator.
 */
public class ThisTemplateTypeVisitor extends AbstractClosureVisitor {
  private final Deque<JSType> currentJsTypeStack = new ArrayDeque<>();

  public ThisTemplateTypeVisitor(GenerationContext context) {
    super(context);
  }

  @Override
  protected boolean visitClassOrInterface(FunctionType type) {
    currentJsTypeStack.push(type.getInstanceType());
    return true;
  }

  @Override
  protected void endVisitClassOrInterface(FunctionType type) {
    currentJsTypeStack.pop();
  }

  @Override
  protected boolean visitMethod(FunctionType method, boolean isStatic) {
    Optional<TemplateType> thisTemplateType = getThisTemplateType(method);
    thisTemplateType.ifPresent(
        templateType ->
            getJavaTypeRegistry()
                .registerThisTemplateType(templateType, currentJsTypeStack.peek()));
    return true;
  }

  private Optional<TemplateType> getThisTemplateType(FunctionType type) {
    return type.getTypeOfThis().isTemplateType()
        ? Optional.of(type.getTypeOfThis().toMaybeTemplateType())
        : Optional.empty();
  }
}
