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


/* Convenience for converting between jboolean and jint representations of booleans */
jint jboolean2jint(jboolean b);
jboolean jint2jboolean(jint i);
jint* jbooleanArray2jintArray(JNIEnv * env, jboolean * a, jint size);
void jintArray2jbooleanArray(JNIEnv * env, jint * a, jboolean * b, jint size);