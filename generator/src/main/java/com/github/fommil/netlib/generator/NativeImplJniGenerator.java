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

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Mojo(
    name = "native-jni",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE
)
public class NativeImplJniGenerator extends AbstractNetlibGenerator {

  protected final STGroupFile jniTemplates = new STGroupFile("com/github/fommil/netlib/generator/netlib-jni.stg", '$', '$');

  /**
   * The interface that we are implementing.
   */
  @Parameter(required = true)
  protected String implementing;

  /**
   * C Header files to include
   */
  @Parameter
  protected List<String> includes;

  /**
   * Prepended to the native function name.
   */
  @Parameter
  protected String prefix = "";

  /**
   * Suffixed to the native function name.
   */
  @Parameter
  protected String suffix = "";

  /**
   * Prepended to the native function parameter list.
   */
  @Parameter
  protected String firstParam;

  @Parameter
  protected String noFirstParam;

  @Parameter
  protected boolean cblas_hack;

  @Parameter
  protected boolean lapacke_hack;

  @Parameter
  protected boolean fortran_hack;

  @Parameter
  protected boolean extractChar;

  @Override
  protected String generate(List<Method> methods) throws Exception {
    ST t = jniTemplates.getInstanceOf("jni");

    if (includes == null)
      includes = Lists.newArrayList();

    includes.add(outputName.replace(".c", ".h"));
    t.add("includes", includes);

    List<String> members = Lists.newArrayList();
    for (Method method : methods) {
      members.add(render(method, false));
      if (hasOffsets(method))
        members.add(render(method, true));
    }

    t.add("members", members);

    return t.render();
  }

  private String render(Method method, boolean offsets) {
    ST f = jniTemplates.getInstanceOf("function");
    f.add("returns", jType2C(method.getReturnType()));
    f.add("fqn", (implementing + "." + method.getName()).replace(".", "_") + (offsets ? "_1offsets" : ""));
    f.add("name", prefix + method.getName() + suffix);
    List<String> params = getNetlibCParameterTypes(method, offsets);
    List<String> names = getNetlibJavaParameterNames(method, offsets);
    f.add("paramTypes", params);
    f.add("paramNames", names);
    f.add("params", getCMethodParams(method, offsets));

    if (method.getReturnType() == Void.TYPE) {
      if (lapacke_hack && Iterables.getLast(names).equals("info")) {
        f.add("assignReturn", "int returnValue = ");
      } else {
        f.add("assignReturn", "");
      }
      f.add("return", "");
    } else {
      f.add("assignReturn", jType2C(method.getReturnType()) + " returnValue = ");
      f.add("return", "return returnValue;");
    }

    List<String> init = Lists.newArrayList();
    List<String> clean = Lists.newArrayList();

    for (int i = 0; i < params.size(); i++) {
      String param = params.get(i);
      String name = names.get(i);
      ST before = jniTemplates.getInstanceOf(param + "_init");
      if (lapacke_hack && name.equals("info"))
        before = jniTemplates.getInstanceOf(param + "_info_init");
      if (before != null) {
        before.add("name", name);
        init.add(before.render());
      }

      ST after = jniTemplates.getInstanceOf(param + "_clean");
      if (lapacke_hack && name.equals("info"))
        after = jniTemplates.getInstanceOf(param + "_info_clean");
      if (after != null) {
        after.add("name", name);
        clean.add(after.render());
      }
    }
    Collections.reverse(clean);

    f.add("init", init);
    f.add("clean", clean);
    return f.render();
  }

  private List<String> getNetlibCParameterTypes(Method method, boolean offsets) {
    final List<String> types = Lists.newArrayList();
    iterateRelevantParameters(method, offsets, new ParameterCallback() {
      @Override
      public void process(int i, Class<?> param, String name, String offsetName) {
        types.add(jType2C(param));
      }
    });
    return types;
  }

  private String jType2C(Class param) {
    if (param == Void.TYPE)
      return "void";
    if (param.isArray())
      return "j" + param.getComponentType().getSimpleName() + "Array";
    return "j" + param.getSimpleName().toLowerCase();
  }

  private List<String> getCMethodParams(final Method method, final boolean offsets) {
    final LinkedList<String> params = Lists.newLinkedList();
    if (firstParam != null && !method.getName().matches(noFirstParam)) {
      params.add(firstParam);
    }

    iterateRelevantParameters(method, false, new ParameterCallback() {
      @Override
      public void process(int i, Class<?> param, String name, String offsetName) {
        if (lapacke_hack && name.equals("info"))
          return;

        if (param == Object.class)
          throw new UnsupportedOperationException(method + " " + param + " " + name);

        if (param == Boolean.TYPE || !param.isPrimitive()) {
          name = "jni_" + name;
          // NOTE: direct comparisons against StringW.class don't work as expected
          if (!param.getSimpleName().equals("StringW") && param.getSimpleName().endsWith("W")) {
            name = "&" + name;
          }
        }

        if (param == String.class) {
          if (cblas_hack) {
            if (name.contains("trans"))
              name = "getCblasTrans(" + name + ")";
            else if (name.contains("uplo"))
              name = "getCblasUpLo(" + name + ")";
            else if (name.contains("side"))
              name = "getCblasSide(" + name + ")";
            else if (name.contains("diag"))
              name = "getCblasDiag(" + name + ")";
          }
        }

        if (!fortran_hack && param == String.class && extractChar)
          name += "[0]";

        if (fortran_hack && param.isPrimitive())
          name = "&" + name;

        if (offsets && offsetName != null) {
          name = name + " + " + offsetName;
        }

        params.add(name);
      }
    });

    return params;
  }
}
