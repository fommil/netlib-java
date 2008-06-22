#include <jni.h>
#include <cblas.h>

enum CBLAS_TRANSPOSE getTrans(const char * fortranChar){
	// enum CBLAS_TRANSPOSE {CblasNoTrans=111, CblasTrans=112, CblasConjTrans=113, AtlasConj=114};
	// can be "N" or "T"
	if (fortranChar[0] == 'N'){
		return (enum CBLAS_TRANSPOSE) 111;
	}
	if (fortranChar[0] == 'T'){
		return (enum CBLAS_TRANSPOSE) 112;
	}
	printf("ERROR in F2J JNI: getTrans() got %s", fortranChar);
	return -1;
}

enum CBLAS_UPLO getUpLo(const char * fortranChar){
	// enum CBLAS_UPLO  {CblasUpper=121, CblasLower=122};
	// can be "U" or "L"
	if (fortranChar[0] == 'U'){
		return (enum CBLAS_UPLO) 121;
	}
	if (fortranChar[0] == 'L'){
		return (enum CBLAS_UPLO) 122;
	}
	printf("ERROR in F2J JNI: getUpLo() got %s", fortranChar);
	return -1;
}

enum CBLAS_SIDE getSide(const char * fortranChar){
	// enum CBLAS_SIDE  {CblasLeft=141, CblasRight=142};
	// can be "L" or "R"
	if (fortranChar[0] == 'L'){
		return (enum CBLAS_SIDE) 141;
	}
	if (fortranChar[0] == 'R'){
		return (enum CBLAS_SIDE) 142;
	}
	printf("ERROR in F2J JNI: getSide() got %s", fortranChar);
	return -1;
}

enum CBLAS_DIAG getDiag(const char * fortranChar){
	// enum CBLAS_DIAG  {CblasNonUnit=131, CblasUnit=132};
	// can be "U" or "N"
	if (fortranChar[0] == 'N'){
		return (enum CBLAS_DIAG) 131;
	}
	if (fortranChar[0] == 'U'){
		return (enum CBLAS_DIAG) 132;
	}
	printf("ERROR in F2J JNI: getDiag() got %s", fortranChar);
	return -1;
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