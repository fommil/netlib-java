package com.github.fommil.netlib.generator;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.lang.reflect.Method;
import java.util.List;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;

@Mojo(
    name = "native-jni",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE
)
public class NativeImplJniGenerator extends AbstractNetlibGenerator {

  protected final STGroupFile jniTemplates = new STGroupFile("com/github/fommil/netlib/generator/netlib-jni.stg", '$', '$');

  @Override
  protected String generate(List<Method> methods) throws Exception {
    ST t = jniTemplates.getInstanceOf("jni");

    t.add("includes", newArrayList("<cblas.h>"));

    List<String> members = Lists.newArrayList();
    for (Method method : methods) {
      ST f = jniTemplates.getInstanceOf("function");
      f.add("returns", "void");
      f.add("fqn", (method.getDeclaringClass().getCanonicalName() + "." + method.getName()).replace(".", "_"));
      f.add("paramTypes", getNetlibCParameterTypes(method));
      f.add("paramNames", getNetlibJavaParameterNames(method));
      f.add("return", "");
      members.add(f.render());
    }

    t.add("members", members);

    return t.render();
  }

  private List<String> getNetlibCParameterTypes(Method method) {
    return newArrayList(transform(getNetlibJavaParameterTypes(method), new Function<String, String>() {
      @Override
      public String apply(String input) {
        return input;
      }
    }));
  }

}
