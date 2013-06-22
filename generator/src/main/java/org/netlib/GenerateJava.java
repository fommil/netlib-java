package org.netlib;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

/**
 * @author Sam Halliday
 */
@Mojo(name = "java", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateJava extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException {
        STGroupFile templates = new STGroupFile("netlib-java.stg");

//        File generated = new File(outputDirectory, "testing.java");

        ST t = templates.getInstanceOf("test");

        getLog().info(t.toString());

//        project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
    }
}
