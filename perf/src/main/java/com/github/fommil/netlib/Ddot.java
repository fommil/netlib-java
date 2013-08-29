package com.github.fommil.netlib;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import static com.github.fommil.netlib.Benchmarks.randomArray;

/**
 * @author Sam Halliday
 */
public class Ddot implements Benchmark.Parameterised {

  @Override
  public long benchmark(int size) {
    Stopwatch stopwatch = new Stopwatch();

    double[] array1 = randomArray(size);
    double[] array2 = randomArray(size);
    stopwatch.start();
    BLAS.getInstance().ddot(size, array1, 1, array2, 1);
    stopwatch.stop();

    return stopwatch.elapsed(TimeUnit.NANOSECONDS);
  }

}
