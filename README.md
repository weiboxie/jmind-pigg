jmind-pigg: Distributed ORM Framework for Java
=========================================




Requires JDK 1.6 or higher.

Latest release
--------------



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
