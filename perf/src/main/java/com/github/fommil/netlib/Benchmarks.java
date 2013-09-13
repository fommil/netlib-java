package com.github.fommil.netlib;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.Lists;
import lombok.Cleanup;
import lombok.extern.java.Log;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Random;

import static java.lang.System.getProperty;

/**
 * @author Sam Halliday
 */
@Log
public class Benchmarks {

  public static void main(String[] args) throws Exception {
    int reps = Integer.valueOf(getProperty("perf.reps", "10"));
    int sets = Integer.valueOf(getProperty("perf.max", "100"));

    // make sure we're all loaded
    BLAS.getInstance();
    LAPACK.getInstance();
    ARPACK.getInstance();

    List<Benchmark> benchmarks = Lists.newArrayList();
//    benchmarks.add(new Linpack());

    List<Benchmark.Parameterised> pBenchmarks = Lists.newArrayList();
    pBenchmarks.add(new Ddot());
    pBenchmarks.add(new Dgemm());
    pBenchmarks.add(new Dgetri());
    pBenchmarks.add(new Dsaupd());

    for (Benchmark b : benchmarks) {
      File file = new File(getTarget(b) + ".csv");
      log.info("writing to " + file);
      @Cleanup CSVWriter csv = new CSVWriter(new FileWriter(file));
      for (int i = 0; i < sets; i++) {
        long result = b.benchmark();
        csv.writeNext(new String[]{Long.toString(result)});
      }
    }

    double factor = 6 / 100.0;
    for (Benchmark.Parameterised b : pBenchmarks) {
      File file = new File(getTarget(b) + ".csv");
      log.info("writing to " + file);
      @Cleanup CSVWriter csv = new CSVWriter(new FileWriter(file));
      for (int i = 0; i < reps; i++) {
        log.info(file + " rep " + i);
        for (int j = sets; j > 0; j--) {
          int size = (int) Math.pow(10, factor * j);
          if (size < 10) continue;
          try {
            long result = b.benchmark(size);
            csv.writeNext(new String[]{Integer.toString(size), Long.toString(result)});
          } catch (IllegalArgumentException e) {
            log.info("skipping a datum...");
          }
        }
      }
    }
  }

  private static String getTarget(Object o) {
    return (getProperty("os.name") + "-"
        + getProperty("os.arch") + "-"
        + getJvm()
        + o.getClass().getSimpleName() + "-"
        + BLAS.getInstance().getClass().getSimpleName()
    ).toLowerCase().replace(" ", "_");
  }

  private static String getJvm() {
    if (getProperty("jvm.type") == null) return "";
    return getProperty("jvm.type") + "-";
  }

  // return array of size n with normally distributed elements
  public static double[] randomArray(int n) {
    assert n > 0;
    Random random = new Random();
    double[] array = new double[n];
    for (int i = 0; i < n; i++) {
      array[i] = random.nextGaussian();
    }
    return array;
  }

  public static boolean within(double a, double b, double epsilon) {
    return ((a - b) <= epsilon);
  }

  public static boolean isUnit(double[] a, int m, double epsilon) {
    for (int i = 0; i < m; i++) {
      int diag = m * i + i;
      if (!Benchmarks.within(a[diag], 1, epsilon)) {
        log.info("bad value (1) != " + a[diag]);
        return false;
      }
      for (int j = 0; j < m; j++)
        if (j != i && !Benchmarks.within(a[i * m + j], 0, epsilon)) {
          log.info("bad value (0) != " + a[i * m + j]);
          return false;
        }
    }
    return true;
  }
}
