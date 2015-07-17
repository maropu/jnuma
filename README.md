[![Build Status](https://travis-ci.org/maropu/jnuma.svg?branch=master)](https://travis-ci.org/maropu/jnuma)

jnuma
=========

A Java library for accessing NUMA (Non Uniform Memory Access) API.

## Building a native library

    $ make native

## Create a JAR package

    $ bin/sbt assembly

## Limitation

Currenty jnuma supports 64-bit Linux only. For the other operating systems, standard memory allocation in JVM will be used.

## Requirements

* libnuma (2.0 or higher)
* gcc (glibc 2.5 or higher)
* jdk (1.6 or higher)

