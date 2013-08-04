#include <jni.h>
#include <cblas.h>

// these CBLAS get* helpers are really irritating because
// the first thing the cblas_ methods do is to do a reverse
// lookup for the char and then pass it to the fortran lib!

enum CBLAS_TRANSPOSE getCblasTrans(const char * fortranChar) {
	switch (fortranChar[0]) {
	    case 'N': return CblasNoTrans;
		case 'T': return CblasTrans;
		default: return -1;
	}
}

enum CBLAS_UPLO getCblasUpLo(const char * fortranChar) {
	switch (fortranChar[0]) {
	    case 'U': return CblasUpper;
		case 'L': return CblasLower;
		default: return -1;
	}
}

enum CBLAS_SIDE getCblasSide(const char * fortranChar) {
	switch (fortranChar[0]) {
	    case 'L': return CblasLeft;
		case 'R': return CblasRight;
		default: return -1;
	}
}

enum CBLAS_DIAG getCblasDiag(const char * fortranChar) {
	switch (fortranChar[0]) {
	    case 'N': return CblasNonUnit;
		case 'U': return CblasUnit;
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