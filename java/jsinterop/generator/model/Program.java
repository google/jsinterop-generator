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

import com.google.common.collect.ImmutableList;
import com.google.j2cl.common.visitor.Context;
import com.google.j2cl.common.visitor.Processor;
import com.google.j2cl.common.visitor.Visitable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Keep the list of generated types. */
@Visitable
@Context
public class Program implements Node {

  private final Map<String, String> thirdPartyTypesMapping;
  @Visitable List<Type> types = new ArrayList<>();

  public Program(Map<String, String> thirdPartyTypesMapping) {
    this.thirdPartyTypesMapping = thirdPartyTypesMapping;
  }

  public void addType(Type type) {
    types.add(type);
  }

  public List<Type> getAllTypes() {
    return ImmutableList.copyOf(types);
  }

  @Override
  public Node accept(Processor processor) {
    return Visitor_Program.visit(processor, this);
  }

  public boolean isThirdPartyType(String tsFqn) {
    return thirdPartyTypesMapping.containsKey(tsFqn);
  }

  public String getThirdPartyTypeJavaFqn(String tsFqn) {
    return thirdPartyTypesMapping.get(tsFqn);
  }
}
