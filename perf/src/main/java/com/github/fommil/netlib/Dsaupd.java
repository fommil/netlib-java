package com.github.fommil.netlib;

import com.google.common.base.Stopwatch;
import lombok.extern.java.Log;
import org.netlib.util.doubleW;
import org.netlib.util.intW;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Port of dssimp from the APACK distribution.
 *
 * @author Sam Halliday
 * @see <a href="http://forge.scilab.org/index.php/p/arpack-ng/source/tree/master/EXAMPLES/SIMPLE/dssimp.f">ddsimp.f</a>
 */
@Log
public class Dsaupd implements Benchmark.Parameterised {

  private ARPACK arpack = ARPACK.getInstance();

  @Override
  public long benchmark(int size) {
    int n = (int)Math.sqrt(size);

    // calculate the top 10%
    boolean eigenvectors = true;
    int eigenvalues = Math.max(1, n / 10);

    double tolerance = 0.00001;

    int ldv = n;
    intW nev = new intW(eigenvalues);
    int ncv = 2 * eigenvalues;
    if (ncv > n) {
      ncv = n;
    }
    String bmat = "I";
    String which = "LM";
    doubleW tol = new doubleW(tolerance);
    intW info = new intW(0);
    int[] iparam = new int[11];
    iparam[0] = 1;
    iparam[2] = 100;
    iparam[6] = 1;

    int lworkl = ncv * (ncv + 8);
    int vMemory = ldv * ncv;
    int worklMemory = lworkl;
    int workdMemory = 3 * n;
    int dMemory = 2 * ncv;
    int residMemory = n;
    int selectMemory = ncv;

    double[] v = new double[vMemory];
    double[] workl = new double[worklMemory];
    double[] workd = new double[workdMemory];
    double[] d = new double[dMemory];
    double[] resid = new double[residMemory];
    boolean[] select = new boolean[selectMemory];
    int[] ipntr = new int[11];

    intW ido = new intW(0);
    intW ierr = new intW(0);
    double sigma = 0.0;

    Stopwatch watch = new Stopwatch();
    watch.start();

    log.info(ncv + " " + nev.val + " " + n);

    while (true) {
//      log.info(Joiner.on(",").join(ido.val, bmat, n, which, nev.val, tol.val, resid, ncv, v, ldv, iparam, ipntr, workd, workl, lworkl, info.val));
      arpack.dsaupd(ido, bmat, n, which, nev.val, tol, resid, ncv, v, ldv, iparam, ipntr, workd, workl, lworkl, info);
      if (ido.val != -1 && ido.val != 1) break;

      // don't count the matrix routines in the time test!
      watch.stop();
      av(n, workd, ipntr[0] - 1, workd, ipntr[1] - 1);
      watch.start();
    }

    watch.stop();

    if (info.val < 0) throw new IllegalStateException("info = " + info.val);

    arpack.dseupd(eigenvectors, "All", select, d, v, ldv, sigma, bmat, n, which, nev, tol.val, resid, ncv, v, ldv, iparam, ipntr, workd, workl, lworkl, ierr);

    if (ierr.val != 0) throw new IllegalStateException("ierr = " + ierr.val);

    return watch.elapsed(TimeUnit.NANOSECONDS);
  }

  /*
  | Perform matrix vector multiplication |
  |              y <--- OP*x             |

  Computes w <--- OP*v, where OP is the nx*nx by nx*nx block
  tridiagonal matrix

                    | T -I          |
                    |-I  T -I       |
               OP = |   -I  T       |
                    |        ...  -I|
                    |           -I T|
  */
  private void av(int n, double[] input, int input_offset, double[] output, int output_offset) {
    double[] x = Arrays.copyOfRange(input, input_offset, input_offset + n);
//    double[] y = new double[n];

    double[] y = x;
    // let T = 2, I = 1
    // matrix applied to x, results in y...
//    for (int row = 0; row < n; row++) {
//      for (int col = 0; col < n; col++) {
//        if (col == (row - 1) || col == (row + 1)) {
//          y[row] = y[row] - x[col];
//        } else if (row == col) {
//          y[row] = y[row] + 2 * x[col];
//        }
//      }
//    }

    for (int i = 0; i < n; i++) {
      output[i + output_offset] = y[i];
    }
  }
}
