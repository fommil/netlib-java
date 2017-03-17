package com.github.fommil.netlib;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class BLASTest {
  private final BLAS blas = BLAS.getInstance();

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

  @Test
  public void ddot() {
    double[] dx = {1.1, 2.2, 3.3, 4.4};
    double[] dy = {1.1, 2.2, 3.3, 4.4};
    int n = dx.length;

    double answer = blas.ddot(n, dx, 1, dy, 1);
    Assert.assertTrue(Math.abs(answer - 36.3) < 0.00001d);
  }
}
