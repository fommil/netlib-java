package com.github.fommil.netlib;

import com.google.common.base.Stopwatch;
import lombok.extern.java.Log;
import org.netlib.util.doubleW;
import org.netlib.util.intW;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Port of dssimp from the ARPACK distribution (although note that DSSIMP is incredibly memory inefficient as it
 * confuses N with NX and ends up setting all memory requirements to be needlessly quadratic).
 *
 * @author Sam Halliday
 * @see <a href="http://forge.scilab.org/index.php/p/arpack-ng/source/tree/master/EXAMPLES/SIMPLE/dssimp.f">ddsimp.f</a>
 */
@Log
public class Dsaupd implements Benchmark.Parameterised {

  private ARPACK arpack = ARPACK.getInstance();

  @Override
  public long benchmark(int size) {
    int n = (int) Math.sqrt(size);

    // calculate 10% of the eigenvalues
    int eigenvalues = Math.max(1, n / 10);

    double tolerance = 0.0;
    int ldv = n;
    intW nev = new intW(eigenvalues);

    int ncv = Math.min(2 * eigenvalues, n);
    String bmat = "I";
    String which = "LM";
    doubleW tol = new doubleW(tolerance);
    intW info = new intW(0);
    int[] iparam = new int[11];
    iparam[0] = 1;
    iparam[2] = 300;
    iparam[6] = 1;
    intW ido = new intW(0);

    // used for initial residual (if info != 0)
    // and eventually the output residual
    double[] resid = new double[n];
    // Lanczos basis vectors
    double[] v = new double[ldv * ncv];
    // Arnoldi reverse communication
    double[] workd = new double[3 * n];
    // private work array
    double[] workl = new double[ncv * (ncv + 8)];
    int[] ipntr = new int[11];

    Stopwatch watch = new Stopwatch();
    watch.start();

    int i = 0;
    while (true) {
      i++;
      arpack.dsaupd(ido, bmat, n, which, nev.val, tol, resid, ncv, v, ldv, iparam, ipntr, workd, workl, workl.length, info);
      if (ido.val != -1 && ido.val != 1) break;

      // could be refactored to handle the other types of mode

      watch.stop();
      av(n, workd, ipntr[0] - 1, ipntr[1] - 1);
      watch.start();
    }

    watch.stop();

    log.info(i + " iterations for " + n);

    if (info.val < 0) throw new IllegalStateException("info = " + info.val);

//    double[] d = new double[2 * ncv];
//    boolean[] select = new boolean[ncv];
//    intW ierr = new intW(0);
//    double sigma = 0.0;
//    arpack.dseupd(eigenvectors, "All", select, d, v, ldv, sigma, bmat, n, which, nev, tol.val, resid, ncv, v, ldv, iparam, ipntr, workd, workl, workl.length, ierr);
//    if (ierr.val != 0) throw new IllegalStateException("ierr = " + ierr.val);

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
  private void av(int n, double[] work, int input_offset, int output_offset) {
    double[] x = Arrays.copyOfRange(work, input_offset, input_offset + n);
    double[] y = new double[n];

    // let T = 2, I = 1
    // matrix applied to x, results in y...
    for (int row = 0; row < n; row++) {
      for (int col = 0; col < n; col++) {
        if (col == (row - 1) || col == (row + 1)) {
          y[row] -= x[col];
        } else if (row == col) {
          y[row] += 2 * x[col];
        }
      }
    }

    System.arraycopy(y, 0, work, output_offset, n);
  }
}
