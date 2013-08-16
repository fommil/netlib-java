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
 * Generates the Java part of a JNI implementation of a netlib interface.
 */
@Mojo(
    name = "native-java",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE
)
public class NativeImplJavaGenerator extends AbstractJavaGenerator {

  /**
   * The interface that we are implementing.
   */
  @Parameter(required = true)
  protected String implementing;

  /**
   * The name of the native library.
   */
  @Parameter(required = true)
  protected String[] natives;

  /**
   * The implementation that we are extending (if not specified,
   * {@link UnsupportedOperationException} may be thrown by excluded
   * methods we are implementing).
   *
   * @see #unsupported
   */
  @Parameter
  protected String extending;

  /**
   * Methods we don't support.
   */
  @Parameter
  protected String unsupported;

  @Override
  protected String generate(List<Method> methods) throws Exception {
    List<String> members = Lists.newArrayList();

    ST loader = jTemplates.getInstanceOf("staticJniLoader");
    loader.add("libs", natives);
    members.add(loader.render());

    for (Method method : methods) {
      ST m = getTemplate(method, false);
      if (m == null) continue;
      members.add(render(m, method, false));
      if (hasOffsets(method))
        members.add(render(getTemplate(method, true), method, true));
    }

    ST t = jTemplates.getInstanceOf("implClass");
    t.add("package", getTargetPackage());
    t.add("name", getTargetClassName());
    t.add("members", members);
    t.add("docs", getGenerationSummaryJavadocs());
    t.add("parent", extending != null ? extending : implementing);

    return t.render();
  }

  private ST getTemplate(Method method, boolean offsets) {
    ST m = jTemplates.getInstanceOf("nativeImplMethod" + (offsets ? "_offsets" : ""));
    if (unsupported != null && method.getName().matches(unsupported)) {
      if (extending == null)
        m = jTemplates.getInstanceOf("unsupportedMethod");
      else
        return null;
    }
    return m;
  }

  private String render(ST m, Method method, boolean offsets) {
    m.add("returns", method.getReturnType());
    if (offsets && method.getReturnType() == Void.TYPE)
      m.add("return", "");
    m.add("method", method.getName());
    m.add("paramTypes", getNetlibJavaParameterTypes(method, offsets));
    m.add("paramNames", getNetlibJavaParameterNames(method, offsets));
    return m.render();
  }
}
