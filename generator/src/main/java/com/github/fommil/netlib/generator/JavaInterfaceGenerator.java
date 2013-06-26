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
     * The default implementation.
     */
    @Parameter(required = true)
    protected String fallback;

    /**
     * Arbitrary Java code that is run after instance creation.
     */
    @Parameter
    protected String initCode;

    @Override
    protected String generate(List<Method> methods) throws Exception {
        List<String> members = Lists.newArrayList();
        for (Method method : methods) {
            ST m = jTemplates.getInstanceOf("abstractMethod");
            m.add("return", method.getReturnType());
            if (method.getReturnType().equals(Void.TYPE))
                m.add("returnDocs", "");
            m.add("method", method.getName());
            m.add("paramTypes", getNetlibJavaParameterTypes(method));
            m.add("paramNames", getNetlibJavaParameterNames(method));
            if (!Strings.isNullOrEmpty(javadoc))
                m.add("docs", getJavadocs(method));
            members.add(m.render());
        }

        ST t = jTemplates.getInstanceOf("abstractClass");
        t.add("package", getTargetPackage());
        t.add("name", getTargetClassName());
        t.add("members", members);
        t.add("docs", getGenerationSummaryJavadocs());
        t.add("fallback", fallback);
        if (!Strings.isNullOrEmpty(initCode))
            t.add("initCode", initCode);

        return t.render();
    }

    private String getJavadocs(Method method) throws IOException {
        File jar = getFile(javadoc);
        F2jJavadocExtractor extractor = new F2jJavadocExtractor(jar);
        return extractor.getJavadocDescription(method);
    }
}
