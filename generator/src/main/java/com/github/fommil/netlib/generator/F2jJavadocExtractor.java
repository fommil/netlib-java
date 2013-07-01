package com.github.fommil.netlib.generator;

import com.google.common.io.CharStreams;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.lang.String.format;

/**
 * Extracts F2J Javadoc comments.
 *
 * @author Sam Halliday
 */
@RequiredArgsConstructor
public class F2jJavadocExtractor {

    private final File jar;

    private String getRawJavadoc(Method method) throws IOException {
        String filename = method.getDeclaringClass().getCanonicalName().replace(".", "/") + ".html";

        @Cleanup ZipFile zip = new ZipFile(jar);
        Enumeration<? extends ZipEntry> en = zip.entries();
        while (en.hasMoreElements()) {
            ZipEntry entry = en.nextElement();
            if (entry.getName().endsWith(filename)) {
                @Cleanup InputStreamReader stream = new InputStreamReader(zip.getInputStream(entry), "UTF-8");
                return CharStreams.toString(stream);
            }
        }
        return "";
    }

    public String getJavadocDescription(Method method) throws IOException {
        Pattern pattern = Pattern.compile("seymour@cs.utk.edu</a> with any questions.\n<p>");
        String javadoc = getRawJavadoc(method);
        Matcher matcher = pattern.matcher(javadoc);
        boolean matched = matcher.find();
        if (!matched) return format(
                "<i>{@code %s} could not find docs for {@code %s} in {@code %s}</i>.",
                getClass().getSimpleName(), method, jar.getName()
        );

        int start = matcher.end();
        int end = javadoc.indexOf("</pre>", start);
        javadoc = javadoc.substring(start, end).replaceAll("\n\\s*c", "\n").replace("\n\n", "\n");
        return "<pre><code>" + javadoc + " * </code></pre>";
    }

}
