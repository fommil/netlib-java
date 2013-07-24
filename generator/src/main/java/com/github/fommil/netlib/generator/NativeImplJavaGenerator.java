package com.github.fommil.netlib.generator;

import com.google.common.collect.Lists;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.stringtemplate.v4.ST;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

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
  protected String library;

  /**
   * The implementation that we are extending (if not specified,
   * {@link UnsupportedOperationException} may be thrown by excluded
   * methods we are implementing).
   *
   * @see #excluded
   */
  @Parameter
  protected String extending;

  /**
   * Method names that we do not support (comma separated).
   *
   * @see #extending
   */
  @Parameter
  protected String excluded;

  @Override
  protected String generate(List<Method> methods) throws Exception {
    Set<String> excludes = newHashSet(excluded != null ? excluded.split(",") : new String[0]);

    List<String> members = Lists.newArrayList();
    for (Method method : methods) {
      ST m = jTemplates.getInstanceOf("nativeImplMethod");
      if (excludes.contains(method.getName())) {
        if (extending == null)
          m = jTemplates.getInstanceOf("unsupportedMethod");
        else
          continue;
      }
      m.add("returns", method.getReturnType());
      m.add("method", method.getName());
      m.add("paramTypes", getNetlibJavaParameterTypes(method));
      m.add("paramNames", getNetlibJavaParameterNames(method));
      members.add(m.render());
    }

    ST t = jTemplates.getInstanceOf("implClass");
    t.add("package", getTargetPackage());
    t.add("name", getTargetClassName());
    t.add("members", members);
    t.add("docs", getGenerationSummaryJavadocs());
    t.add("parent", extending != null ? extending : implementing);

    return t.render();
  }
}
