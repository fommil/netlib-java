package com.github.fommil.jni;

import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;

import static java.io.File.createTempFile;
import static java.nio.channels.Channels.newChannel;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@Log
public final class JniLoader {

  public static final String JNI_EXTRACT_DIR_PROP = "com.github.fommil.netlib.jni.dir";

  static private boolean loaded;

  /**
   * Attempts to load a native library from the {@code java.library.path}
   * and (if that fails) will extract the named file from the classpath
   * into a pre-defined directory ({@value #JNI_EXTRACT_DIR_PROP}, or - if not
   * defined - a temporary directory is created) and load from there.
   * <p/>
   * Will stop on the first successful load of a native library.
   *
   * @param paths alternative relative path of the native library
   *              on either the library path or classpath.
   * @throws ExceptionInInitializerError if the input parameters are invalid or
   *                                     all paths failed to load (making this
   *                                     safe to use in static code blocks).
   */
  public synchronized static void load(String... paths) {
    if (paths == null || paths.length == 0)
      throw new ExceptionInInitializerError("invalid parameters");

    for (String path : paths) {
      if (loaded) return;
      liberalLoad(path);
      if (loaded) return;
      File extracted = extract(path);
      if (extracted != null)
        liberalLoad(extracted.getAbsolutePath());
      if (loaded) return;
    }

    if (!loaded)
      throw new ExceptionInInitializerError("unable to load from " + Arrays.toString(paths));
  }

  private static void liberalLoad(String path) {
    try {
      log.info("loading " + path);
      System.load(path);
      log.info("successfully loaded " + path);
      loaded = true;
    } catch (Throwable e) {
      if (e instanceof SecurityException || e instanceof UnsatisfiedLinkError)
        log.log(INFO, "skipping direct load of " + path, e);
      else throw new ExceptionInInitializerError(e);
    }
  }

  private static File extract(String path) {
    try {
      URL url = JniLoader.class.getResource(path);
      if (url == null) return null;

      @Cleanup InputStream in = JniLoader.class.getResourceAsStream("/" + path);
      File file = file(path);
      deleteOnExit(file);

      log.info("extracting " + url + " to " + file.getAbsoluteFile());

      ReadableByteChannel src = newChannel(in);
      @Cleanup FileChannel dest = new FileOutputStream(file).getChannel();
      dest.transferFrom(src, 0, Long.MAX_VALUE);

      return file;
    } catch (Throwable e) {
      if (e instanceof SecurityException || e instanceof IOException) {
        log.log(INFO, "skipping direct load of " + path, e);
        return null;
      } else throw new ExceptionInInitializerError(e);
    }
  }

  private static File file(String path) throws IOException {
    String name = new File(path).getName();

    String dir = System.getProperty(JNI_EXTRACT_DIR_PROP);
    if (dir != null) {
      File file = new File(dir, name);
      if (file.exists() && !file.isFile())
        throw new IllegalArgumentException(file.getAbsolutePath() + " is not a file.");
      if (!file.exists()) file.createNewFile();
      return file;
    }
    return createTempFile("", name);
  }

  /**
   * Sadly, a wrapper for the File method of this name. Swallows security exceptions, which
   * can erroneously appear (on OS X at least) despite the policy file saying otherwise and which
   * are probably not fatal at any rate.
   *
   * @param file
   * @see <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6997203">Java Bug 6997203</a>
   */
  @SuppressWarnings("CallToThreadYield")
  private static void deleteOnExit(File file) {
    try {
      file.deleteOnExit();
    } catch (Exception e1) {
      log.log(INFO, file.getAbsolutePath() + " delete denied, retrying - might be Java bug #6997203.");
      try {
        System.gc();
        Thread.yield();
        file.deleteOnExit();
      } catch (Exception e2) {
        log.log(WARNING, file.getAbsolutePath() + " delete denied a second time.", e2);
      }
    }
  }
}