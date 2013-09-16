/*
CUDA speed test without the memory overhead

gcc-mp-4.8 -O3 cudadgemmtest.c common.c -o cudadgemmtest -I../../../../netlib/CBLAS -I/usr/local/cuda/include/ -L/usr/local/cuda/lib -lcublas
export DYLD_LIBRARY_PATH=/usr/local/cuda/lib
./cudadgemmtest  > ../../../results/mac_os_x-x86_64-dgemm-cuda_nooh.csv


*/

#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <cblas.h>
#include "common.h"
#include <cublas.h>


// does AB == C ? If not, complain on stderr
void test(int m, double* a, double *b, double *c) {
	int i, j, k, exact = 0, wrong = 0;
	double diff;
	double* d = calloc(m * m, sizeof(double));
	for (i = 0 ; i < m ; i++) {
		for (j = 0 ; j < m ; j++) {
			for (k = 0 ; k < m ; k++) {
				d[i + j * m] += a[i + k * m] * b[j * m + k];
			}
		}
	}
	for (i = 0 ; i < m ; i++) {
		for (j = 0 ; j < m ; j++) {
			diff = c[i * m + j] - d[i * m + j];
			if (diff != 0.0) {
				exact++;
			}
			if (abs(diff) > 0.000001) {
				wrong++;
			}
		}		
	}
	free(d);
	if (wrong > 0) {
		fprintf(stderr, "not exact = %d, wrong = %d\n", exact, wrong);
	}
}

void checkStatus(char* message, cublasStatus status) {
    if (status != CUBLAS_STATUS_SUCCESS) {
    	fprintf (stderr, "!!!! %s fail %d\n", message, status);
    	exit(EXIT_FAILURE);
    }
}

long benchmark(int size) {
    int m = sqrt(size);
	long requestStart, requestEnd;

    double* a = random_array(m * m);
    double* b = random_array(m * m);
    double* c = calloc(m * m, sizeof(double));

	double *cuA, *cuB, *cuC;
	cublasStatus status;

	status = cublasAlloc(m * m, sizeof(double),(void**)&cuA);
	checkStatus("A", status);
	status = cublasAlloc(m * m, sizeof(double),(void**)&cuB);
	checkStatus("B", status);
	status = cublasAlloc(m * m, sizeof(double),(void**)&cuC);
	checkStatus("C", status);
	
	status = cublasSetMatrix(m, m, sizeof(double), a, m, cuA, m);
	checkStatus("setA", status);

	status = cublasSetMatrix(m, m, sizeof(double), b, m, cuB, m);
	checkStatus("setB", status);

	requestStart = currentTimeNanos();

	cublasDgemm('N', 'N', m, m, m, 1, cuA, m, cuB, m, 0, cuC, m);

	requestEnd = currentTimeNanos();

	status = cublasGetMatrix(m, m, sizeof(double), cuC, m, c, m);
	checkStatus("setB", status);
	
	status = cublasFree(cuA);
	checkStatus("freeA", status);
	status = cublasFree(cuB);
	checkStatus("freeB", status);
	status = cublasFree(cuC);
	checkStatus("freeC", status);

#ifdef __TEST__
	test(m, a, b, c);
#endif


    free(a);
    free(b);
    free(c);

    return (requestEnd - requestStart);
  }

main()
{
	cublasStatus status;
	
	srand(time(NULL));
	
	status = cublasInit();
	checkStatus("init",  status);

    double factor = 6.0 / 100.0;
    int i, j;
    for (i = 0 ; i < 10 ; i++) {
        for (j = 1 ; j <= 100 ; j++) {
            int size = (int) pow(10.0, factor * j);
            if (size < 10) continue;
            long took = benchmark(size);
            printf("\"%d\",\"%lu\"\n", size, took);
			fflush(stdout);
        }
    }
}