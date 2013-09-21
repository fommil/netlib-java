/*


gcc-mp-4.8 -O3 cudaddottest.c common.c -o cudaddottest -I../../../../netlib/CBLAS -I/usr/local/cuda/include/ -L/usr/local/cuda/lib -lcublas
export DYLD_LIBRARY_PATH=/usr/local/cuda/lib
./cudaddottest  > ../../../results/mac_os_x-x86_64-ddot-cuda_nooh.csv

*/

#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <cblas.h>
#include "common.h"
#include <cublas.h>

void checkStatus(char* message, cublasStatus status) {
    if (status != CUBLAS_STATUS_SUCCESS) {
    	fprintf (stderr, "!!!! %s fail %d\n", message, status);
    	exit(EXIT_FAILURE);
    }
}

long benchmark(int size) {
	long requestStart, requestEnd;
	int incx = 1, incy = 1, n = size;
	double *cuA, *cuB;
	cublasStatus status;
	

    double* a = random_array(size);
    double* b = random_array(size);

	status = cublasAlloc(n, sizeof(double),(void**)&cuA);
	checkStatus("A", status);
	status = cublasAlloc(n, sizeof(double),(void**)&cuB);
	checkStatus("B", status);
	
	status = cublasSetVector(n, sizeof(double), a, incx, cuA, incx);
	checkStatus("setA", status);

	status = cublasSetVector(n, sizeof(double), b, incy, cuB, incy);
	checkStatus("setB", status);

	requestStart = currentTimeNanos();

	cublasDdot(n, cuA, incx, cuB, incy);

	requestEnd = currentTimeNanos();
	
	status = cublasFree(cuA);
	checkStatus("freeA", status);
	status = cublasFree(cuB);
	checkStatus("freeB", status);

    free(a);
    free(b);

    return (requestEnd - requestStart);
  }

main()
{
	cublasStatus status;

	status = cublasInit();
	checkStatus("init",  status);	
	
	srand(time(NULL));

    double factor = 6.0 / 100.0;
    int i, j;
    for (i = 0 ; i < 10 ; i++) {
        for (j = 1 ; j <= 100 ; j++) {
            int size = (int) pow(10.0, factor * j);
            if (size < 10) continue;
            long took = benchmark(size);
            printf("\"%d\",\"%lu\"\n", size, took);
        }
    }
}