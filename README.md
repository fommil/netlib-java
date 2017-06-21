netlib-java
===========

**If you require support or wish to ensure the continuation of this library, you must get your company to respond to the [Call For Funding](https://github.com/fommil/netlib-java/wiki/CallForFunding). I do not have the inclination to provide gratis assistance.**

`netlib-java` is a wrapper for low-level [BLAS](http://en.wikipedia.org/wiki/Basic_Linear_Algebra_Subprograms),
[LAPACK](http://en.wikipedia.org/wiki/LAPACK) and [ARPACK](http://en.wikipedia.org/wiki/ARPACK)
that performs **as fast as the C / Fortran interfaces** with a pure JVM fallback. `netlib-java` is included with recent versions of [Apache Spark](https://spark.apache.org/).

For more details on high performance linear algebra on the JVM, please watch [my talk at Scala eXchange 2014](https://skillsmatter.com/skillscasts/5849-high-performance-linear-algebra-in-scala) ([follow along with high-res slides](http://fommil.github.io/scalax14/#/)).

If you're a developer looking for an easy-to-use linear algebra library on the JVM, we strongly recommend Commons-Math, MTJ and Breeze:

* [Apache Commons Math](http://commons.apache.org/proper/commons-math/) for the most popular mathematics library in Java ([not using `netlib-java`](https://issues.apache.org/jira/browse/MATH-270)).
* [Matrix Toolkits for Java](https://github.com/fommil/matrix-toolkits-java/) for high performance linear algebra in Java (builds on top of `netlib-java`).
* [Breeze](https://github.com/scalanlp/breeze) for high performance linear algebra in Scala and Spark (builds on top of `netlib-java`).


In `netlib-java`, implementations of BLAS/LAPACK/ARPACK are provided by:

* delegating builds that use machine optimised system libraries (see below)
* self-contained native builds using the reference Fortran from [netlib.org](http://www.netlib.org)
* [F2J](http://icl.cs.utk.edu/f2j/) to ensure full portability on the JVM

The [JNILoader](https://github.com/fommil/jniloader) will attempt to load the implementations in this order automatically.

All major operating systems are supported out-of-the-box:

* OS X (`x86_64`)
* Linux (`i686`, `x86_64`, Raspberry Pi `armhf`) (**must have `libgfortran3` installed**)
* Windows (32 and 64 bit)


Machine Optimised System Libraries
==================================

High performance BLAS / LAPACK are available
[commercially and open source](http://en.wikipedia.org/wiki/Basic_Linear_Algebra_Subprograms#Implementations)
for specific CPU chipsets. It is worth noting that "optimised" here means a lot more than simply changing
the compiler optimisation flags: specialist assembly instructions are combined with [compile time profiling](http://en.wikipedia.org/wiki/Automatically_Tuned_Linear_Algebra_Software#Optimization_approach)
and the [selection of array alignments for the kernel and CPU combination](http://en.wikipedia.org/wiki/Automatically_Tuned_Linear_Algebra_Software#Can_it_afford_to_copy.3F).

An alternative to optimised libraries is to use the GPU:
e.g. [cuBLAS](https://developer.nvidia.com/cublas) or [clBLAS](https://github.com/clMathLibraries/clBLAS).
Setting up cuBLAS must be done via [our NVBLAS instructions](https://github.com/fommil/netlib-java/wiki/NVBLAS), since cuBLAS does not implement the actual BLAS API out of the box.

Be aware that GPU implementations have severe performance degradation for small arrays.
[MultiBLAS](https://github.com/fommil/multiblas) is an initiative to work around
the limitation of GPU BLAS implementations by selecting the optimal implementation
at runtime, based on the array size.

**To enable machine optimised natives in `netlib-java`, end-users make their machine-optimised `libblas3` (CBLAS) and
`liblapack3` (Fortran) available as shared libraries at runtime.**

If it is not possible to provide a shared library, [the author](https://github.com/fommil/) may be available
to assist with custom builds (and further improvements to `netlib-java`) on a commercial basis.
Make contact for availability (budget estimates are appreciated).

OS X
----

Apple OS X requires no further setup because OS X ships with the [veclib framework](https://developer.apple.com/documentation/Performance/Conceptual/vecLib/),
boasting incredible CPU performance that is difficult to surpass (performance charts below
show that it out-performs ATLAS and is on par with the Intel MKL).


Linux
-----

(includes Raspberry Pi)

Generically-tuned ATLAS and OpenBLAS are available with most distributions (e.g. [Debian](https://wiki.debian.org/DebianScience/LinearAlgebraLibraries)) and must be enabled
explicitly using the package-manager. e.g. for Debian / Ubuntu one would type

    sudo apt-get install libatlas3-base libopenblas-base
    sudo update-alternatives --config libblas.so
    sudo update-alternatives --config libblas.so.3
    sudo update-alternatives --config liblapack.so
    sudo update-alternatives --config liblapack.so.3

selecting the preferred implementation.

However, these are only generic pre-tuned builds. To get optimal performance for a specific
machine, it is best to compile locally by grabbing the [latest ATLAS](http://sourceforge.net/projects/math-atlas/files/latest/download) or the [latest OpenBLAS](https://github.com/xianyi/OpenBLAS/archive/master.zip) and following the compilation
instructions (don't forget to turn off CPU throttling and power management during the build!).
Install the shared libraries into a folder that is seen by the runtime linker (e.g. add your install
folder to `/etc/ld.so.conf` then run `ldconfig`) ensuring that `libblas.so.3` and `liblapack.so.3`
exist and point to your optimal builds.

If you have an [Intel MKL](http://software.intel.com/en-us/intel-mkl) licence, you could also
create symbolic links from `libblas.so.3` and `liblapack.so.3` to `libmkl_rt.so` or use
Debian's alternatives system:

```
sudo update-alternatives --install /usr/lib/libblas.so     libblas.so     /opt/intel/mkl/lib/intel64/libmkl_rt.so 1000
sudo update-alternatives --install /usr/lib/libblas.so.3   libblas.so.3   /opt/intel/mkl/lib/intel64/libmkl_rt.so 1000
sudo update-alternatives --install /usr/lib/liblapack.so   liblapack.so   /opt/intel/mkl/lib/intel64/libmkl_rt.so 1000
sudo update-alternatives --install /usr/lib/liblapack.so.3 liblapack.so.3 /opt/intel/mkl/lib/intel64/libmkl_rt.so 1000
```

and don't forget to add the MKL libraries to your `/etc/ld.so.conf`
file (and run `sudo ldconfig`), e.g. add

```
/opt/intel/lib/intel64
/opt/intel/mkl/lib/intel64
```

*NOTE: Some distributions, such as Ubuntu `precise` do not create the necessary symbolic links
`/usr/lib/libblas.so.3` and `/usr/lib/liblapack.so.3` for the system-installed implementations,
so they must be created manually.*

Windows
-------

The `native_system` builds expect to find `libblas3.dll` and `liblapack3.dll` on the `%PATH%`
(or current working directory).
Besides vendor-supplied implementations,
OpenBLAS provide [generically tuned binaries](http://sourceforge.net/projects/openblas/files/),
and it is possible to build
[ATLAS](http://math-atlas.sourceforge.net/atlas_install/node54.html).

Use [Dependency Walker](http://www.dependencywalker.com) to help resolve any problems such as:
`UnsatisfiedLinkError (Can't find dependent libraries)`.

*NOTE: OpenBLAS [doesn't provide separate libraries](https://github.com/xianyi/OpenBLAS/issues/296)
so you will have to customise the build or copy the binary into both `libblas3.dll` and
`liblapack3.dll` whilst also obtaining a copy of `libgfortran-1-3.dll`, `libquadmath-0.dll` and
`libgcc_s_seh-1.dll` from [MinGW](http://www.mingw.org).*


Customisation
=============

A specific implementation may be forced like so:

* `-Dcom.github.fommil.netlib.BLAS=com.github.fommil.netlib.NativeRefBLAS`
* `-Dcom.github.fommil.netlib.LAPACK=com.github.fommil.netlib.NativeRefLAPACK`
* `-Dcom.github.fommil.netlib.ARPACK=com.github.fommil.netlib.NativeRefARPACK`

A specific (non-standard) JNI binary may be forced like so:

* `-Dcom.github.fommil.netlib.NativeSystemBLAS.natives=netlib-native_system-myos-myarch.so`

(note that this is **not** your `libblas.so.3` or `liblapack.so.3`, it is the `netlib-java` native wrapper component which automatically detects and loads your system's libraries).

To turn off natives altogether, add these to the JVM flags:

* `-Dcom.github.fommil.netlib.BLAS=com.github.fommil.netlib.F2jBLAS`
* `-Dcom.github.fommil.netlib.LAPACK=com.github.fommil.netlib.F2jLAPACK`
* `-Dcom.github.fommil.netlib.ARPACK=com.github.fommil.netlib.F2jARPACK`


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

Of particular note is the [cuBLAS](https://developer.nvidia.com/cublas) (NVIDIA's graphics card) which performs as well
as ATLAS on `DGEMM` for arrays of `~20,000+` elements (but as badly as the Raspberry Pi for smaller arrays!) and
not so good for `DDOT`.

Included in the CUDA performance results is the
time taken to setup the CUDA interface and copy the matrix elements to the GPU device. The `nooh` run is
a version that does not include the overhead of transferring arrays to/from the GPU device: to take
full advantage of the GPU requires developers to re-write their applications with
GPU devices in mind. e.g. re-written implementation of LAPACK that took advantage of the GPU BLAS
would give a much better performance improvement than dipping in-and-out of GPU address space.


The [DGEMM](http://www.netlib.no/netlib/lapack/double/dgemm.f) benchmark
measures [matrix multiplication](http://en.wikipedia.org/wiki/General_Matrix_Multiply)
performance:

![dgemm](http://i752.photobucket.com/albums/xx162/fommil/dgemm_zps96e874f4.png)

The [DGETRI](http://www.netlib.no/netlib/lapack/double/dgetri.f) benchmark
measures matrix [LU Factorisation](http://en.wikipedia.org/wiki/LU_decomposition)
and [matrix inversion](http://mathworld.wolfram.com/MatrixInverse.html) performance:

![dgetri](http://i752.photobucket.com/albums/xx162/fommil/dgetri_zpsbbbf225f.png)

The [DDOT](http://www.netlib.no/netlib/blas/ddot.f) benchmark measures
[vector dot product](http://en.wikipedia.org/wiki/Dot_product) performance:

![ddot](http://i752.photobucket.com/albums/xx162/fommil/ddot_zpsa0f2eb74.png)


The [DSAUPD](http://www.caam.rice.edu/software/ARPACK/UG/node136.html) benchmark measures the
calculation of 10% of the eigenvalues for sparse matrices (`N` rows by `N` colums). Not included in
this benchmark is the time taken to perform the matrix multiplication at each iteration
(typically `N` iterations).

![dsaupd](http://i752.photobucket.com/albums/xx162/fommil/dsaupd_zps1b033991.png)


*NOTE: larger arrays were called first so the JIT has already kicked in for F2J
implementations: on a cold startup the F2J implementations are about 10 times slower and get to peak
performance after about 20 calls of a function (Raspberry Pi doesn't seem to have a JIT).*


Installation
============

**Don't download the zip file unless you know what you're doing: use [maven](http://maven.apache.org/) or [ivy](http://ant.apache.org/ivy/) to manage your dependencies as described below.**

Releases are distributed on Maven central:

```xml
<dependency>
  <groupId>com.github.fommil.netlib</groupId>
  <artifactId>all</artifactId>
  <version>1.1.2</version>
  <type>pom</type>
</dependency>
```

SBT developers can use

```scala
"com.github.fommil.netlib" % "all" % "1.1.2" pomOnly()
```

Those wanting to preserve the pre-1.0 API can use the legacy package (but
note that it **will** be removed in the next release):

```xml
<dependency>
  <groupId>com.googlecode.netlib-java</groupId>
  <artifactId>netlib</artifactId>
  <version>1.1</version>
</dependency>
```

and developers who feel the native libs are too much bandwidth can
depend on a subset of implementations: simply look in the `all`
module's [`pom.xml`](all/pom.xml).


Snapshots (preview releases, when new features are in active development) are distributed on Sonatype's Snapshot Repository, e.g.:

```xml
<dependency>
  <groupId>com.github.fommil.netlib</groupId>
  <artifactId>all</artifactId>
  <version>1.2-SNAPSHOT</version>
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
