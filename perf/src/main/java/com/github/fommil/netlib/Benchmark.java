package com.github.fommil.netlib;

/**
 * @author Sam Halliday
 */
public interface Benchmark {

  // returns nanoseconds spent in computations
  long benchmark();

  public interface Parameterised {
    // size is a parameter > 0
    long benchmark(int size);
  }
}
