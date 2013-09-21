#include <cublas.h>
#include <cblas.h>
#include <stdio.h>
#include <stdlib.h>

void checkStatus(char* message, cublasStatus status) {
    if (status != CUBLAS_STATUS_SUCCESS) {
    	fprintf (stderr, "!!!! %s fail %d\n", message, status);
    	exit(EXIT_FAILURE);
    }
}
							   
double cblas_ddot (const int n, const double *x, const int incx, const double *y, const int incy) {
	double result;
	double *cuA, *cuB;
	cublasStatus status;

	status = cublasInit();
	checkStatus("init",  status);	
	status = cublasAlloc(n, sizeof(double),(void**)&cuA);
	checkStatus("A", status);
	status = cublasAlloc(n, sizeof(double),(void**)&cuB);
	checkStatus("B", status);
	
	status = cublasSetVector(n, sizeof(double), x, incx, cuA, incx);
	checkStatus("setA", status);

	status = cublasSetVector(n, sizeof(double), y, incy, cuB, incy);
	checkStatus("setB", status);
	
	result = cublasDdot(n, cuA, incx, cuB, incy);
	
	status = cublasFree(cuA);
	checkStatus("freeA", status);
	status = cublasFree(cuB);
	checkStatus("freeB", status);

//	cublasShutdown();
	
	return result;
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
	cublasDgemm('N', 'N', M, N, K, alpha, cuA, lda, cuB, ldb, beta, cuC, ldc);
	//checkStatus("dgemm", status);
	
	status = cublasGetMatrix(M, N, sizeof(double), cuC, ldc, C, ldc);
	checkStatus("setB", status);
	
	status = cublasFree(cuA);
	checkStatus("freeA", status);
	status = cublasFree(cuB);
	checkStatus("freeB", status);
	status = cublasFree(cuC);
	checkStatus("freeC", status);

//	cublasShutdown();
}