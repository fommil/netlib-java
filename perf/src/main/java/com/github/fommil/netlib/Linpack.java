package com.github.fommil.netlib;

/**
 * Routines that give a bit of a workout of BLAS.
 *
 * @author Sam Halliday (adapted to netlib-java)
 * @author David M. Doolin (bugfixes)
 * @author Jonathan Hardwick (Java optimisations)
 * @author Reed Wade (Java translation)
 * @author Jack Dongarra (bugfixes)
 * @author Bonnie Toy (C translation)
 * @see <a href="http://www.netlib.org/linpack/">LINPACK</a>
 */
public class Linpack {

  private BLAS blas = BLAS.getInstance();

  public static void main(String[] args) {
    Linpack l = new Linpack();
    l.run_benchmark();
  }

  double abs(double d) {
    return (d >= 0) ? d : -d;
  }

  double second_orig = -1;

  double second() {
    if (second_orig == -1) {
      second_orig = System.currentTimeMillis();
    }
    return (System.currentTimeMillis() - second_orig) / 1000;
  }

  public void run_benchmark() {
    double mflops_result = 0.0;
    double residn_result = 0.0;
    double time_result = 0.0;
    double eps_result = 0.0;

    double a[][] = new double[200][201];
    double b[] = new double[200];
    double x[] = new double[200];
    double cray, ops, total, norma, normx;
    double resid, time;
    double kf;
    int n, i, ntimes, info, lda, ldaa, kflops;
    int ipvt[] = new int[200];

    //double mflops_result;
    //double residn_result;
    //double time_result;
    //double eps_result;

    lda = 201;
    ldaa = 200;
    cray = .056;
    n = 100;

    ops = (2.0e0 * (n * n * n)) / 3.0 + 2.0 * (n * n);

    norma = matgen(a, lda, n, b);
    time = second();
    info = dgefa(a, lda, n, ipvt);
    dgesl(a, lda, n, ipvt, b, 0);
    total = second() - time;

    for (i = 0; i < n; i++) {
      x[i] = b[i];
    }
    norma = matgen(a, lda, n, b);
    for (i = 0; i < n; i++) {
      b[i] = -b[i];
    }
    dmxpy(n, b, n, lda, x, a);
    resid = 0.0;
    normx = 0.0;
    for (i = 0; i < n; i++) {
      resid = (resid > abs(b[i])) ? resid : abs(b[i]);
      normx = (normx > abs(x[i])) ? normx : abs(x[i]);
    }

    eps_result = epslon((double) 1.0);
/*

    residn_result = resid/( n*norma*normx*eps_result );
    time_result = total;
    mflops_result = ops/(1.0e6*total);

    return ("Mflops/s: " + mflops_result +
	    "  Time: " + time_result + " secs" +
	    "  Norm Res: " + residn_result +
	    "  Precision: " + eps_result);
*/
    residn_result = resid / (n * norma * normx * eps_result);
    residn_result += 0.005; // for rounding
    residn_result = (int) (residn_result * 100);
    residn_result /= 100;

    time_result = total;
    time_result += 0.005; // for rounding
    time_result = (int) (time_result * 100);
    time_result /= 100;

    mflops_result = ops / (1.0e6 * total);
    mflops_result += 0.0005; // for rounding
    mflops_result = (int) (mflops_result * 1000);
    mflops_result /= 1000;

    System.out.println("Mflops/s: " + mflops_result +
        "  Time: " + time_result + " secs" +
        "  Norm Res: " + residn_result +
        "  Precision: " + eps_result);
  }


  final double matgen(double a[][], int lda, int n, double b[]) {
    double norma;
    int init, i, j;

    init = 1325;
    norma = 0.0;
/*  Next two for() statements switched.  Solver wants
matrix in column order. --dmd 3/3/97
*/
    for (i = 0; i < n; i++) {
      for (j = 0; j < n; j++) {
        init = 3125 * init % 65536;
        a[j][i] = (init - 32768.0) / 16384.0;
        norma = (a[j][i] > norma) ? a[j][i] : norma;
      }
    }
    for (i = 0; i < n; i++) {
      b[i] = 0.0;
    }
    for (j = 0; j < n; j++) {
      for (i = 0; i < n; i++) {
        b[i] += a[j][i];
      }
    }

    return norma;
  }


  /*
    dgefa factors a double precision matrix by gaussian elimination.

    dgefa is usually called by dgeco, but it can be called
    directly with a saving in time if  rcond  is not needed.
    (time for dgeco) = (1 + 9/n)*(time for dgefa) .

    on entry

    a       double precision[n][lda]
    the matrix to be factored.

    lda     integer
    the leading dimension of the array  a .

    n       integer
    the order of the matrix  a .

    on return

    a       an upper triangular matrix and the multipliers
    which were used to obtain it.
    the factorization can be written  a = l*u  where
    l  is a product of permutation and unit lower
    triangular matrices and  u  is upper triangular.

    ipvt    integer[n]
    an integer vector of pivot indices.

    info    integer
    = 0  normal value.
    = k  if  u[k][k] .eq. 0.0 .  this is not an error
    condition for this subroutine, but it does
    indicate that dgesl or dgedi will divide by zero
    if called.  use  rcond  in dgeco for a reliable
    indication of singularity.

    linpack. this version dated 08/14/78.
    cleve moler, university of new mexico, argonne national lab.

    functions

    blas daxpy,dscal,idamax
  */
  final int dgefa(double a[][], int lda, int n, int ipvt[]) {
    double[] col_k, col_j;
    double t;
    int j, k, kp1, l, nm1;
    int info;

    // gaussian elimination with partial pivoting

    info = 0;
    nm1 = n - 1;
    if (nm1 >= 0) {
      for (k = 0; k < nm1; k++) {
        col_k = a[k];
        kp1 = k + 1;

        // find l = pivot index

        l = blas.idamax(n - k, col_k, k, 1) + k;
        ipvt[k] = l;

        // zero pivot implies this column already triangularized

        if (col_k[l] != 0) {

          // interchange if necessary

          if (l != k) {
            t = col_k[l];
            col_k[l] = col_k[k];
            col_k[k] = t;
          }

          // compute multipliers

          t = -1.0 / col_k[k];
          blas.dscal(n - (kp1), t, col_k, kp1, 1);

          // row elimination with column indexing

          for (j = kp1; j < n; j++) {
            col_j = a[j];
            t = col_j[l];
            if (l != k) {
              col_j[l] = col_j[k];
              col_j[k] = t;
            }
            blas.daxpy(n - (kp1), t, col_k, kp1, 1,
                col_j, kp1, 1);
          }
        } else {
          info = k;
        }
      }
    }
    ipvt[n - 1] = n - 1;
    if (a[(n - 1)][(n - 1)] == 0) info = n - 1;

    return info;
  }


  /*
    dgesl solves the double precision system
    a * x = b  or  trans(a) * x = b
    using the factors computed by dgeco or dgefa.

    on entry

    a       double precision[n][lda]
    the output from dgeco or dgefa.

    lda     integer
    the leading dimension of the array  a .

    n       integer
    the order of the matrix  a .

    ipvt    integer[n]
    the pivot vector from dgeco or dgefa.

    b       double precision[n]
    the right hand side vector.

    job     integer
    = 0         to solve  a*x = b ,
    = nonzero   to solve  trans(a)*x = b  where
    trans(a)  is the transpose.

    on return

    b       the solution vector  x .

    error condition

    a division by zero will occur if the input factor contains a
    zero on the diagonal.  technically this indicates singularity
    but it is often caused by improper arguments or improper
    setting of lda .  it will not occur if the subroutines are
    called correctly and if dgeco has set rcond .gt. 0.0
    or dgefa has set info .eq. 0 .

    to compute  inverse(a) * c  where  c  is a matrix
    with  p  columns
    dgeco(a,lda,n,ipvt,rcond,z)
    if (!rcond is too small){
    for (j=0,j<p,j++)
    dgesl(a,lda,n,ipvt,c[j][0],0);
    }

    linpack. this version dated 08/14/78 .
    cleve moler, university of new mexico, argonne national lab.

    functions

    blas daxpy,ddot
  */
  final void dgesl(double a[][], int lda, int n, int ipvt[], double b[], int job) {
    double t;
    int k, kb, l, nm1, kp1;

    nm1 = n - 1;
    if (job == 0) {

      // job = 0 , solve  a * x = b.  first solve  l*y = b

      if (nm1 >= 1) {
        for (k = 0; k < nm1; k++) {
          l = ipvt[k];
          t = b[l];
          if (l != k) {
            b[l] = b[k];
            b[k] = t;
          }
          kp1 = k + 1;
          blas.daxpy(n - (kp1), t, a[k], kp1, 1, b, kp1, 1);
        }
      }

      // now solve  u*x = y

      for (kb = 0; kb < n; kb++) {
        k = n - (kb + 1);
        b[k] /= a[k][k];
        t = -b[k];
        blas.daxpy(k, t, a[k], 0, 1, b, 0, 1);
      }
    } else {

      // job = nonzero, solve  trans(a) * x = b.  first solve  trans(u)*y = b

      for (k = 0; k < n; k++) {
        t = blas.ddot(k, a[k], 0, 1, b, 0, 1);
        b[k] = (b[k] - t) / a[k][k];
      }

      // now solve trans(l)*x = y

      if (nm1 >= 1) {
        for (kb = 1; kb < nm1; kb++) {
          k = n - (kb + 1);
          kp1 = k + 1;
          b[k] += blas.ddot(n - (kp1), a[k], kp1, 1, b, kp1, 1);
          l = ipvt[k];
          if (l != k) {
            t = b[l];
            b[l] = b[k];
            b[k] = t;
          }
        }
      }
    }
  }


  /*
    estimate unit roundoff in quantities of size x.

    this program should function properly on all systems
    satisfying the following two assumptions,
    1.  the base used in representing dfloating point
    numbers is not a power of three.
    2.  the quantity  a  in statement 10 is represented to
    the accuracy used in dfloating point variables
    that are stored in memory.
    the statement number 10 and the go to 10 are intended to
    force optimizing compilers to generate code satisfying
    assumption 2.
    under these assumptions, it should be true that,
    a  is not exactly equal to four-thirds,
    b  has a zero for its last bit or digit,
    c  is not exactly equal to one,
    eps  measures the separation of 1.0 from
    the next larger dfloating point number.
    the developers of eispack would appreciate being informed
    about any systems where these assumptions do not hold.

    *****************************************************************
    this routine is one of the auxiliary routines used by eispack iii
    to avoid machine dependencies.
    *****************************************************************

    this version dated 4/6/83.
  */
  final double epslon(double x) {
    double a, b, c, eps;

    a = 4.0e0 / 3.0e0;
    eps = 0;
    while (eps == 0) {
      b = a - 1.0;
      c = b + b + b;
      eps = abs(c - 1.0);
    }
    return (eps * abs(x));
  }


  /*
    purpose:
    multiply matrix m times vector x and add the result to vector y.

    parameters:

    n1 integer, number of elements in vector y, and number of rows in
    matrix m

    y double [n1], vector of length n1 to which is added
    the product m*x

    n2 integer, number of elements in vector x, and number of columns
    in matrix m

    ldm integer, leading dimension of array m

    x double [n2], vector of length n2

    m double [ldm][n2], matrix of n1 rows and n2 columns
  */
  final void dmxpy(int n1, double y[], int n2, int ldm, double x[], double m[][]) {
    int j, i;

    // cleanup odd vector
    for (j = 0; j < n2; j++) {
      for (i = 0; i < n1; i++) {
        y[i] += x[j] * m[j][i];
      }
    }
  }

}
