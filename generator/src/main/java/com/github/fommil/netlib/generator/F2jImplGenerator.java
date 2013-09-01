/*
 * Copyright (C) 2013 Samuel Halliday
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see [http://www.gnu.org/licenses/].
 */
package com.github.fommil.netlib.generator;

import com.google.common.collect.Lists;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.stringtemplate.v4.ST;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Generates the F2J implementation of a netlib-java interface for the given methods.
 */
@Mojo(
    name = "f2j",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE
)
public class F2jImplGenerator extends AbstractJavaGenerator {

  /**
   * The interface that we are implementing.
   */
  @Parameter(required = true)
  protected String implementing;

  @Override
  protected String generate(List<Method> methods) throws Exception {
    List<String> members = Lists.newArrayList();
    for (Method method : methods) {
      members.add(renderMethod(method, false));
      if (hasOffsets(method))
        members.add(renderMethod(method, true));
    }

    ST t = jTemplates.getInstanceOf("implClass");
    t.add("package", getTargetPackage());
    t.add("name", getTargetClassName());
    t.add("members", members);
    t.add("docs", getGenerationSummaryJavadocs());
    t.add("parent", implementing);

    return t.render();
  }

  private String renderMethod(Method method, boolean offsets) {
    ST m = jTemplates.getInstanceOf("f2jImplMethod");
    m.add("returns", method.getReturnType());
    m.add("method", method.getName());
    m.add("paramTypes", getNetlibJavaParameterTypes(method, offsets));
    m.add("paramNames", getNetlibJavaParameterNames(method, offsets));
    m.add("impl", method.getDeclaringClass().getCanonicalName() + "." + method.getName());
    m.add("calls", getF2jJavaParameters(method, offsets));
    if (method.getReturnType().equals(Void.TYPE))
      m.add("return", "");
    return m.render();
  }

}
