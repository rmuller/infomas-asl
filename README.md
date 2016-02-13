# infomas-asl

[![Build Status](https://secure.travis-ci.org/rmuller/infomas-asl.png)](http://travis-ci.org/rmuller/infomas-asl)

INFOMAS ASL contains all open sourced code from the INFOMAS PCM Application Suite. All code is licensed by the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0), so it can be used by both open source and commercial projects.

The INFOMAS PCM Application Suite is a commercial Product Content Management (also known as PIM, Product Information Management) Application. For more information, visit http://www.xiam.nl.

## Warning: API Change!!
Starting version 3.1.0-SNAPSHOT the API has been changed considerably and several new features 
have been added. Do not worry, we did not sacrifice the unique selling points of this library:
simple API, small footprint and great performance. Performance is even improved by about 10%! 
The library size increased from 16 kB to 20 kB.
This version is not (yet) released to Maven Central, so if you disagree with some changes or 
have better ideas, please let us know!

Changes (3.1.0-SNAPSHOT):
+ More fluent API, builder style
+ Made API Java 8 friendly (using Functional interfaces / SAM's)
+ Added standard logging (using standard `java.util.logging`), removed DEBUG logging
+ Some efficiency improvements
+ Simplified API for standard use cases (see issue #7)
+ Added possibility to filter scanned classes based on the file name (see issue #11)
+ Possibility to report types, fields, methods and annotations as `java.lang.Class`, `java.lang.reflect.Field`, `java.lang.reflect.Method` and `java.lang.annotation.Annotation` (see issue #6)

## Quick Facts
+ Simple builder style API
+ Very fast (on moderate hardware about 200 MB/s)!
+ Lightweight, no dependencies, only 20 kb jar
+ Language: Java 6 SE or better with Java 8 friendly API
+ Tested with Oracle JDK 6, 7, 8 and OpenJDK 6 and 7 (Last four VM's using 
  [Travis CI Server](https://travis-ci.org/))
+ OSGi Bundle artifact
+ Apache License, Version 2.0
+ Build System: Maven 3
+ Maven Artifacts are available from [Central Maven](http://search.maven.org/#search%7Cga%7C1%7Cinfomas)
+ [Project information generated by Maven](http://rmuller.github.io/infomas-asl/)

## Modules
Currently INFOMAS ASL contains the following modules:

+ annotation-detector
+ More to come ...

### annotation-detector
This library can be used to scan (part of) the class path for annotated classes, methods or 
instance variables.
Main advantages of this library compared with similar solutions are: light weight 
(**no dependencies**, simple API, **20 kb jar file**) and **very fast** 
(fastest annotation detection library as far as I know).

#### Maven configuration:

``` xml
<dependency>
   <groupId>eu.infomas</groupId>
   <artifactId>annotation-detector</artifactId>
   <version>3.0.4</version>
</dependency>
```

#### Example Usage (3.0.x versions):
Put the `annotation-detector-3.0.x.jar` in the class path. No other dependencies are required!
You can either scan the complete class path or only scan specified packages or Files
(see [JavaDoc](http://rmuller.github.io/infomas-asl/annotation-detector/apidocs/index.html) 
for more details).

``` java
// Scan all .class files on the class path
// Report all .class files, with org.junit.Test annotated methods
final MethodReporter reporter = new MethodReporter() {

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Annotation>[] annotations() {
        return new Class[]{Test.class};
    }

    @Override
    public void reportMethodAnnotation(Class<? extends Annotation> annotation,
        String className, String methodName) {
        // do something
    }
    
};
final AnnotationDetector cf = new AnnotationDetector(reporter);
cf.detect();
```

That's all!

#### Example Usage (3.1.x versions):
Put the `annotation-detector-3.1.x.jar` in the class path. No other dependencies are required!
You can either scan the complete class path or only scan specified packages or Files
(see [JavaDoc](http://rmuller.github.io/infomas-asl/annotation-detector/apidocs/index.html) 
for more details).

``` java
// Get a List with all classes annotated with @RuntimeVisibleTestAnnotation (Java 8 syntax)
List<Class<?>> types = AnnotationDetector.scanClassPath()
    .forAnnotations(RuntimeVisibleTestAnnotation.class)
    .collect((Cursor cursor) -> cursor.getType());

    assertEquals(1, types.size());
    assertSame(NewApiTest.class, types.get(0));

// Get a List with all methods annotated with @RuntimeVisibleTestAnnotation, excluding
// files ending with "Test.class" in the "eu.infomas" package and subpackages (Java 8 syntax)
List<Method> methods = AnnotationDetector.scanClassPath("eu.infomas") // or: scanFiles(File... files)
    .forAnnotations(RuntimeVisibleTestAnnotation.class) // one or more annotations
    .on(ElementType.METHOD) // optional, default ElementType.TYPE. One ore more element types
    .filter((File dir, String name) -> !name.endsWith("Test.class")) // optional, default all *.class files
    .collect((Cursor cursor) -> cursor.getMethod()));

    assertEquals(1, methods.size());
    assertEquals(void.class, methods.get(0).getReturnType());
```

Even simpler, isn't it?

## License

Copyright (c) 2011 - 2014 XIAM Solutions B.V.

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0

[![Ohloh profile for ronaldmuller](https://www.ohloh.net/accounts/224392/widgets/account_tiny.gif)](https://www.ohloh.net/accounts/224392?ref=Tiny)
