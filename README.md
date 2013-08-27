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

The following are 10 runs each of various sized arrays in pure Java (black) and reference native (red) for `ddot` (dot product of vectors):

![native vs java](http://i43.tinypic.com/2dr5gew.png)

Amazingly, Pure Java starts to get as fast as the Fortran code for arrays of about 1,000 elements (and higher).

```sh
mvn test | grep -x '[0-9]*,[0-9]*' > ~/java.csv
mvn test -Dcom.github.fommil.netlib.BLAS=com.github.fommil.netlib.NativeRefBLAS -Dcom.github.fommil.netlib.LAPACK=com.github.fommil.netlib.NativeRefLAPACK | grep -x '[0-9]*,[0-9]*' > ~/native_ref.csv
```

```R
png("out.png", width=800, height=800)
par(cex = 1.5, cex.lab=1.5, cex.axis=1.5, cex.main=1.5, cex.sub=1.5, family="Palatino")
java = read.csv("~/java.csv")
native = read.csv("~/native_ref.csv")
# 10^9 => nanoseconds and 10 repeats
java[,2] = java[,2] / 10000000000
native[,2] = native[,2] / 10000000000
ylim = c(min(java[,2], native[,2]), max(java[,2], native[,2]))
plot(java, xlab="Array size", ylab="Time (seconds)", log="xy", lwd=2, pch=4, xaxt="n", yaxt="n", ylim=ylim, main="ddot Performance")
lines(java, lwd=2)
points(native, lwd=2, col="red", pch=4)
lines(native, lwd=2, col="red")

x1 <- floor(log10(range(java[,1])))
pow <- seq(x1[1], x1[2]+1)
ticksat <- as.vector(sapply(pow, function(p) (1:10)*10^p))
axis(1, 10^pow)
axis(1, ticksat, labels=NA, tcl=-0.25, lwd=0, lwd.ticks=1)

y1 <- floor(log10(range(java[,2])))
pow <- seq(y1[1], y1[2]+1)
ticksat <- as.vector(sapply(pow, function(p) (1:10)*10^p))
axis(2, 10^pow)
axis(2, ticksat, labels=NA, tcl=-0.25, lwd=0, lwd.ticks=1)

legend("topleft", c("F2J","Reference Netlib"), lty=c(1,1), lwd=c(2,2), col=c("black","red"), bty="n")
dev.off()
```
