package org.netlib;

import com.google.common.collect.Lists;
import com.thoughtworks.paranamer.DefaultParanamer;
import com.thoughtworks.paranamer.Paranamer;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Mojo(
        name = "f2j",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE
)
public class F2jImplGenerator extends AbstractNetlibGenerator {

    private final STGroupFile templates = new STGroupFile("netlib-java.stg", '$', '$');

    private Paranamer paranamer = new DefaultParanamer();

    @Override
    protected String generate(List<Method> methods) throws Exception {
        String targetPackage = outputName.replace("/", ".").substring(0, outputName.lastIndexOf("/"));
        String targetClassname = outputName.replace(".java", "").substring(outputName.lastIndexOf("/") + 1);

        if (netlib_javadoc_artifact != null)
            paranamer = new JavadocParanamer(getFile(netlib_javadoc_artifact));

        List<String> members = Lists.newArrayList();
        for (Method method : methods) {
            ST m = templates.getInstanceOf("f2jImplMethod");
            m.add("return", method.getReturnType());
            m.add("method", method.getName());
            m.add("params", getJavaParameters(method));
            m.add("impl", method.getDeclaringClass().getCanonicalName() + "." + method.getName());
            m.add("calls", getJavaJ2jArguments(method));
            members.add(m.render());
        }

        ST t = templates.getInstanceOf("class");
        t.add("package", targetPackage);
        t.add("name", targetClassname);
        t.add("members", members);

        return t.render();
    }

    // FIXME move much of this into an AbstractJavaGenerator

    // returns a list of the text parameters, including their FQN type and name (if available)
    private List<String> getJavaParameters(Method method) {
        final List<String> params = Lists.newArrayList();
        iterateRelevantParameters(method, new ParameterCallback() {
            @Override
            public void process(int i, Class<?> param, String name) {
                params.add(param.getSimpleName() + " " + name);
            }
        });
        return params;
    }

    private List<String> getJavaJ2jArguments(Method method) {
        final List<String> args = Lists.newArrayList();
        iterateRelevantParameters(method, new ParameterCallback() {
            @Override
            public void process(int i, Class<?> param, String name) {
                args.add(name);
                if (param.isArray()) args.add("0");
            }
        });
        return args;
    }

    protected interface ParameterCallback {
        void process(int i, Class<?> param, String name);
    }

    // skips out the offset parameter introduced by F2J for array arguments
    private void iterateRelevantParameters(Method method, ParameterCallback callback) {
        String[] names = paranamer.lookupParameterNames(method, false);
        Class <?> last = null;
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Class<?> param = method.getParameterTypes()[i];
            if (last != null && last.isArray() && param.equals(Integer.TYPE)) {
                last = param;
                continue;
            }
            last = param;
            String name = "arg" + i;
            if (names.length > 0)
                name = names[i];

            callback.process(i, param, name);
        }
    }

    public static void main(String[] args) throws Exception {
        File file = new File("/Users/samuel/.m2/repository/net/sourceforge/f2j/jlapack/0.8/jlapack-0.8-javadoc.jar");

        Paranamer paranamer = new JavadocParanamer(file);

        for (Method method : org.netlib.blas.Dasum.class.getMethods())
            System.out.println(Arrays.toString(paranamer.lookupParameterNames(method)));
    }

}
