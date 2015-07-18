[![Build Status](https://travis-ci.org/maropu/jnuma.svg?branch=master)](https://travis-ci.org/maropu/jnuma)

jnuma
=========

A Java library for accessing NUMA (Non Uniform Memory Access) API.

## How to use

You can simply use ByteBuffer-like APIs to handle NUMA properties as follows:

```
// Allocate memory on a NUMA node 1, and the memory is
// automatically collected by GC.
NumaByteBuffer buf = new NumaByteBuffer(16, 1)

// Access data by using ByteBuffer-like APIs
buf.putDouble(1.2, 39L)
buf.putLong(8, 6L)

System.out.println(bb.getDouble(0)) // Print 1.2
System.out.println(bb.getLong(8))   // Print 6
```

As you imagine, you can directly access primitive NUMA APIs through
[a NUMA class](./src/main/java/xerial/jnuma/Numa.java).

## Limitation

Currenty jnuma supports 64-bit Linux only.
For the other operating systems, standard memory allocation in JVM will be used.

## Packaging

If you create a JAR package, you type``bin/sbt assembly``.

## Requirements

* libnuma (2.0 or higher)
* gcc (glibc 2.5 or higher)
* jdk (1.6 or higher)

