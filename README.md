netlib-java [![Build Status](https://travis-ci.org/fommil/netlib-java.png?branch=master)](https://travis-ci.org/fommil/netlib-java)
===========

Mission-critical software components for linear algebra systems.

Java wrapper for low-level [BLAS](http://en.wikipedia.org/wiki/Basic_Linear_Algebra_Subprograms),
[LAPACK](http://en.wikipedia.org/wiki/LAPACK) and [ARPACK](http://en.wikipedia.org/wiki/ARPACK).

Pure Java implementations are provided to ensure full portability by [F2J](http://icl.cs.utk.edu/f2j/),
with native reference builds (using the Fortran code from [netlib.org](http://www.netlib.org))
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

*Improvements to startup time (several seconds) can be achieved by providing the exact binary name to load,
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

Java has a reputation with (mostly) older generation developers, because they remember
when Java first came out and was very slow.
Nowadays, the [JIT](http://en.wikipedia.org/wiki/Just-in-time_compilation)
ensures that Java applications keep pace with C++ applications (or indeed, Fortran applications!).

The following benchmark, [LINPACK](http://www.netlib.org/linpack), shows the performance of
Java vs reference native implementations of BLAS. Note that the Java implementation is about 10 times
faster by about the 3rd or 4th iteration (that's the JIT kicking in).

One should expect machine optimised natives to out-perform the reference native implementation.

![linpack](http://i39.tinypic.com/280trgl.png)

The following performance charts give an idea of the performance ratios of Java vs the native
reference implementation for dot product of vectors (`ddot`) and matrix multiplication (`dgemm`):

![ddot](http://i43.tinypic.com/dot9qs.png)

![dgemm](http://i44.tinypic.com/w0ro7t.png)



Installation
============

Snapshots are distributed on Sonatype's Snapshot Repository:

```xml
<dependency>
  <groupId>com.github.fommil.netlib</groupId>
  <artifactId>all</artifactId>
  <version>1.0-SNAPSHOT</version>
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

