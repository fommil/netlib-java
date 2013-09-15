#include <stdlib.h>
#include <jni.h>

typedef jobject jfloatw;
typedef jobject jdoublew;
typedef jobject jintw;
typedef jobject jstringw;
typedef jobject jbooleanw;


// convenience methods for dealing with the CBLAS (not BLAS) specific enums
// our API is using the Fortran-style char* system.
enum CBLAS_TRANSPOSE getCblasTrans(const char *);
enum CBLAS_UPLO getCblasUpLo(const char *);
enum CBLAS_SIDE getCblasSide(const char *);
enum CBLAS_DIAG getCblasDiag(const char *);


/* Convenience for checking if we ran out of memory */
void check_memory(JNIEnv *, void *);


/* Convenience for converting between jboolean (Java Boolean) and int (Fortran LOGICAL). */
int jboolean2int(jboolean b);
jboolean int2jboolean(int i);
int* jbooleanArray2intArray(JNIEnv * env, jboolean * a, jint size);
void intArray2jbooleanArray(int * a, jboolean * b, jint size);
