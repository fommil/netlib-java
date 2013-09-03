netlib-java
===========

Mission-critical components for linear algebra systems.

`netlib-java` is a wrapper for low-level [BLAS](http://en.wikipedia.org/wiki/Basic_Linear_Algebra_Subprograms),
[LAPACK](http://en.wikipedia.org/wiki/LAPACK) and [ARPACK](http://en.wikipedia.org/wiki/ARPACK)
that performs **as fast as the C / Fortran interfaces**.

If you're a developer looking for an easy-to-use linear algebra library on the JVM, we strongly recommend:

* [Apache Commons Math](http://commons.apache.org/proper/commons-math/) for the most popular mathematics library in Java.
* [Matrix Toolkits for Java](https://github.com/fommil/matrix-toolkits-java/) for high performance linear algebra in Java (builds on top of `netlib-java`).
* [Breeze](https://github.com/scalanlp/breeze) for high performance linear algebra in Scala (builds on top of `netlib-java`).


In `netlib-java`, pure Java implementations of BLAS/LAPACK/ARPACK are provided by [F2J](http://icl.cs.utk.edu/f2j/) to ensure full portability, with native reference builds (using the Fortran code from [netlib.org](http://www.netlib.org))
shipped as standard for all major operating systems:

* OS X (`x86_64`)
* Linux (`i686`, `x86_64`, Raspberry Pi (`armhf`): `GLIBC_2.2.5+` and `libgfortran3`)
* Windows (32 and 64 bit)

Native loading is provided by [JNILoader](https://github.com/fommil/jniloader): disabled by default.
Enabling reference natives is as simple as setting system properties on JVM startup:

* `-Dcom.github.fommil.netlib.BLAS=com.github.fommil.netlib.NativeRefBLAS`
* `-Dcom.github.fommil.netlib.LAPACK=com.github.fommil.netlib.NativeRefLAPACK`
* `-Dcom.github.fommil.netlib.ARPACK=com.github.fommil.netlib.NativeRefARPACK`

If the natives fail to load, the Java implementation is the fallback.

*Improvements to startup time (tens to hundreds of milliseconds) can be achieved by providing the exact binary name to load,
e.g. on OS X `-Dcom.github.fommil.netlib.NativeRefBLAS.natives=netlib-native_ref-osx-x86_64.jnilib`,
and placing the file in a directory which is passed as `-Djava.library.path=...`.*


Machine Optimised Natives
=========================

High performance BLAS / LAPACK are available
[commercially](http://en.wikipedia.org/wiki/Basic_Linear_Algebra_Subprograms#Implementations)
and [open source](http://en.wikipedia.org/wiki/Automatically_Tuned_Linear_Algebra_Software) for
specific CPU chipsets.

Due to the nature of machine-optimised binaries, we cannot bundle them with `netlib-java`.

However, we have made it as simple as possible (without sacrificing performance)
for developers to use existing optimised implementations:
the primary goal of the `native_ref` module is to show how this can be achieved.

[The author](https://github.com/fommil/) may be available to assist with builds (and further
improvements to `netlib-java`) on a commercial basis. Make contact for availability (budget estimates
are appreciated).


Performance
===========

Java has a reputation with older generation developers because
Java applications were slow in the 1990s.
Nowadays, the [JIT](http://en.wikipedia.org/wiki/Just-in-time_compilation)
ensures that Java applications keep pace with – or exceed the performance of –
C / C++ / Fortran applications.

The following performance charts give an idea of the performance ratios of Java vs the native
reference implementation. Also shown are pure C performance runs that show that
**dropping to C at the application layer gives no performance benefit**.
If anything, the Java version is faster for smaller matrices and is consistently faster
than the "optimised" implementations for some types of operations (e.g. `ddot`).

One should expect machine optimised natives to out-perform the reference implementation
– especially for larger arrays – as demonstrated below by Apple's
[veclib framework](https://developer.apple.com/library/mac/documentation/Performance/Conceptual/vecLib/Reference/reference.html),
Intel's [MKL](http://software.intel.com/en-us/intel-mkl) and (to a lesser extent)
[ATLAS](https://sourceforge.net/projects/math-atlas/).

*NOTE: a different machine is used for each OS: Macbook Air for OS X, Debian 64bit and Ubuntu 32 bit;
Raspberry Pi for ARM; and iMac for Windows 8. Raspberry Pi results are truncated because I didn't want
to wait around all day.*

The [DGEMM](http://www.netlib.no/netlib/lapack/double/dgemm.f) benchmark
measures [matrix multiplication](http://en.wikipedia.org/wiki/General_Matrix_Multiply)
performance:

![dgemm](http://i752.photobucket.com/albums/xx162/fommil/dgemm_zpsa272550e.png)

The [DGETRI](http://www.netlib.no/netlib/lapack/double/dgetri.f) benchmark
measures matrix [LU Factorisation](http://en.wikipedia.org/wiki/LU_decomposition)
and [matrix inversion](http://mathworld.wolfram.com/MatrixInverse.html) performance:

![dgetri](http://i752.photobucket.com/albums/xx162/fommil/dgetri_zpsca6e1ada.png)

The [DDOT](http://www.netlib.no/netlib/blas/ddot.f) benchmark measures
[vector dot product](http://en.wikipedia.org/wiki/Dot_product) performance:

![ddot](http://i752.photobucket.com/albums/xx162/fommil/ddot_zps25486a2e.png)


The following benchmark, [LINPACK](http://www.netlib.org/linpack), shows the performance of
Java vs reference native implementations of BLAS. Note that the Java implementation is about 10 times
faster (than its start speed) by about the 20th iteration (that's the JIT kicking in).

![linpack](http://i752.photobucket.com/albums/xx162/fommil/linpack_zps8fdf763b.png)


Installation
============

Releases are distributed on Maven central:

```xml
<dependency>
  <groupId>com.github.fommil.netlib</groupId>
  <artifactId>all</artifactId>
  <version>1.0</version>
  <type>pom</type>
</dependency>
```

SBT developers can use

```scala
"com.github.fommil.netlib" % "all" % "1.0" pomOnly()
```

Those wanting to preserve the pre-1.0 API can use the legacy package (but
note that it **will** be removed in the next release):

```xml
<dependency>
  <groupId>com.googlecode.netlib-java</groupId>
  <artifactId>netlib</artifactId>
  <version>1.0</version>
</dependency>
```

and developers who feel the native libs are too much bandwidth can
depend on a subset of implementations: simply look in the `all`
module's [`pom.xml`](all/pom.xml).


Snapshots are distributed on Sonatype's Snapshot Repository, e.g.:

```xml
<dependency>
  <groupId>com.github.fommil.netlib</groupId>
  <artifactId>all</artifactId>
  <version>1.1-SNAPSHOT</version>
</dependency>
```

If the above fails, ensure you have the following in your `pom.xml`:

```xml
    <repositories>
        <repository>
            <id>sonatype-snapshots</id>
            <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
            <releases>
                <enabled>false</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
```



Donations
=========

Please consider supporting the maintenance of this open source project with a donation:

[![Donate via Paypal](https://www.paypal.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=B2HW5ATB8C3QW&lc=GB&item_name=netlib&currency_code=GBP&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted)


Contributing
============

Contributors are encouraged to fork this repository and issue pull
requests. Contributors implicitly agree to assign an unrestricted licence
to Sam Halliday, but retain the copyright of their code (this means
we both have the freedom to update the licence for those contributions).

