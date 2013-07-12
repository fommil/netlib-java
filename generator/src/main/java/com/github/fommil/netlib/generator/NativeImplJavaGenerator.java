package com.github.fommil.netlib.generator;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Generates the Java implementation of the netlib interface,
 * extending a pure Java implementation (if necessary) but primarily
 * delegating methods to native code (which is loaded as the responsibility
 * of the generated class).
 */
@Mojo(
        name = "native-java",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE
)
public class NativeImplJavaGenerator extends AbstractJavaGenerator {
    @Override
    protected String generate(List<Method> methods) throws Exception {
        throw new UnsupportedOperationException("TODO");
    }
}
