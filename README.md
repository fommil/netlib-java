netlib-java
===========

`netlib-java` is a wrapper for low-level [BLAS](http://en.wikipedia.org/wiki/Basic_Linear_Algebra_Subprograms),
[LAPACK](http://en.wikipedia.org/wiki/LAPACK) and [ARPACK](http://en.wikipedia.org/wiki/ARPACK)
that performs **as fast as the C / Fortran interfaces**.

If you're a developer looking for an easy-to-use linear algebra library on the JVM, we strongly recommend:

* [Apache Commons Math](http://commons.apache.org/proper/commons-math/) for the most popular mathematics library in Java.
* [Matrix Toolkits for Java](https://github.com/fommil/matrix-toolkits-java/) for high performance linear algebra in Java (builds on top of `netlib-java`).
* [Breeze](https://github.com/scalanlp/breeze) for high performance linear algebra in Scala (builds on top of `netlib-java`).


In `netlib-java`, implementations of BLAS/LAPACK/ARPACK are provided by:

* [F2J](http://icl.cs.utk.edu/f2j/) to ensure full portability on the JVM
* self-contained native builds using the reference Fortran from [netlib.org](http://www.netlib.org)
* delegating builds that use (potentially machine optimised) system libraries

for all major operating systems:

* OS X (`x86_64`)
* Linux (`i686`, `x86_64`, Raspberry Pi `armhf`)
* Windows (32 and 64 bit)

The F2J is the default implementation. Thanks to [JNILoader](https://github.com/fommil/jniloader),
enabling reference natives is as simple as setting system properties on JVM startup:

* `-Dcom.github.fommil.netlib.BLAS=com.github.fommil.netlib.NativeRefBLAS`
* `-Dcom.github.fommil.netlib.LAPACK=com.github.fommil.netlib.NativeRefLAPACK`
* `-Dcom.github.fommil.netlib.ARPACK=com.github.fommil.netlib.NativeRefARPACK`

*Linux natives require the system library `libgfortran3` to be installed.*

Machine optimised libraries require a little more setup, see below.

If the natives fail to load, the Java implementation is the fallback.


Machine Optimised Natives
=========================

High performance BLAS / LAPACK are available
[commercially and open source](http://en.wikipedia.org/wiki/Basic_Linear_Algebra_Subprograms#Implementations)
for specific CPU chipsets. It is worth noting that "optimised" here means a lot more than simply changing
the compiler optimisation flags: specialist assembly instructions are combined with [compile time profiling](http://en.wikipedia.org/wiki/Automatically_Tuned_Linear_Algebra_Software#Optimization_approach)
and the [selection of array alignments for the kernel and CPU combination](http://en.wikipedia.org/wiki/Automatically_Tuned_Linear_Algebra_Software#Can_it_afford_to_copy.3F).

Due to the nature of machine-optimised binaries, we cannot bundle them with `netlib-java`:
commercial licenses prohibit distribution and open source *tuning* only occurs when
compiled on the target machine.

There are two ways to use machine-optimised natives with `netlib-java`:

1. make `libblas` (CBLAS) and `liblapack` (Fortran) available to the `native_system` implementation.
2. build a custom backend (using `native_ref` and `native_system` as inspiration).

[The author](https://github.com/fommil/) may be available to assist with custom builds (and further
improvements to `netlib-java`) on a commercial basis. Make contact for availability (budget estimates
are appreciated).


The `native_system` implementation is enabled with the following (requires at least `1.1-SNAPSHOT`):

* `-Dcom.github.fommil.netlib.BLAS=com.github.fommil.netlib.NativeSystemBLAS`
* `-Dcom.github.fommil.netlib.LAPACK=com.github.fommil.netlib.NativeSystemLAPACK`
* `-Dcom.github.fommil.netlib.ARPACK=com.github.fommil.netlib.NativeSystemARPACK`

plus the following Operating System specific instructions.


OS X
----

Apple OS X requires no further setup because OS X ships with the [veclib framework](https://developer.apple.com/documentation/Performance/Conceptual/vecLib/),
boasting incredible performance that is difficult to surpass (performance charts below
show that it out-performs ATLAS and is on par with the Intel MKL).

Unfortunately, although [cuBLAS](https://developer.nvidia.com/cublas) implements a form
of the BLAS interface, some "genius" decided to re-design the API with trivial changes
that require *source code modifications* (hopefully somebody will write a CBLAS compatibility layer).
Seriously NVIDIA, **epic fail** guys!

The future of GPU BLAS appears to be in the AMD-sponsored, open source,
[clBLAS](https://github.com/clMathLibraries/clBLAS). It looks like
[some work needs to be done for OS X](https://github.com/clMathLibraries/clBLAS/issues/7).

Linux
-----

(includes Raspberry Pi)

Generically-tuned ATLAS and OpenBLAS are available with most distributions (e.g. [Debian](https://wiki.debian.org/DebianScience/LinearAlgebraLibraries)) and must be enabled
explicitly using the package-manager. e.g. for Debian / Ubuntu one would type

    sudo apt-get install libatlas3-base libopenblas-base
    sudo update-alternatives --config libblas.so.3
    sudo update-alternatives --config liblapack.so.3

selecting the preferred implementation.

However, these are only generic pre-tuned builds. To get optimal performance for a specific
machine, it is best to compile locally by grabbing the [latest ATLAS](http://sourceforge.net/projects/math-atlas/files/latest/download) or the [latest OpenBLAS](https://github.com/xianyi/OpenBLAS/archive/master.zip) and following the compilation
instructions (don't forget to turn off CPU throttling and power management during the build!).
Install the shared libraries into a folder that is seen by the runtime linker (e.g. add your install
folder to `/etc/ld.so.conf` then run `ldconfig`) ensuring that `libblas.so.3` and `liblapack.so.3`
exist and point to your optimal builds.

If you have an [Intel MKL](http://software.intel.com/en-us/intel-mkl) licence, you could also
try creating symbolic links from `libblas.so.3` and `liblapack.so.3` to `libmkl_rt.so`
(and let us know how you get on), and the equivalents for other vendor-supplied libraries.

*NOTE: Some distributions, such as Ubuntu `precise` do not create the necessary symbolic links
`/usr/lib/libblas.so.3` and `/usr/lib/liblapack.so.3` for the system-installed implementations,
so they must be created manually.*

Windows
-------

The `native_system` builds expect to find `libblas3.dll` and `liblapack3.dll` on the `%PATH%`
(or current working directory).
Either install the generically tuned
[OpenBLAS binaries](http://sourceforge.net/projects/openblas/files/),
obtain vendor-supplied libraries (Intel, AMD, NVIDIA), or
compile and install your own machine-optimised OpenBLAS /
[ATLAS](http://math-atlas.sourceforge.net/atlas_install/node54.html).

Use [Dependency Walker](http://www.dependencywalker.com) to help resolve any

*NOTE: OpenBLAS [doesn't provide separate libraries](https://github.com/xianyi/OpenBLAS/issues/296)
so you will have to customise the build or copy the binary into both `libblas3.dll` and
`liblapack3.dll` whilst also obtaining a copy of `libgfortran-1-3.dll`, `libquadmath-0.dll` and
`libgcc_s_seh-1.dll` from [MinGW](http://www.mingw.org).*


Performance
===========

Java has a reputation with older generation developers because
Java applications were slow in the 1990s.
Nowadays, the [JIT](http://en.wikipedia.org/wiki/Just-in-time_compilation)
ensures that Java applications keep pace with – or exceed the performance of –
C / C++ / Fortran applications.

The following performance charts give an idea of the performance ratios of Java vs the native
implementations. Also shown are pure C performance runs that show that
**dropping to C at the application layer gives no performance benefit**.
If anything, the Java version is faster for smaller matrices and is consistently faster
than the "optimised" implementations for some types of operations (e.g. `ddot`).

One can expect machine-optimised natives to out-perform the reference implementation
– especially for larger arrays – as demonstrated below by Apple's
[veclib framework](https://developer.apple.com/library/mac/documentation/Performance/Conceptual/vecLib/Reference/reference.html),
Intel's [MKL](http://software.intel.com/en-us/intel-mkl) and (to a lesser extent)
[ATLAS](https://sourceforge.net/projects/math-atlas/).

Of particular note is the [cuBLAS](https://developer.nvidia.com/cublas) which appears to have
constant time evaluation of arrays up to 1 million entries. Although this means that cuBLAs
is rather slow for smaller matrices, it is hundreds (and possibly thousands to millions) of
times faster for larger arrays.

*NOTE: a different machine is used for each OS: Macbook Air for OS X, Debian 64bit and Ubuntu 32 bit;
Raspberry Pi for ARM; and iMac for Windows 8. Raspberry Pi results are truncated because I didn't want
to wait around all day.*

The [DGEMM](http://www.netlib.no/netlib/lapack/double/dgemm.f) benchmark
measures [matrix multiplication](http://en.wikipedia.org/wiki/General_Matrix_Multiply)
performance:

![dgemm](http://i752.photobucket.com/albums/xx162/fommil/dgemm_zpsd831b476.png)

The [DGETRI](http://www.netlib.no/netlib/lapack/double/dgetri.f) benchmark
measures matrix [LU Factorisation](http://en.wikipedia.org/wiki/LU_decomposition)
and [matrix inversion](http://mathworld.wolfram.com/MatrixInverse.html) performance:

![dgetri](http://i752.photobucket.com/albums/xx162/fommil/dgetri_zpsf883006e.png)

The [DDOT](http://www.netlib.no/netlib/blas/ddot.f) benchmark measures
[vector dot product](http://en.wikipedia.org/wiki/Dot_product) performance:

![ddot](http://i752.photobucket.com/albums/xx162/fommil/ddot_zpsb1a4a7f1.png)


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

