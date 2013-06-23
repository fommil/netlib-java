package org.netlib;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.stringtemplate.v4.STGroupFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author Sam Halliday
 */
@Mojo(name = "java", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateJava extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException {
        try {
            STGroupFile templates = new STGroupFile("netlib-java.stg");

//            ST t = templates.getInstanceOf("test");
//            getLog().info(t.render());

            // TODO: http://downloads.sourceforge.net/project/f2j/f2j/jlapack-0.8/jlapack-0.8-javadoc.zip?r=&ts=1371940324&use_mirror=heanet


            // create the BLAS wrapper
            JavaGenerator blas =
                    new JavaGenerator("org.netlib.blas", "BLAS",
                            "lib/f2j/jlapack-0.8-javadoc.zip");
            writeToFile(blas.getAbstractWrapper(), "src/org/netlib/blas/BLAS.java");
            writeToFile(blas.getJavaWrapper(), "src/org/netlib/blas/JBLAS.java");
            writeToFile(blas.getJNIWrapper(), "src/org/netlib/blas/NativeBLAS.java");
            writeToFile(blas.getJNIC(), "jni/org_netlib_blas_NativeBLAS.c");

            // create the LAPACK wrapper
            JavaGenerator lapack =
                    new JavaGenerator("org.netlib.lapack", "LAPACK",
                            "lib/f2j/jlapack-0.8-javadoc.zip");
            writeToFile(lapack.getAbstractWrapper(),
                    "src/org/netlib/lapack/LAPACK.java");
            writeToFile(lapack.getJavaWrapper(),
                    "src/org/netlib/lapack/JLAPACK.java");
            writeToFile(lapack.getJNIWrapper(),
                    "src/org/netlib/lapack/NativeLAPACK.java");
            writeToFile(lapack.getJNIC(), "jni/org_netlib_lapack_NativeLAPACK.c");

            // create the ARPACK wrapper
            // TODO: add the ARPACK javadocs here
            JavaGenerator arpack =
                    new JavaGenerator("org.netlib.arpack", "ARPACK", "");
            writeToFile(arpack.getAbstractWrapper(),
                    "src/org/netlib/arpack/ARPACK.java");
            writeToFile(arpack.getJavaWrapper(),
                    "src/org/netlib/arpack/JARPACK.java");
            writeToFile(arpack.getJNIWrapper(),
                    "src/org/netlib/arpack/NativeARPACK.java");
            writeToFile(arpack.getJNIC(), "jni/org_netlib_arpack_NativeARPACK.c");



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
