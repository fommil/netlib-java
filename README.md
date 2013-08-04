netlib-java [![Build Status](https://travis-ci.org/fommil/netlib-java.png?branch=master)](https://travis-ci.org/fommil/netlib-java)
===========

Mission-critical software components for linear algebra systems.

This project is currently undergoing a major (API preserving) rewrite and will be back shortly with easy-to-use native binaries.

If you need access to the old native codebase, look in the `ant` branch.

The TRUNK reference implementation of BLAS.java (not machine optimised)
is between 60% and 10% faster than the Java implementation for vector multiplication.
Native code tends to work best for arrays of about a million entries.