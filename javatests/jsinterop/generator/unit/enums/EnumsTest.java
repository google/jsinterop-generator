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
package jsinterop.generator.unit.enums;

import static jsinterop.generator.closure.TestUtil.runClosureJsInteropGenerator;
import static org.junit.Assert.assertThrows;

import com.google.javascript.jscomp.SourceFile;
import jsinterop.generator.helper.AbortError;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class EnumsTest {

  @Test
  public void unsupportedEnumTypes_throwException() {
    assertUnsupported("/** @enum {?string} */ var NullableStringEnum = {A: 'a'};");
    assertUnsupported("/** @enum {?number} */ var NullableNumberEnum = {A: 1};");
    assertUnsupported("/** @enum {?boolean} */ var NullableBooleanEnum = {A: true};");
    assertUnsupported("/** @enum {?} */ var UnknownEnum = {A: 'a'};");
    assertUnsupported("/** @enum {*} */ var AllTypeEnum = {A: 'a'};");
    assertUnsupported("/** @enum {!Object} */ var ObjectEnum = {A: {}};");
    assertUnsupported("/** @enum {(string|number)} */ var UnionEnum = {A: 'a', B: 1};");
  }

  private static void assertUnsupported(String enumDeclaration) {
    SourceFile source = SourceFile.fromCode("enums.js", "/** @externs */\n" + enumDeclaration);
    assertThrows(AbortError.class, () -> runClosureJsInteropGenerator(source));
  }
}
