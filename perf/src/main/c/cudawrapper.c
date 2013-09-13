/*
This seems to be needed as the CBLAS with, e.g.

../../../../netlib/CBLAS/*{dgemm,xerbla,f77,globals}*.* -DF77_dgemm=cublasDgemm

doesn't seem to produce the correct calls, giving:

  ** On entry to DGEMM  parameter number 1 had an illegal value

*/

#include <cublas.h>
#include <cblas.h>
							   
double cblas_ddot (const int n, const double *x, const int incx, const double *y, const int incy) {
	return cublasDdot(n, x, incx, y, incy);
}

void checkStatus(char* message, cublasStatus status) {
	printf(message);
	printf("\n");
	fflush(stdout);
    if (status != CUBLAS_STATUS_SUCCESS) {
    	fprintf (stderr, "!!!! device memory allocation error (A)\n");
    	return EXIT_FAILURE;
    }
}

void cblas_dgemm(const enum CBLAS_ORDER Order, const enum CBLAS_TRANSPOSE TransA,
                 const enum CBLAS_TRANSPOSE TransB, const int M, const int N,
                 const int K, const double alpha, const double  *A,
                 const int lda, const double  *B, const int ldb,
                 const double beta, double  *C, const int ldc) {
	double *cuA, *cuB, *cuC;
	cublasStatus status;

	status = cublasInit();
	checkStatus("init",  status);	
	status = cublasAlloc(M * N, sizeof(double),(void**)&cuA);
	checkStatus("A", status);
	status = cublasAlloc(N * K, sizeof(double),(void**)&cuB);
	checkStatus("B", status);
	status = cublasAlloc(M * K, sizeof(double),(void**)&cuC);
	checkStatus("C", status);
	
	status = cublasSetMatrix(M, N, sizeof(double), A, lda, cuA, lda);
	checkStatus("setA", status);

	status = cublasSetMatrix(M, N, sizeof(double), B, ldb, cuB, ldb);
	checkStatus("setB", status);

	// status = cublasSetMatrix(M, N, sizeof(double), C, ldc, cuC, ldc);
	// checkStatus("setC", status);
	
	// HACK: ignore trans			   
	status = cublasDgemm('N', 'N', M, N, K, alpha, cuA, lda, cuB, ldb, beta, cuC, ldc);
	checkStatus("dgemm", status);
	
	status = cubblasGetMatrix(M, N, sizeof(double), cuC, ldc, C, ldc);
	checkStatus("setB", status);
	
	status = cublasFree(cuA);
	checkStatus("freeA", status);
	status = cublasFree(cuB);
	checkStatus("freeB", status);
	status = cublasFree(cuC);
	checkStatus("freeC", status);
}