#include <jni.h>
#include "f2c.h"
#include <cblas.h>
#include <clapack.h>
#include "arpack.h"
// this will be ignored if a system clapack.h was found first
#include "clapack.h"

// enum CBLAS_ORDER {CblasRowMajor=101, CblasColMajor=102 };
// this is the ordering used by F2J
#define F2J_JNI_ORDER (enum CBLAS_ORDER) 102

// convenience methods for dealing with the CBLAS (not BLAS) specific enums
// our API is using the Fortran-style char*[] system.

enum CBLAS_TRANSPOSE getTrans(const char *);

enum CBLAS_UPLO getUpLo(const char *);

enum CBLAS_SIDE getSide(const char *);

enum CBLAS_DIAG getDiag(const char *);

/* Convenience method for checking if we ran out of memory */
void check_memory(JNIEnv *, void *);
