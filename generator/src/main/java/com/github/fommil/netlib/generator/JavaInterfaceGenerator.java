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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

@Mojo(
    name = "interface",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE
)
public class JavaInterfaceGenerator extends AbstractJavaGenerator {

  /**
   * The fallback implementation.
   */
  @Parameter(required = true)
  protected String fallback;

  /**
   * The default implementations (CSV).
   */
  @Parameter(required = true)
  protected String impls;

  /**
   * Arbitrary Java code that is run after instance creation.
   */
  @Parameter
  protected String initCode;

  @Override
  protected String generate(List<Method> methods) throws Exception {
    List<String> members = Lists.newArrayList();
    for (Method method : methods) {
      members.add(renderMethod(method, false));
      if (hasOffsets(method))
        members.add(renderMethod(method, true));
    }

    ST t = jTemplates.getInstanceOf("abstractClass");
    t.add("package", getTargetPackage());
    t.add("name", getTargetClassName());
    t.add("members", members);
    t.add("docs", getGenerationSummaryJavadocs());
    t.add("fallback", fallback);
    t.add("impls", impls);
    if (!Strings.isNullOrEmpty(initCode))
      t.add("initCode", initCode);

    return t.render();
  }

  private String renderMethod(Method method, boolean offsets) throws IOException {
    ST m = jTemplates.getInstanceOf("abstractMethod");
    m.add("return", method.getReturnType());
    if (method.getReturnType().equals(Void.TYPE))
      m.add("returnDocs", "");
    m.add("method", method.getName());
    m.add("paramTypes", getNetlibJavaParameterTypes(method, offsets));
    m.add("paramNames", getNetlibJavaParameterNames(method, offsets));
    if (!Strings.isNullOrEmpty(javadoc))
      m.add("docs", getJavadocs(method));
    return m.render();
  }

  private String getJavadocs(Method method) throws IOException {
    File jar = getFile(javadoc);
    F2jJavadocExtractor extractor = new F2jJavadocExtractor(jar);
    return extractor.getJavadocDescription(method);
  }
}
