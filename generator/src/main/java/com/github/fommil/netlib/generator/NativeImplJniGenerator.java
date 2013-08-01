package com.github.fommil.netlib.generator;

import com.google.common.collect.Lists;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

@Mojo(
    name = "native-jni",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE
)
public class NativeImplJniGenerator extends AbstractNetlibGenerator {

  protected final STGroupFile jniTemplates = new STGroupFile("com/github/fommil/netlib/generator/netlib-jni.stg", '$', '$');

  /**
   * C Header files to include
   */
  @Parameter
  protected List<String> includes;

  @Override
  protected String generate(List<Method> methods) throws Exception {
    ST t = jniTemplates.getInstanceOf("jni");

    if (includes != null)
      t.add("includes", includes);

    List<String> members = Lists.newArrayList();
    for (Method method : methods) {
      ST f = jniTemplates.getInstanceOf("function");
      f.add("returns", "void");
      f.add("fqn", (method.getDeclaringClass().getCanonicalName() + "." + method.getName()).replace(".", "_"));
      List<String> params = getNetlibCParameterTypes(method);
      List<String> names = getNetlibJavaParameterNames(method);
      f.add("paramTypes", params);
      f.add("paramNames", names);
      f.add("return", "");

      List<String> init = Lists.newArrayList();
      List<String> clean = Lists.newArrayList();

      for (int i = 0; i < params.size(); i++) {
        String param = params.get(i);
        String name = names.get(i);
        ST before = jniTemplates.getInstanceOf(param + "_init");
        if (before != null) {
          before.add("name", name);
          init.add(before.render());
        }

        ST after = jniTemplates.getInstanceOf(param + "_clean");
        if (after != null) {
          after.add("name", name);
          clean.add(after.render());
        }
      }
      Collections.reverse(clean);

      f.add("init", init);
      f.add("clean", clean);
      members.add(f.render());
    }


    t.add("members", members);

    return t.render();
  }

  private List<String> getNetlibCParameterTypes(Method method) {
    final List<String> types = Lists.newArrayList();
    iterateRelevantParameters(method, new ParameterCallback() {
      @Override
      public void process(int i, Class<?> param, String name) {
        if (param.isArray())
          types.add("j" + param.getComponentType().getSimpleName() + "Array");
        else
          types.add("j" + param.getSimpleName().toLowerCase());
      }
    });
    return types;
  }

}
