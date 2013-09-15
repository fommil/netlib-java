#include <netlib-jni.h>
#include <cblas.h>

// these CBLAS get* helpers are really irritating because
// the first thing the cblas_ methods do is to do a reverse
// lookup for the char and then pass it to the fortran lib!

enum CBLAS_TRANSPOSE getCblasTrans(const char * fortranChar) {
	switch (fortranChar[0]) {
	    case 'N': return CblasNoTrans;
	    case 'n': return CblasNoTrans;
		case 'T': return CblasTrans;
		case 't': return CblasTrans;
		default: return -1;
	}
}

enum CBLAS_UPLO getCblasUpLo(const char * fortranChar) {
	switch (fortranChar[0]) {
	    case 'U': return CblasUpper;
	    case 'u': return CblasUpper;
		case 'L': return CblasLower;
		case 'l': return CblasLower;
		default: return -1;
	}
}

enum CBLAS_SIDE getCblasSide(const char * fortranChar) {
	switch (fortranChar[0]) {
	    case 'L': return CblasLeft;
	    case 'l': return CblasLeft;
		case 'R': return CblasRight;
		case 'r': return CblasRight;
		default: return -1;
	}
}

enum CBLAS_DIAG getCblasDiag(const char * fortranChar) {
	switch (fortranChar[0]) {
	    case 'N': return CblasNonUnit;
	    case 'n': return CblasNonUnit;
		case 'U': return CblasUnit;
		case 'u': return CblasUnit;
		default: return -1;
	}
}

inline void check_memory(JNIEnv * env, void * arg) {
	if (arg != NULL) {
		return;
	}
	/*
	 * WARNING: Memory leak
	 *
	 * This doesn't clean up successful allocations prior to throwing this exception.
	 * However, it's a pretty dire situation to be anyway and the client code is not
	 * expected to recover.
	 */
	(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/OutOfMemoryError"),
		"Out of memory transferring array to native code in F2J JNI");
}

inline int jboolean2int(jboolean b) {
    switch (b) {
        case JNI_TRUE: return 1;
        default: return 0;
    }
}

inline jboolean int2jboolean(int i) {
    switch (i) {
        case 1: return JNI_TRUE;
        default: return JNI_FALSE;
    }
}


int* jbooleanArray2intArray(JNIEnv * env, jboolean * a, jint size) {
	int * j = (int*) malloc(size * sizeof(int));
	check_memory(env, j);
	
	int i;
	for (i = 0 ; i < size ; i++) {
	    j[i] = jboolean2int(a[i]);
	}
	return j;
}

void intArray2jbooleanArray(int * a, jboolean * b, jint size) {
	int i;
	for (i = 0 ; i < size ; i++) {
	    b[i] = int2jboolean(a[i]);
	}
}

