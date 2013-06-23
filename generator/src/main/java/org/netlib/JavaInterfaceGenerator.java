package org.netlib;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.stringtemplate.v4.STGroupFile;

import java.lang.reflect.Method;
import java.util.List;

@Mojo(
        name = "interface",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE
)
public class JavaInterfaceGenerator extends AbstractNetlibGenerator {

    private final STGroupFile templates = new STGroupFile("netlib-java.stg", '$', '$');

    @Override
    protected String generate(List<Method> methods) throws Exception {
        throw new UnsupportedOperationException();
    }
}
