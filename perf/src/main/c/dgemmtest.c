/*

gcc -O3 dgemmtest.c -o dgemmtest -L. -lnetlib -I../../../../netlib/CBLAS
./dgemmtest  > ../../../results/mac_os_x-x86_64-dgemm-CBLAS.csv

gcc -O3 dgemmtest.c -o dgemmtest -I/System/Library/Frameworks/vecLib.framework/Headers -framework veclib
./dgemmtest  > ../../../results/mac_os_x-x86_64-dgemm-veclib.csv


*/

#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <cblas.h>


#include <time.h>
#include <sys/time.h>

// http://stackoverflow.com/questions/5167269
#ifdef __MACH__
#include <mach/clock.h>
#include <mach/mach.h>
#endif


// http://stackoverflow.com/questions/7034930
double drand() {
  return (rand()+1.0)/(RAND_MAX+1.0);
}

double random_normal()  {
  return sqrt(-2*log(drand())) * cos(2*M_PI*drand());
}

double* random_array(int size) {
	double *a = malloc(sizeof(double) * size);
	int i;
	
	for (i = 0 ; i < size; i++) {
		a[i] = random_normal();
	}
	
	return a;
}

long currentTime() {
struct timespec ts;

#ifdef __MACH__
clock_serv_t cclock;
mach_timespec_t mts;
host_get_clock_service(mach_host_self(), CALENDAR_CLOCK, &cclock);
clock_get_time(cclock, &mts);
mach_port_deallocate(mach_task_self(), cclock);
ts.tv_sec = mts.tv_sec;
ts.tv_nsec = mts.tv_nsec;

#else
clock_gettime(CLOCK_REALTIME, &ts);
#endif

return ts.tv_sec + ts.tv_nsec;
}

long benchmark(int size) {
    int m = sqrt(size);
	long requestStart, requestEnd;

    double* a = random_array(m * m);
    double* b = random_array(m * m);
    double* c = calloc(m * m, sizeof(double));

	requestStart = currentTime();

    cblas_dgemm(CblasColMajor, CblasNoTrans, CblasNoTrans, m, m, m, 1, a, m, b, m, 0, c, m);

	requestEnd = currentTime();

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
        }
    }
}