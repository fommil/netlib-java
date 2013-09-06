#include <cublas.h>
#include <cblas.h>
							   
double cblas_ddot (const int n, const double *x, const int incx, const double *y, const int incy) {
	return cublasDdot(n, x, incx, y, incy);
}

void cblas_dgemm(const enum CBLAS_ORDER Order, const enum CBLAS_TRANSPOSE TransA,
                 const enum CBLAS_TRANSPOSE TransB, const int M, const int N,
                 const int K, const double alpha, const double  *A,
                 const int lda, const double  *B, const int ldb,
                 const double beta, double  *C, const int ldc) {
	// HACK: ignore trans			   
	cublasDgemm('N', 'N', M, N, K, alpha, A, lda, B, ldb, beta, C, ldc);
}