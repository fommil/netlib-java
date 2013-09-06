/*

cp ../../../../native_ref/<target binary> libnetlib.so

gcc-mp-4.8 -O3 dgetritest.c common.c -o dgetritest -L. -lnetlib -I../../../../netlib/CBLAS -I../../../../netlib/LAPACKE
./dgetritest  > ../../../results/mac_os_x-x86_64-dgetri-CBLAS.csv

gcc-mp-4.8 -O3 dgetritest.c common.c -o dgetritest -I/System/Library/Frameworks/vecLib.framework/Headers -framework veclib
./dgetritest  > ../../../results/mac_os_x-x86_64-dgetri-veclib.csv

gcc-mp-4.8 -O3 dgetritest.c common.c -o dgetritest -I/opt/local/include /opt/local/lib/libatlas.a /opt/local/lib/libcblas.a /opt/local/lib/liblapack.a /opt/local/lib/libf77blas.a -lgfortran
./dgetritest  > ../../../results/mac_os_x-x86_64-dgetri-atlas.csv

gcc-mp-4.8 -O3 dgetritest.c common.c -o dgetritest -I../../../../netlib/CBLAS -L/opt/intel/composerxe/mkl/lib -lmkl_rt
export DYLD_LIBRARY_PATH=/opt/intel/composerxe/mkl/lib:/opt/intel/composerxe/lib/
./dgetritest  > ../../../results/mac_os_x-x86_64-dgetri-mkl.csv

*/

#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include "common.h"

long benchmark(int size) {
    int m = sqrt(size);
	long requestStart, requestEnd;

    // random matrices are full rank (and can always be inverted if square)
    // http://www.sciencedirect.com/science/article/pii/S0096300306009040
    double* a = random_array(m * m);
    int bSize = m * m;
    double* b = calloc(bSize, sizeof(double));
    int* p = calloc(m, sizeof(int));
    int info = 0;

	requestStart = currentTimeNanos();

    // calling raw fortran because OS X doesn't have LAPACKE
    dgetrf_( &m, &m, a, &m, p, &info );
    dgetri_( &m, a, &m, p, b, &bSize, &info );

	requestEnd = currentTimeNanos();

    free(a);
    free(b);
    free(p);

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
        }
    }
}