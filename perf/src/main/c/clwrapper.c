#include <stdio.h>
#include <stdlib.h>

#include <cblas.h>
#include <clBlas.h>

void 
cblas_dgemm(const enum CBLAS_ORDER Order, const enum CBLAS_TRANSPOSE TransA,
	    const enum CBLAS_TRANSPOSE TransB, const int M, const int N,
	    const int K, const double alpha, const double *A,
	    const int lda, const double *B, const int ldb,
	    const double beta, double *C, const int ldc)
{
	cl_int		err;
	cl_platform_id	platform = 0;
	cl_device_id	device = 0;
	cl_context_properties props[3] = {CL_CONTEXT_PLATFORM, 0, 0};
	cl_context	ctx = 0;
	cl_command_queue queue = 0;
	cl_mem		bufA   , bufB, bufC;
	cl_event	event = NULL;
	cl_uint available = 0;
	int		ret = 0;

	// HACK: ignore order / trans
    clblasOrder order = clblasColumnMajor;
    clblasTranspose transA = clblasNoTrans;
    clblasTranspose transB = clblasNoTrans;


	/* Setup OpenCL environment. */
	err = clGetPlatformIDs(1, &platform, &available);
	if (err != CL_SUCCESS) {
		printf("clGetPlatformIDs() failed with %d and %d\n", err, available);
		exit(1);
	}
	printf("found %d OpenCL platforms\n", available);
	
	// CL_DEVICE_TYPE_GPU forces GPU use
	err = clGetDeviceIDs(platform, CL_DEVICE_TYPE_DEFAULT, 1, &device, &available);
	if (err != CL_SUCCESS) {
		printf("clGetDeviceIDs() failed with %d\n", err);
		exit(1);
	}
	printf("found %d OpenCL devices\n", available);
	
	props[1] = (cl_context_properties) platform;
	ctx = clCreateContext(props, 1, &device, NULL, NULL, &err);
	if (err != CL_SUCCESS) {
		printf("clCreateContext() failed with %d\n", err);
		exit(1);
	}
	printf("created context\n");
	
	queue = clCreateCommandQueue(ctx, device, 0, &err);
	if (err != CL_SUCCESS) {
		printf("clCreateCommandQueue() failed with %d\n", err);
		clReleaseContext(ctx);
		exit(1);
	}
	printf("created command queue\n");

	/* Setup clblas. */
	err = clblasSetup();
	if (err != CL_SUCCESS) {
		printf("clblasSetup() failed with %d\n", err);
		clReleaseCommandQueue(queue);
		clReleaseContext(ctx);
		exit(1);
	}
	printf("setup clblas\n");

	/* Prepare OpenCL memory objects and place matrices inside them. */
	bufA = clCreateBuffer(ctx, CL_MEM_READ_ONLY, M * K * sizeof(*A), NULL, &err);
	bufB = clCreateBuffer(ctx, CL_MEM_READ_ONLY, K * N * sizeof(*B), NULL, &err);
	bufC = clCreateBuffer(ctx, CL_MEM_READ_WRITE, M * N * sizeof(*C), NULL, &err);
	printf("created buffers\n");


	err = clEnqueueWriteBuffer(queue, bufA, CL_TRUE, 0, M * K * sizeof(double), A, 0, NULL, NULL);
	err = clEnqueueWriteBuffer(queue, bufB, CL_TRUE, 0, K * N * sizeof(double), B, 0, NULL, NULL);
	err = clEnqueueWriteBuffer(queue, bufC, CL_TRUE, 0, M * N * sizeof(double), C, 0, NULL, NULL);
	printf("enqueud buffers\n");

	/*
	 * Call clblas extended function. Perform gemm for the lower right
	 * sub-matrices
	 */
	err = clblasDgemm(order, transA, transB, M, N, K,
                         alpha, bufA, 0, lda,
                         bufB, 0, ldb, beta,
                         bufC, 0, ldc,
                         1, &queue, 0, NULL, &event);
	if (err != CL_SUCCESS) {
		printf("clblasSgemmEx() failed with %d\n", err);
		ret = 1;
	} else {
		printf("no errors for calculation\n");
		fflush(stdout);
		/* Wait for calculations to be finished. */
		err = clWaitForEvents(1, &event);

		/* Fetch results of calculations from GPU memory. */
		err = clEnqueueReadBuffer(queue, bufC, CL_TRUE, 0,
					  M * N * sizeof(double),
					  C, 0, NULL, NULL);
		printf("got result\n");
	}

	/* Release OpenCL memory objects. */
	clReleaseMemObject(bufC);
	clReleaseMemObject(bufB);
	clReleaseMemObject(bufA);
	printf("released\n");

	/* Finalize work with clblas. */
	clblasTeardown();
	printf("teardown clblas\n");

	/* Release OpenCL working objects. */
	clReleaseCommandQueue(queue);
	printf("release command queue\n");
	clReleaseContext(ctx);
	printf("release context\n");
}