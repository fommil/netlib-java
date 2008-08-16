
/**
 * Sep 14, 2007
 *
 * @author Samuel Halliday, ThinkTank Maths Limited
 * @copyright ThinkTank Maths Limited 2007
 */
package org.netlib.lapack;

import junit.framework.TestCase;

/**
 * @author Samuel Halliday, ThinkTank Maths Limited
 */
public class LAPACKTest extends TestCase {
	LAPACK jLAPACK = JLAPACK.INSTANCE;

	LAPACK nativeLAPACK = NativeLAPACK.INSTANCE;

	public void testGesvd() {
		double[] jAns = testGesvd1(jLAPACK);
		double[] nativeAns = testGesvd1(nativeLAPACK);
		assert arrayElementsEqual(jAns, nativeAns);
	}

	public void testSygv() {
		double[] jAns = testSygv1(jLAPACK);
		double[] nativeAns = testSygv1(nativeLAPACK);
		assert arrayElementsEqual(jAns, nativeAns);
	}

	private double[] testGesvd1(LAPACK lapack) {
		int M = 5;
		int N = 3;
		double[] m = {18.91, 14.91, -6.15, -18.15, 27.5, -1.59, -1.59, -2.25,
			-1.59, -2.25, -1.59, 1.59, 0.0, 1.59, 0.0
		};

		double[] s = new double[m.length];
		double[] u = new double[M * M];
		double[] vt = new double[N * N];
		double[] work =
			new double[Math.max(3 * Math.min(M, N) + Math.max(M, N),
			5 * Math.min(M, N))];
		org.netlib.util.intW info = new org.netlib.util.intW(2);

		lapack.dgesvd("A", "A", M, N, m, M, s, u, M, vt, N, work, work.length,
			info);

		return s;
	}

	private double[] testSygv1(LAPACK lapack) {
		int itype = 1;
		int n = 3;
		double[] a = {1.0, 2.0, 4.0, 0.0, 3.0, 5.0, 0.0, 0.0, 6.0};
		int lda = 3;
		double[] b = {2.5298, 0.6405, 0.2091, 0.3798, 2.7833, 0.6808, 0.4611,
			0.5678, 2.7942
		};
		int ldb = 3;
		double[] w = new double[n];
		int lwork = 9;
		double[] work = new double[lwork];
		org.netlib.util.intW info = new org.netlib.util.intW(0);

		lapack.dsygv(itype, "N", "U", n, a, lda, b, ldb, w, work, lwork, info);
		return w;
	}

	/**
	 * Convenience method to check equality of all elements in two arrays.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean arrayElementsEqual(double[] a, double[] b) {
		if (a.length != b.length)
			return false;

		for (int i = 0; i < a.length; i++) {
			if (Math.abs(a[i] - b[i]) > 0.00001d)
				return false;
		}
		return true;
	}
}
