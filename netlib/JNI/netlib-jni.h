#include <jni.h>

typedef jobject jfloatw;
typedef jobject jdoublew;
typedef jobject jintw;

// convenience methods for dealing with the CBLAS (not BLAS) specific enums
// our API is using the Fortran-style char* system.

enum CBLAS_TRANSPOSE getCblasTrans(const char *);

enum CBLAS_UPLO getCblasUpLo(const char *);

enum CBLAS_SIDE getCblasSide(const char *);

enum CBLAS_DIAG getCblasDiag(const char *);


/* Convenience method for checking if we ran out of memory */
void check_memory(JNIEnv *, void *);
