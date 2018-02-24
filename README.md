jmind-pigg: Distributed ORM Framework for Java
=========================================

[![Build Status](https://travis-ci.org/jfaster/mango.svg?branch=master)](https://travis-ci.org/jfaster/mango)
[![Coverage Status](https://coveralls.io/repos/github/jfaster/mango/badge.svg?branch=master)](https://coveralls.io/github/jfaster/mango?branch=master)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Fast, simple, reliable. Mango is a high-performance Distributed ORM Framework.
Mango makes it easier to use a relational database with object-oriented applications.
Performance and Simplicity are the biggest advantage of the Mango data mapper over object relational mapping tools.
In our payment system, we use mango to build a distributed payment order system
that can handle 120000+ payment order per second.



Requires JDK 1.6 or higher.

Latest release
--------------

The most recent release is Mango 1.5.2, released March 22, 2017.

To add a dependency on Mango using Maven, use the following:

```xml
<dependency>
    <groupId>net.oschina.jmind</groupId>
    <artifactId>jmind-pigg</artifactId>
    <version>2.0.0</version>
</dependency>
```



JMH Benchmarks
--------------



 * Jdbc means using only native jdbc API, do not use any ORM frameworks.
 * One *Query Cycle* is defined as single ``select id, name, age from user where id = ?``.
 * One *Update Cycle* is defined as single ``update user set age = ? where id = ?``.

<sup>
<sup>1</sup> Versions: jmind-pigg 2.0.0, spring-jdbc 4.0.5, mybatis 3.4.0, hsqldb 2.3.1, Java 1.7.0_25 <br/>
<sup>2</sup> Java options: -server -XX:+AggressiveOpts -XX:+UseFastAccessorMethods -Xms1096m -Xmx1096m <br/>
</sup>
