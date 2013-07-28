/*
  This file has been extracted and edited from the ARPACK++ distribution
  to define the Fortran routines for use in C.

  ARPACK++ v1.0 8/1/1997
  c++ interface to ARPACK code.

  MODULE arpackf.h
  ARPACK FORTRAN routines.

  ARPACK Authors
     Richard Lehoucq
     Danny Sorensen
     Chao Yang
     Dept. of Computational & Applied Mathematics
     Rice University
     Houston, Texas
*/

// debug "common" statement.

  struct { 
    integer logfil, ndigit, mgetv0;
    integer msaupd, msaup2, msaitr, mseigt, msapps, msgets, mseupd;
    integer mnaupd, mnaup2, mnaitr, mneigt, mnapps, mngets, mneupd;
    integer mcaupd, mcaup2, mcaitr, mceigt, mcapps, mcgets, mceupd;
  } debug_;


// double precision symmetric routines.

  void dsaupd_(integer *ido, char *bmat, integer *n, char *which,
                       integer *nev, double *tol, double *resid,
                       integer *ncv, double *V, integer *ldv,
                       integer *iparam, integer *ipntr, double *workd,
                       double *workl, integer *lworkl, integer *info);

  void dseupd_(logical *rvec, char *HowMny, logical *select,
                       double *d, double *Z, integer *ldz,
                       double *sigma, char *bmat, integer *n,
                       char *which, integer *nev, double *tol,
                       double *resid, integer *ncv, double *V,
                       integer *ldv, integer *iparam, integer *ipntr,
                       double *workd, double *workl,
                       integer *lworkl, integer *info);

// double precision nonsymmetric routines.

  void dnaupd_(integer *ido, char *bmat, integer *n, char *which,
                       integer *nev, double *tol, double *resid,
                       integer *ncv, double *V, integer *ldv,
                       integer *iparam, integer *ipntr, double *workd,
                       double *workl, integer *lworkl, integer *info);

  void dneupd_(logical *rvec, char *HowMny, logical *select,
                       double *dr, double *di, double *Z,
                       integer *ldz, double *sigmar,
                       double *sigmai, double *workev,
                       char *bmat, integer *n, char *which,
                       integer *nev, double *tol, double *resid,
                       integer *ncv, double *V, integer *ldv,
                       integer *iparam, integer *ipntr,
                       double *workd, double *workl,
                       integer *lworkl, integer *info);

// single precision symmetric routines.

  void ssaupd_(integer *ido, char *bmat, integer *n, char *which,
                       integer *nev, float *tol, float *resid,
                       integer *ncv, float *V, integer *ldv,
                       integer *iparam, integer *ipntr, float *workd,
                       float *workl, integer *lworkl, integer *info);

  void sseupd_(logical *rvec, char *HowMny, logical *select,
                       float *d, float *Z, integer *ldz,
                       float *sigma, char *bmat, integer *n,
                       char *which, integer *nev, float *tol,
                       float *resid, integer *ncv, float *V,
                       integer *ldv, integer *iparam, integer *ipntr,
                       float *workd, float *workl,
                       integer *lworkl, integer *info);

// single precision nonsymmetric routines.

  void snaupd_(integer *ido, char *bmat, integer *n, char *which,
                       integer *nev, float *tol, float *resid,
                       integer *ncv, float *V, integer *ldv,
                       integer *iparam, integer *ipntr, float *workd,
                       float *workl, integer *lworkl, integer *info);

  void sneupd_(logical *rvec, char *HowMny, logical *select,
                       float *dr, float *di, float *Z,
                       integer *ldz, float *sigmar,
                       float *sigmai, float *workev, char *bmat,
                       integer *n, char *which, integer *nev,
                       float *tol, float *resid, integer *ncv,
                       float *V, integer *ldv, integer *iparam,
                       integer *ipntr, float *workd, float *workl,
                       integer *lworkl, integer *info);
