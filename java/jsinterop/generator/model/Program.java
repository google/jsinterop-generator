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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/** Keep the list of generated types. */
public class Program implements Visitable<Program> {
  private final Map<String, String> thirdPartyTypesMapping;
  private final List<Type> types;

  public Program(Map<String, String> thirdPartyTypesMapping) {
    this.thirdPartyTypesMapping = thirdPartyTypesMapping;
    this.types = new ArrayList<>();
  }

  public void addType(Type type) {
    types.add(type);
  }

  public List<Type> getAllTypes() {
    return ImmutableList.copyOf(types);
  }

  @Override
  public Program doVisit(ModelVisitor visitor) {
    if (visitor.visit(this)) {
      visitor.accept(types);
    }
    visitor.endVisit(this);

    return this;
  }

  public boolean isThirdPartyType(String tsFqn) {
    return thirdPartyTypesMapping.containsKey(tsFqn);
  }

  public String getThirdPartyTypeJavaFqn(String tsFqn) {
    return thirdPartyTypesMapping.get(tsFqn);
  }

  public void removeTypes(Collection<Type> typeToDelete) {
    types.removeAll(typeToDelete);
  }
}
