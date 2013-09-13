/*

cp ../../../../native_ref/<target binary> libnetlib.so

gcc-mp-4.8 -O3 dgemmtest.c common.c -o dgemmtest -L. -lnetlib -I../../../../netlib/CBLAS
./dgemmtest  > ../../../results/mac_os_x-x86_64-dgemm-CBLAS.csv

gcc-mp-4.8 -O3 dgemmtest.c common.c -o dgemmtest -I/System/Library/Frameworks/vecLib.framework/Headers -framework veclib
./dgemmtest  > ../../../results/mac_os_x-x86_64-dgemm-veclib.csv

gcc-mp-4.8 -O3 dgemmtest.c common.c -o dgemmtest -I/opt/local/include /opt/local/lib/libatlas.a /opt/local/lib/libcblas.a /opt/local/lib/liblapack.a /opt/local/lib/libf77blas.a -lgfortran
./dgemmtest  > ../../../results/mac_os_x-x86_64-dgemm-atlas.csv

gcc-mp-4.8 -O3 dgemmtest.c common.c -o dgemmtest -I../../../../netlib/CBLAS -L/opt/intel/composerxe/mkl/lib -lmkl_rt
export DYLD_LIBRARY_PATH=/opt/intel/composerxe/mkl/lib:/opt/intel/composerxe/lib/
./dgemmtest  > ../../../results/mac_os_x-x86_64-dgemm-mkl.csv

gcc-mp-4.8 -O3 dgemmtest.c cudawrapper.c common.c -o dgemmtest -I../../../../netlib/CBLAS -I/usr/local/cuda/include/ -L/usr/local/cuda/lib -lcublas
export DYLD_LIBRARY_PATH=/usr/local/cuda/lib
./dgemmtest  > ../../../results/mac_os_x-x86_64-dgemm-cuda.csv

CLB=/Users/samuel/Documents/Projects/clBLAS/src
gcc-mp-4.8 -O3 dgemmtest.c clwrapper.c common.c -o dgemmtest -I../../../../netlib/CBLAS -I$CLB -I$CLB/include -L. -lclBLAS  -framework OpenCL
./dgemmtest  > ../../../results/mac_os_x-x86_64-dgemm-clblas.csv

*/

#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <cblas.h>
#include "common.h"

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

long benchmark(int size) {
    int m = sqrt(size);
	long requestStart, requestEnd;

    double* a = random_array(m * m);
    double* b = random_array(m * m);
    double* c = calloc(m * m, sizeof(double));

	requestStart = currentTimeNanos();

    cblas_dgemm(CblasColMajor, CblasNoTrans, CblasNoTrans, m, m, m, 1, a, m, b, m, 0, c, m);

	requestEnd = currentTimeNanos();

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
	srand(time(NULL));

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