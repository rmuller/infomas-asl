# infomas-asl

[![Build Status](https://secure.travis-ci.org/rmuller/infomas-asl.png)](http://travis-ci.org/rmuller/infomas-asl)

INFOMAS ASL contains all open sourced code from the INFOMAS PCM Application Suite. All code is licensed by the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0), so it can be used by both open source and commercial projects.

The INFOMAS PCM Application Suite is a commercial Product Content Management (also known as PIM, Product Information Management) Application. For more information, visit http://www.xiam.nl.

## Quick Facts
+ Language: Java 6 SE or better
+ Tested with both Oracle JDK 6 and 7 and OpenJDK 6 and 7 (Last three VM's using [Travis CI Server](https://travis-ci.org/))
+ Build System: Maven 3
+ Apache License, Version 2.0
+ Maven Artifacts are available from [Central Maven](http://search.maven.org/#search%7Cga%7C1%7Cinfomas)

## Modules
Currently INFOMAS ASL contains the following modules:

+ annotation-detector
+ More to come ...

### annotation-detector
This library can be used to scan (part of) the class path for annotated classes, methods or instance variables.
Main advantages of this library compared with similar solutions are: light weight (**no dependencies**, simple API, **16 kb jar file**) and **very fast** (fastest annotation detection library as far as I know).

#### Maven configuration:

``` xml
<dependency>
   <groupId>eu.infomas</groupId>
   <artifactId>annotation-detector</artifactId>
   <version>3.0.2</version>
</dependency>
```

#### Example Usage:
Put the `annotation-detector-{version}.jar` in the class path. No other dependencies are required!
You can either scan the complete class path or only scan specified packages (see [JavaDoc](https://github.com/rmuller/infomas-asl/blob/master/annotation-detector/src/main/java/eu/infomas/annotation/AnnotationDetector.java) for more details).

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

## License

Copyright (c) 2011 - 2012 XIAM Solutions B.V.

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0

[![Ohloh profile for ronaldmuller](https://www.ohloh.net/accounts/224392/widgets/account_tiny.gif)](https://www.ohloh.net/accounts/224392?ref=Tiny)
