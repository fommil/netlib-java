package com.github.fommil.netlib;

import com.google.common.base.Stopwatch;
import lombok.extern.java.Log;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Log
public class BLASTest {
  private final BLAS blas = BLAS.getInstance();

  @Test
  public void ddot() {
    testDot1(blas);
  }

  @Test
  public void ddotPerf() {
    Stopwatch stopwatch = new Stopwatch();
    double factor = 6 / 100.0;
    for (int i = 1; i <= 100; i++) {
      stopwatch.reset();
      int size = (int) Math.pow(10, factor * i);

      for (int j = 0 ; j < 10; j++) {
        final double[] array1 = randomArray(size);
        final double[] array2 = randomArray(size);
        stopwatch.start();
        blas.ddot(size, array1, 1, array2, 1);
        stopwatch.stop();
      }

      System.out.println(size + "," + stopwatch.elapsed(TimeUnit.NANOSECONDS));
    }
  }

  @Test
  public void offsets() {
    double[] matrix = new double[]{
        1, 1, 1, 1, 1,
        1, 1, 1, 1, 1,
        1, 1, 1, 1, 1,
        1, 1, 1, 1, 1,
        1, 1, 1, 1, 1
    };
    blas.dscal(5, 2.0, matrix, 2, 5);
    double[] expected = new double[]{
        1, 1, 2, 1, 1,
        1, 1, 2, 1, 1,
        1, 1, 2, 1, 1,
        1, 1, 2, 1, 1,
        1, 1, 2, 1, 1
    };
    Assert.assertArrayEquals(Arrays.toString(matrix), expected, matrix, 0.0);
  }

  // return array of size n with normally distributed elements
  // this is a bottleneck when running the tests
  private double[] randomArray(int n) {
    assert n > 0;
    Random random = new Random();
    double[] array = new double[n];
    for (int i = 0; i < n; i++) {
      array[i] = random.nextGaussian();
    }
    return array;
  }

  private void testDot1(BLAS blas) {
    double[] dx = {1.1, 2.2, 3.3, 4.4};
    double[] dy = {1.1, 2.2, 3.3, 4.4};
    int n = dx.length;

    double answer = blas.ddot(n, dx, 1, dy, 1);
    assert Math.abs(answer - 36.3) < 0.00001d;
  }
}
