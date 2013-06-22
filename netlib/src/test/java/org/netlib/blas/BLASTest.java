
/**
 * Jun 4, 2007
 *
 * @author Samuel Halliday, ThinkTank Maths Limited
 * @copyright ThinkTank Maths Limited 2007
 */
package org.netlib.blas;

import java.util.Random;

import junit.framework.TestCase;

/**
 * @author Samuel Halliday, ThinkTank Maths Limited
 */
public class BLASTest extends TestCase {
	private final BLAS jBLAS = JBLAS.INSTANCE;

	private final NativeBLAS nativeBLAS = NativeBLAS.INSTANCE;

	public void testDot() {
		testDot1(jBLAS);
		testDot1(nativeBLAS);
	}

	/**
	 * We test the JNI and the Java code at the same time so that we can compare results.
	 * Future JUnit tests should probably test against prior results so that we can write
	 * tests for the BLAS interface and then send the appropriate implementation to it.
	 *
	 * @see #testDot()
	 */
	public void testDotSpeed() {
		assert nativeBLAS.isLoaded;

		int[] sizes = new int[]{10, 100, 1000, 10000, 20000, 50000, 75000,
			100000, 200000, 500000, 1000000, 10000000
		}; // , 50000000 };

		for (int size : sizes) {
			final double[] array1 = randomArray(size);
			final double[] array2 = randomArray(size);

			long start = System.currentTimeMillis();
			double outJ = jBLAS.ddot(size, array1, 1, array2, 1);
			long endJ = System.currentTimeMillis();
			double outN = nativeBLAS.ddot(size, array1, 1, array2, 1);
			long endN = System.currentTimeMillis();


			assert Math.abs(outJ - outN) < 0.00001d;
			System.out.println("Array size: " + size + ", jLAPACK took: " +
				(endJ - start) / 1000.0 + ", nativeLAPACK took: " +
				(endN - endJ) / 1000.0);
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
