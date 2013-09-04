package com.github.fommil.netlib;

import com.google.common.base.Stopwatch;
import lombok.extern.java.Log;
import org.netlib.util.intW;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author Sam Halliday
 */
@Log
public class Dgetri implements Benchmark.Parameterised {
  @Override
  public long benchmark(int size) {
    int m = (int) Math.sqrt(size);

    // random matrices are full rank (and can always be inverted if square)
    // http://www.sciencedirect.com/science/article/pii/S0096300306009040
    double[] a = Benchmarks.randomArray(m * m);
    double[] aOrig = Arrays.copyOf(a, a.length);
    double[] b = new double[1];
    int[] p = new int[m];
    intW info = new intW(0);

    Stopwatch watch = new Stopwatch();

    LAPACK.getInstance().dgetri(m, a, m, p, b, -1, info);
    //log.info(m + " supposedly has optimal work of " + b[0]);
    b = new double[(int)b[0]];

    watch.start();
    LAPACK.getInstance().dgetrf(m, m, a, m, p, info);
    if (info.val != 0)
      throw new IllegalArgumentException();
    LAPACK.getInstance().dgetri(m, a, m, p, b, b.length, info);
    if (info.val != 0)
      throw new IllegalArgumentException();
    watch.stop();

    // quick check
    double[] c = new double[m * m];
    BLAS.getInstance().dgemm("N", "N", m, m, m, 1, aOrig, m, a, m, 0, c, m);
    if (!Benchmarks.isUnit(c, m, 0.000001)) {
      Dgetri.log.warning("failed to invert matrix");
    }

    return watch.elapsed(TimeUnit.NANOSECONDS);
  }
}
