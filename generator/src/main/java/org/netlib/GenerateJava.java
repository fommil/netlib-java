package org.netlib;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

/**
 * @author Sam Halliday
 */
@Mojo(name = "java")
public class GenerateJava extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        STGroupFile templates = new STGroupFile("netlib-java.stg");

//        File generated = new File(outputDirectory, "testing.java");

        ST t = templates.getInstanceOf("test");

        getLog().info(t.toString());

//        project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
    }
}
