package com.github.fommil.netlib;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * @author Sam Halliday
 */
public class Dgemm implements Benchmark.Parameterised {
  @Override
  public long benchmark(int size) {
    int m = (int) Math.sqrt(size);

    double[] a = Benchmarks.randomArray(m * m);
    double[] b = Benchmarks.randomArray(m * m);
    double[] c = new double[m * m];

    Stopwatch watch = new Stopwatch();
    watch.start();
    BLAS.getInstance().dgemm("N", "N", m, m, m, 1, a, m, b, m, 0, c, m);
    watch.stop();

    return watch.elapsed(TimeUnit.NANOSECONDS);
  }
}
