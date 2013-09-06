/*

cp ../../../../native_ref/<target binary> libnetlib.so

gcc-mp-4.8 -O3 ddottest.c common.c -o ddottest -L. -lnetlib -I../../../../netlib/CBLAS
./ddottest  > ../../../results/mac_os_x-x86_64-ddot-CBLAS.csv

gcc-mp-4.8 -O3 ddottest.c common.c -o ddottest -I/System/Library/Frameworks/vecLib.framework/Headers -framework veclib
./ddottest  > ../../../results/mac_os_x-x86_64-ddot-veclib.csv

gcc-mp-4.8 -O3 ddottest.c common.c -o ddottest -I/opt/local/include /opt/local/lib/libatlas.a /opt/local/lib/libcblas.a /opt/local/lib/liblapack.a /opt/local/lib/libf77blas.a -lgfortran
./ddottest  > ../../../results/mac_os_x-x86_64-ddot-atlas.csv

gcc-mp-4.8 -O3 ddottest.c common.c -o ddottest -I../../../../netlib/CBLAS -L/opt/intel/composerxe/mkl/lib -lmkl_rt
export DYLD_LIBRARY_PATH=/opt/intel/composerxe/mkl/lib:/opt/intel/composerxe/lib/
./ddottest  > ../../../results/mac_os_x-x86_64-ddot-mkl.csv

gcc-mp-4.8 -O3 ddottest.c cudawrapper.c common.c -o ddottest -I../../../../netlib/CBLAS -I/usr/local/cuda/include/ -L/usr/local/cuda/lib -lcublas
export DYLD_LIBRARY_PATH=/usr/local/cuda/lib
./ddottest  > ../../../results/mac_os_x-x86_64-ddot-cuda.csv



*/

#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <cblas.h>
#include "common.h"

long benchmark(int size) {
	long requestStart, requestEnd;

    double* a = random_array(size);
    double* b = random_array(size);

	requestStart = currentTimeNanos();

    cblas_ddot(size, a, 1, b, 1);

	requestEnd = currentTimeNanos();

    free(a);
    free(b);

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