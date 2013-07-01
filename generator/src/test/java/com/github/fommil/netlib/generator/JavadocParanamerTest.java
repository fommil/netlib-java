package com.github.fommil.netlib.generator;

/*
 * Copyright 2007 Paul Hammant
 * Copyright 2013 Samuel Halliday
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.thoughtworks.paranamer.Paranamer;
import junit.framework.TestCase;
import org.netlib.blas.Dasum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

/**
 * @author Samuel Halliday
 */
public class JavadocParanamerTest extends TestCase {

    private static final String JAVADOCS_3 = "http://docs.oracle.com/javase/1.3/docs/api/";
    private static final String JAVADOCS_4 = "http://docs.oracle.com/javase/1.4.2/docs/api/";
    private static final String JAVADOCS_5 = "http://docs.oracle.com/javase/1.5.0/docs/api/";
    private static final String JAVADOCS_6 = "http://docs.oracle.com/javase/6/docs/api/";
    private static final String JAVADOCS_7 = "http://docs.oracle.com/javase/7/docs/api/";

    private static final String JAVADOCS_F2J = "http://icl.cs.utk.edu/projectsfiles/f2j/javadoc/";

    public void testFailsIfBadInput() throws IOException {
        try {
            new JavadocParanamer(new URL(JAVADOCS_7 + "/DOES_NOT_EXIST"));
            fail("should have barfed");
        } catch (FileNotFoundException e) {
            // expected
        }
    }

    public void testFailsIfNotAFile() throws IOException {
        try {
            new JavadocParanamer(new File("DOES_NOT_EXIST"));
            fail("should have barfed");
        } catch (FileNotFoundException e) {
            // expected
        }
    }

    public void testFailsIfNotAJavadocDirectory() throws IOException {
        try {
            new JavadocParanamer(new File("./"));
            fail("should have barfed");
        } catch (FileNotFoundException e) {
            assertTrue(e.getMessage().contains("package-list"));
        }
    }

    public void testJavadocs3() throws Exception {
        testJavaUtilSample(new JavadocParanamer(new URL(JAVADOCS_3)));
    }

    public void testJavadocs4() throws Exception {
        testJavaUtilSample(new JavadocParanamer(new URL(JAVADOCS_4)));
    }

    public void testJavadocs5() throws Exception {
        Paranamer p = new JavadocParanamer(new URL(JAVADOCS_5));
        testJavaUtilSample(p);
        testJavaUtilGenericsSample(p);
    }

    public void testJavadocs6() throws Exception {
        Paranamer p = new JavadocParanamer(new URL(JAVADOCS_6));
        testJavaUtilSample(p);
        testJavaUtilGenericsSample(p);
    }

    public void testJavadocs7() throws Exception {
        Paranamer p = new JavadocParanamer(new URL(JAVADOCS_7));
        testJavaUtilSample(p);
        testJavaUtilGenericsSample(p);
    }

    public void testF2J() throws Exception {
        Paranamer p = new JavadocParanamer(new URL(JAVADOCS_F2J));
        testAccessible(p, Dasum.class.getMethod("dasum",
                Integer.TYPE, double[].class, Integer.TYPE, Integer.TYPE),
                "n", "dx", "_dx_offset", "incx");
    }


    private void testJavaUtilSample(Paranamer p) throws Exception {
        // normal methods, collision in name
        testAccessible(p, Random.class.getMethod("nextInt", Integer.TYPE), "n");
        testAccessible(p, Random.class.getMethod("nextInt"));

        // static
        testAccessible(p, System.class.getMethod("getProperty", String.class, String.class), "key", "def");

        // TODO constructor support
//        testAccessible(p, String.class.getConstructor(char[].class), "value");
    }

    private void testJavaUtilGenericsSample(Paranamer p) throws Exception {
        testAccessible(p, Collection.class.getMethod("containsAll", Collection.class), "c");
    }

    private void testAccessible(Paranamer p, AccessibleObject accessible, String... expected) {
        String[] names = p.lookupParameterNames(accessible);
        assertTrue(accessible + " " + Arrays.toString(names), Arrays.equals(expected, names));
    }

}
