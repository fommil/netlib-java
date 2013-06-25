package com.github.fommil.netlib.generator;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.thoughtworks.paranamer.DefaultParanamer;
import com.thoughtworks.paranamer.JavadocParanamer;
import com.thoughtworks.paranamer.Paranamer;
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
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/netlib-java", required = true)
    protected File outputDir;

    @Parameter(required = true)
    protected String outputName;

    /**
     * The artifact of the jar to generate from.
     * Note that this must be listed as a <code>dependency</code>
     * section of the calling module, not a plugin <code>dependency</code>.
     */
    @Parameter(defaultValue = "net.sourceforge.f2j:arpack_combined_all", required = true)
    protected String input;

    /**
     * The artifact of the javadocs to extract parameter names.
     * Note that this must be listed as a <code>dependency</code>
     * section of the calling module, not a plugin <code>dependency</code>.
     */
    @Parameter
    protected String javadoc;

    /**
     * The package to scan.
     */
    @Parameter(required = true)
    protected String scan;

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

    protected Paranamer paranamer = new DefaultParanamer();

    public void execute() throws MojoExecutionException {
        try {
            if (!Strings.isNullOrEmpty(javadoc))
                paranamer = new JavadocParanamer(getFile(javadoc));

            File jar = getFile(input);
            JarMethodScanner scanner = new JarMethodScanner(jar);

            List<Method> methods = scanner.getStaticMethods(scan);
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

    protected interface ParameterCallback {
        void process(int i, Class<?> param, String name);
    }

    /**
     * Calls the callback with every parameter of the method, skipping out the offset parameter
     * introduced by F2J for array arguments.
     *
     * @param method
     * @param callback
     */
    protected void iterateRelevantParameters(Method method, ParameterCallback callback) {
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

}
