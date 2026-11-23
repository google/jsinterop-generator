/*
 * Copyright 2026 Google Inc.
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
package jsinterop.generator.visitor;

import static jsinterop.generator.model.AnnotationType.FUNCTIONAL_INTERFACE;
import static jsinterop.generator.model.EntityKind.INTERFACE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import jsinterop.generator.model.Method;
import jsinterop.generator.model.Program;
import jsinterop.generator.model.Type;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FunctionalInterfaceAnnotatorTest {

  @Test
  public void singleAbstractMethod_annotated() {
    Type type = createInterface(abstractMethod("foo"));
    runAnnotator(type);
    assertTrue(type.hasAnnotation(FUNCTIONAL_INTERFACE));
  }

  @Test
  public void singleDefaultMethod_notAnnotated() {
    Type type = createInterface(defaultMethod("foo"));
    runAnnotator(type);
    assertFalse(type.hasAnnotation(FUNCTIONAL_INTERFACE));
  }

  @Test
  public void singleStaticMethod_notAnnotated() {
    Type type = createInterface(staticMethod("foo"));
    runAnnotator(type);
    assertFalse(type.hasAnnotation(FUNCTIONAL_INTERFACE));
  }

  @Test
  public void defaultAndStaticMethodsOnly_notAnnotated() {
    Type type = createInterface(defaultMethod("foo"), staticMethod("bar"));
    runAnnotator(type);
    assertFalse(type.hasAnnotation(FUNCTIONAL_INTERFACE));
  }

  @Test
  public void singleAbstractMethodWithDefaultAndStaticHelpers_annotated() {
    Type type = createInterface(abstractMethod("foo"), defaultMethod("bar"), staticMethod("baz"));
    runAnnotator(type);
    assertTrue(type.hasAnnotation(FUNCTIONAL_INTERFACE));
  }

  @Test
  public void twoAbstractMethodsWithDefaultHelper_notAnnotated() {
    Type type = createInterface(abstractMethod("foo"), abstractMethod("bar"), defaultMethod("baz"));
    runAnnotator(type);
    assertFalse(type.hasAnnotation(FUNCTIONAL_INTERFACE));
  }

  private static void runAnnotator(Type type) {
    Program program = new Program(Collections.emptyMap());
    program.addType(type);
    new FunctionalInterfaceAnnotator().applyTo(program);
  }

  private static Type createInterface(Method... methods) {
    Type type = new Type(INTERFACE);
    type.setName("TestInterface");
    for (Method method : methods) {
      type.addMethod(method);
    }
    return type;
  }

  private static Method abstractMethod(String name) {
    Method method = new Method();
    method.setName(name);
    return method;
  }

  private static Method defaultMethod(String name) {
    Method method = abstractMethod(name);
    method.setDefault(true);
    return method;
  }

  private static Method staticMethod(String name) {
    Method method = abstractMethod(name);
    method.setStatic(true);
    return method;
  }
}
