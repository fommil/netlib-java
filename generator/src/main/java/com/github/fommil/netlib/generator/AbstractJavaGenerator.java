package com.github.fommil.netlib.generator;

import com.google.common.collect.Lists;
import org.stringtemplate.v4.STGroupFile;

import java.lang.reflect.Method;
import java.util.List;

public abstract class AbstractJavaGenerator extends AbstractNetlibGenerator {

    protected final STGroupFile jTemplates = new STGroupFile("netlib-java.stg", '$', '$');

    protected String getTargetPackage() {
        return outputName.replace("/", ".").substring(0, outputName.lastIndexOf("/"));
    }

    protected String getTargetClassName() {
        return outputName.replace(".java", "").substring(outputName.lastIndexOf("/") + 1);
    }

    /**
     * @param method
     * @return a list of relevant parameters for the netlib interface.
     */
    protected List<String> getNetlibJavaParameterNames(Method method) {
        final List<String> params = Lists.newArrayList();
        iterateRelevantParameters(method, new ParameterCallback() {
            @Override
            public void process(int i, Class<?> param, String name) {
                params.add(name);
            }
        });
        return params;
    }

    /**
     * @param method
     * @return a list of relevant canonical parameter types for the netlib interface.
     */
    protected List<String> getNetlibJavaParameterTypes(Method method) {
        final List<String> types = Lists.newArrayList();
        iterateRelevantParameters(method, new ParameterCallback() {
            @Override
            public void process(int i, Class<?> param, String name) {
                types.add(param.getCanonicalName());
            }
        });
        return types;
    }

    /**
     * @param method
     * @return
     */
    protected List<String> getF2jJavaParameters(Method method) {
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

}
