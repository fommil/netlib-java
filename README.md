netlib-java [![Build Status](https://travis-ci.org/fommil/netlib-java.png?branch=master)](https://travis-ci.org/fommil/netlib-java)
===========

Mission-critical software components for linear algebra systems.

This project is currently undergoing a major (API preserving) rewrite and will be back shortly with easy-to-use native binaries.

If you need access to the old native codebase, look in the `ant` branch.

The TRUNK reference implementation of BLAS (not machine optimised)
is about ten times faster than the Java implementation for vector multiplication.

Some typical results:

* 1000 runs of `dgesvd` takes 401.6 milliseconds with pure Java, reference native takes 41.97 milliseconds.
* 1000 runs of `dsygv` takes 137.2 milliseconds with pure Java, reference native takes 8.9 milliseconds.

The following are some runs with various sized arrays in pure Java (black) and reference native (red) for `ddot` (dot product of vectors):

![native win](http://i39.tinypic.com/rvvbz7.png)

they start to become similar at about 50,000,000 elements.

```
mvn test
mvn test -Dcom.github.fommil.netlib.BLAS=com.github.fommil.netlib.NativeRefBLAS -Dcom.github.fommil.netlib.LAPACK=com.github.fommil.netlib.NativeRefLAPACK

> plot(java, xlab="array size", ylab="time (nanoseconds)", lwd=3)
> lines(java, lwd=3)
> points(native, lwd=3, col="red")
> lines(native, lwd=3, col="red")
```
