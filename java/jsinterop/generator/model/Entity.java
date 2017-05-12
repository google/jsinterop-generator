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

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/** Super class to be extended by class modeling Java entity. */
public abstract class Entity {
  protected static void copyEntityProperties(Entity from, Entity to) {
    to.setName(from.getName());
    to.setStatic(from.isStatic());
    to.setFinal(from.isFinal());
    to.getAnnotations().addAll(from.getAnnotations());
  }

  private String name;
  private AccessModifier accessModifier = AccessModifier.PUBLIC;
  private EntityKind kind;
  private List<Annotation> annotations = new LinkedList<>();
  private boolean finalModifier;
  private boolean staticModifier;

  public EntityKind getKind() {
    return kind;
  }

  public void setKind(EntityKind kind) {
    this.kind = kind;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Annotation> getAnnotations() {
    return annotations;
  }

  public void addAnnotation(Annotation annotation) {
    annotations.add(annotation);
  }

  public boolean isStatic() {
    return staticModifier;
  }

  public void setStatic(boolean isStatic) {
    this.staticModifier = isStatic;
  }

  public void setFinal(boolean aFinal) {
    this.finalModifier = aFinal;
  }

  public boolean isFinal() {
    return finalModifier;
  }

  public boolean hasAnnotation(AnnotationType type) {
    return getAnnotation(type) != null;
  }

  public Annotation getAnnotation(AnnotationType annotationType) {
    for (Annotation annotation : annotations) {
      if (annotation.getType() == annotationType) {
        return annotation;
      }
    }
    return null;
  }

  public boolean removeAnnotation(AnnotationType annotationType) {
    for (int i = 0; i < annotations.size(); i++) {
      if (annotations.get(i).getType() == annotationType) {
        annotations.remove(i);
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Entity entity = (Entity) o;

    return Objects.equals(getName(), entity.getName())
        && Objects.equals(getKind(), entity.getKind())
        && Objects.equals(staticModifier, entity.staticModifier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getKind(), getName(), staticModifier);
  }

  public AccessModifier getAccessModifier() {
    return accessModifier;
  }

  public void setAccessModifier(AccessModifier accessModifier) {
    this.accessModifier = accessModifier;
  }
}
