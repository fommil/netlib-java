//#include "clBlas.h"

#include <cblas.h>

typedef enum clblasOrder {
    clblasRowMajor,
    clblasColumnMajor
} clblasOrder;

typedef enum clblasTranspose {
    clblasNoTrans,
    clblasTrans,
    clblasConjTrans
} clblasTranspose;

typedef enum clblasUplo {
    clblasUpper,
    clblasLower
} clblasUplo;

typedef enum clblasDiag {
    clblasUnit,
    clblasNonUnit
} clblasDiag;

typedef enum clblasSide {
    clblasLeft,
    clblasRight
} clblasSide;


void cblas_dgemm(const enum CBLAS_ORDER Order, const enum CBLAS_TRANSPOSE TransA,
                 const enum CBLAS_TRANSPOSE TransB, const int M, const int N,
                 const int K, const double alpha, const double  *A,
                 const int lda, const double  *B, const int ldb,
                 const double beta, double  *C, const int ldc) {
	// HACK: ignore trans			   
					 clblasDgemm(
    clblasColumnMajor,
    clblasNoTrans,
    clblasNoTrans,
    M, N, K, alpha, A, 0, lda, B, 0, ldb, beta, C, 0, ldc,
	0, NULL, 0, NULL, NULL


/* WTF?
	
	    cl_uint numCommandQueues,
    cl_command_queue *commandQueues,
    cl_uint numEventsInWaitList,
    const cl_event *eventWaitList,
    cl_event *events
	*/
	);
}