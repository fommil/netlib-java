package org.netlib;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

public abstract class AbstractNetlibGenerator extends AbstractMojo {

    /**
     * Location of the generated source files.
     */
    @Parameter(property = "netlib.outputDir", defaultValue = "${project.build.directory}/generated-sources/netlib", required = true)
    protected File outputDir;

    @Parameter(property = "netlib.outputName", defaultValue = "org/netlib/Blas.java", required = true)
    protected String outputName;

    /**
     * The jar to generate from.
     */
    @Parameter(property = "netlib.jar", defaultValue = "net.sourceforge.f2j:arpack_combined_all", required = true)
    protected String netlib_jar_artifact;

    /**
     * The javadocs to use to extract parameter names.
     */
    @Parameter(property = "netlib.javadoc", defaultValue = "net.sourceforge.f2j:jlapack")
    protected String netlib_javadoc_artifact;

    /**
     * The package to scan.
     */
    @Parameter(property = "netlib.package", defaultValue = "org.netlib.blas", required = true)
    protected String netlib_package;

    @Component
    protected MavenProject project;

    protected File getFile(String artifactName) {
        Artifact artifact = project.getArtifactMap().get(artifactName);
        if (artifact == null)
            throw new IllegalArgumentException("could not find artifact " + artifactName + " from " + project.getArtifactMap().keySet());
        File file = artifact.getFile();
        if (file == null)
            throw new IllegalArgumentException("could not find file for " + artifact);
        return file;
    }

    /**
     * Implementation specific interpretation of the parameters.
     *
     * @param methods obtained from a scan of F2J public static methods.
     * @return the file contents for the generated file associated to the parameters.
     * @throws Exception
     */
    abstract protected String generate(List<Method> methods) throws Exception;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            File jar = getFile(netlib_jar_artifact);
            JarMethodScanner scanner = new JarMethodScanner(jar);

            List<Method> methods = scanner.getStaticMethods(netlib_package);
            String generated = generate(methods);

            File output = new File(outputDir, outputName);
            output.getParentFile().mkdirs();

            getLog().info("Generating " + output.getAbsoluteFile());
            Files.write(generated, output, Charsets.UTF_8);

            project.addCompileSourceRoot(outputDir.getAbsolutePath());
        } catch (Exception e) {
            throw new MojoExecutionException("java generation", e);
        }
    }

}
