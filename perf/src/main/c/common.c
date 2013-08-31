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

long currentTimeNanos() {
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

return (ts.tv_sec * 1000000000) + ts.tv_nsec;
}
