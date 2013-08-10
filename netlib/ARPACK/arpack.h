/*
 * This file has been extracted and edited from the ARPACK++ distribution
 * to define the Fortran routines for use in C. The obvious conversions
 * have been used, in agreement with the CBLAS mappings.
 */

// double precision symmetric routines.

  void dsaupd_(int *ido, char *bmat, int *n, char *which,
                       int *nev, double *tol, double *resid,
                       int *ncv, double *V, int *ldv,
                       int *iparam, int *ipntr, double *workd,
                       double *workl, int *lworkl, int *info);

  void dseupd_(int *rvec, char *HowMny, int *select,
                       double *d, double *Z, int *ldz,
                       double *sigma, char *bmat, int *n,
                       char *which, int *nev, double *tol,
                       double *resid, int *ncv, double *V,
                       int *ldv, int *iparam, int *ipntr,
                       double *workd, double *workl,
                       int *lworkl, int *info);

// double precision nonsymmetric routines.

  void dnaupd_(int *ido, char *bmat, int *n, char *which,
                       int *nev, double *tol, double *resid,
                       int *ncv, double *V, int *ldv,
                       int *iparam, int *ipntr, double *workd,
                       double *workl, int *lworkl, int *info);

  void dneupd_(int *rvec, char *HowMny, int *select,
                       double *dr, double *di, double *Z,
                       int *ldz, double *sigmar,
                       double *sigmai, double *workev,
                       char *bmat, int *n, char *which,
                       int *nev, double *tol, double *resid,
                       int *ncv, double *V, int *ldv,
                       int *iparam, int *ipntr,
                       double *workd, double *workl,
                       int *lworkl, int *info);

// single precision symmetric routines.

  void ssaupd_(int *ido, char *bmat, int *n, char *which,
                       int *nev, float *tol, float *resid,
                       int *ncv, float *V, int *ldv,
                       int *iparam, int *ipntr, float *workd,
                       float *workl, int *lworkl, int *info);

  void sseupd_(int *rvec, char *HowMny, int *select,
                       float *d, float *Z, int *ldz,
                       float *sigma, char *bmat, int *n,
                       char *which, int *nev, float *tol,
                       float *resid, int *ncv, float *V,
                       int *ldv, int *iparam, int *ipntr,
                       float *workd, float *workl,
                       int *lworkl, int *info);

// single precision nonsymmetric routines.

  void snaupd_(int *ido, char *bmat, int *n, char *which,
                       int *nev, float *tol, float *resid,
                       int *ncv, float *V, int *ldv,
                       int *iparam, int *ipntr, float *workd,
                       float *workl, int *lworkl, int *info);

  void sneupd_(int *rvec, char *HowMny, int *select,
                       float *dr, float *di, float *Z,
                       int *ldz, float *sigmar,
                       float *sigmai, float *workev, char *bmat,
                       int *n, char *which, int *nev,
                       float *tol, float *resid, int *ncv,
                       float *V, int *ldv, int *iparam,
                       int *ipntr, float *workd, float *workl,
                       int *lworkl, int *info);
