/*
 * Copyright (C) 2013 Samuel Halliday
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see [http://www.gnu.org/licenses/].
 */
package com.github.fommil.netlib.generator;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;

/**
 * Scans jar files for methods.
 * <p>
 * <b>SIDE-EFFECT WARNING: discovered classes are loaded</b>
 * (only point at trusted jars, as arbitrary code may be invoked).
 *
 * @author Sam Halliday
 */
@RequiredArgsConstructor
@Log
public class JarMethodScanner {

    @NonNull
    private final File file;

    private URLClassLoader createLoader() {
        try {
            URL url = file.toURI().toURL();
            return new URLClassLoader(new URL[]{url});
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private List<String> getClasses(String packageName) throws IOException {
        @Cleanup JarFile jar = new JarFile(file);
        List<String> matched = newArrayList();
        for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
            String entryName = entries.nextElement().getName();
            if (entryName.startsWith(packageName.replace(".", "/")) && entryName.endsWith(".class"))
                matched.add(entryName.replace(".class", "").replace("/", "."));
        }
        return matched;
    }

    public List<Method> getStaticMethods(String packageName) throws Exception {
        List<Method> methods = newArrayList();
        List<String> classNames = getClasses(packageName);
        URLClassLoader loader = createLoader();

        for (String className : classNames) {
            Class<?> clazz = loader.loadClass(className);
            for (Method method : clazz.getMethods()) {
                int modifiers = method.getModifiers();
                String fqn = method.getDeclaringClass().getCanonicalName();
                if (isStatic(modifiers) && isPublic(modifiers) && fqn.startsWith(packageName))
                    methods.add(method);
            }
        }

        return methods;
    }

}
