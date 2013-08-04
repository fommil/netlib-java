package com.github.fommil.netlib;

import com.google.common.base.Stopwatch;
import junit.framework.TestCase;
import lombok.extern.java.Log;
import org.junit.Test;

import java.util.Random;

@Log
public class BLASTest extends TestCase {
	private final BLAS jBLAS = BLAS.getInstance();

  @Test
	public void testDot() {
		testDot1(jBLAS);
	}

	/**
	 * We test the JNI and the Java code at the same time so that we can compare results.
	 * Future JUnit tests should probably test against prior results so that we can write
	 * tests for the BLAS interface and then send the appropriate implementation to it.
	 *
	 * @see #testDot()
	 */
  @Test
	public void testDotSpeed() {
		int[] sizes = new int[]{10, 100, 1000, 10000, 20000, 50000, 75000,
			100000, 200000, 500000, 1000000, 10000000, 50000000
		};

        Stopwatch stopwatch = new Stopwatch();
		for (int size : sizes) {
			final double[] array1 = randomArray(size);
			final double[] array2 = randomArray(size);

            stopwatch.start();
			double outJ = jBLAS.ddot(size, array1, 1, array2, 1);
            stopwatch.stop();

            BLASTest.log.info(size + " took " + stopwatch);
		}
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
