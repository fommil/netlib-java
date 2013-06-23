package org.netlib;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author Sam Halliday
 */
@Mojo(
        name = "java",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE
)
public class GenerateJava extends AbstractMojo {

    /** Location of the generated source files. */
    @Parameter(property = "netlib.outputDir", defaultValue = "${project.build.directory}/generated-sources/netlib", required = true)
    private File outputDir;

    /** The jar to generate from. */
    @Parameter(property = "netlib.artifact", defaultValue = "net.sourceforge.f2j:arpack_combined_all", required = true)

    @Component
    protected MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        try {
//            STGroupFile templates = new STGroupFile("netlib-java.stg");
//            ST t = templates.getInstanceOf("test");
//            getLog().info(t.render());

            // TODO: http://downloads.sourceforge.net/project/f2j/f2j/jlapack-0.8/jlapack-0.8-javadoc.zip?r=&ts=1371940324&use_mirror=heanet

            getLog().info(project.getArtifactMap().keySet().toString());

            for (Artifact artifact : project.getArtifacts()) {

                getLog().info(artifact.getFile().getAbsolutePath());
            }

//            // create the BLAS wrapper
//            JavaGenerator blas =
//                    new JavaGenerator("org.netlib.blas", "BLAS",
//                            "lib/f2j/jlapack-0.8-javadoc.zip");
//            writeToFile(blas.getAbstractWrapper(), "src/org/netlib/blas/BLAS.java");
//            writeToFile(blas.getJavaWrapper(), "src/org/netlib/blas/JBLAS.java");
//            writeToFile(blas.getJNIWrapper(), "src/org/netlib/blas/NativeBLAS.java");
//            writeToFile(blas.getJNIC(), "jni/org_netlib_blas_NativeBLAS.c");
//
//            // create the LAPACK wrapper
//            JavaGenerator lapack =
//                    new JavaGenerator("org.netlib.lapack", "LAPACK",
//                            "lib/f2j/jlapack-0.8-javadoc.zip");
//            writeToFile(lapack.getAbstractWrapper(),
//                    "src/org/netlib/lapack/LAPACK.java");
//            writeToFile(lapack.getJavaWrapper(),
//                    "src/org/netlib/lapack/JLAPACK.java");
//            writeToFile(lapack.getJNIWrapper(),
//                    "src/org/netlib/lapack/NativeLAPACK.java");
//            writeToFile(lapack.getJNIC(), "jni/org_netlib_lapack_NativeLAPACK.c");
//
//            // create the ARPACK wrapper
//            // TODO: add the ARPACK javadocs here
//            JavaGenerator arpack =
//                    new JavaGenerator("org.netlib.arpack", "ARPACK", "");
//            writeToFile(arpack.getAbstractWrapper(),
//                    "src/org/netlib/arpack/ARPACK.java");
//            writeToFile(arpack.getJavaWrapper(),
//                    "src/org/netlib/arpack/JARPACK.java");
//            writeToFile(arpack.getJNIWrapper(),
//                    "src/org/netlib/arpack/NativeARPACK.java");
//            writeToFile(arpack.getJNIC(), "jni/org_netlib_arpack_NativeARPACK.c");


            project.addCompileSourceRoot(outputDir.getAbsolutePath());
        } catch (Exception e) {
            throw new MojoExecutionException("java generation", e);
        }
    }

    static void writeToFile(String string, String filename) throws IOException {
        FileOutputStream out = new FileOutputStream(filename);
        OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
        writer.write(string);
        writer.close();
    }
}
