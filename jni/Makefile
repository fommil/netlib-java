include Makefile.incl

all: jniblas jnilapack jniarpack

jniblas: $(LIBPREPEND)jniblas$(LIBAPPEND)

jnilapack: $(LIBPREPEND)jnilapack$(LIBAPPEND)

jniarpack: $(LIBPREPEND)jniarpack$(LIBAPPEND)

$(LIBPREPEND)jniblas$(LIBAPPEND): org_netlib_blas_NativeBLAS.h org_netlib_blas_NativeBLAS.o f2j_jni.o
	$(LD) $(LDFLAGS) $(BLAS_LIBS) org_netlib_blas_NativeBLAS.o f2j_jni.o -o $@

$(LIBPREPEND)jnilapack$(LIBAPPEND): org_netlib_lapack_NativeLAPACK.h org_netlib_lapack_NativeLAPACK.o f2j_jni.o
	$(LD) $(LDFLAGS) $(BLAS_LIBS) $(LAPACK_LIBS) org_netlib_lapack_NativeLAPACK.o f2j_jni.o -o $@

$(LIBPREPEND)jniarpack$(LIBAPPEND): org_netlib_arpack_NativeARPACK.h org_netlib_arpack_NativeARPACK.o f2j_jni.o arpack.a
	$(LD) $(LDFLAGS) $(BLAS_LIBS) $(LAPACK_LIBS) $(FORTRAN_LIBS) org_netlib_arpack_NativeARPACK.o f2j_jni.o arpack.a -o $@

arpack.a:
	rm -f ARPACK/SRC/dnaupe.f 2>/dev/null
	rm -f ARPACK/SRC/snaupe.f 2>/dev/null
	cd ARPACK/UTIL/ ; $(FC) $(FFLAGS) -c *.f
	cd ARPACK/SRC/ ; $(FC) $(FFLAGS) -c *.f
	ar rc arpack.a ARPACK/UTIL/*.o ARPACK/SRC/*.o

%.o: %.c %.h
	$(CC) $(CPPFLAGS) $(LAPACK_INCLUDES) $(CFLAGS) -c $*.c

org_netlib_blas_NativeBLAS.h:
	javah -classpath $(JLAPACK_JNI_CP) -jni org.netlib.blas.NativeBLAS

org_netlib_lapack_NativeLAPACK.h:
	javah -classpath $(JLAPACK_JNI_CP) -jni org.netlib.lapack.NativeLAPACK

org_netlib_arpack_NativeARPACK.h:
	javah -classpath $(JLAPACK_JNI_CP) -jni org.netlib.arpack.NativeARPACK

clean:
	rm -f *.o ARPACK/UTIL/*.o ARPACK/SRC/*.o arpack.a org_*.h

distclean: clean
	rm -f *.dll *.so *.jnilib Makefile.incl
	rm -rf ARPACK
