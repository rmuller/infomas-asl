package eu.infomas.annotation;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import eu.infomas.annotation.AnnotationDetector.MethodReporter;
import eu.infomas.annotation.AnnotationDetector.TypeReporter;

@Deprecated // Deprecated for test purposes!
public final class AnnotationDetectorTest {
    
    // rt.jar is our test file: always available when running the unit tests 
    // and BIG (about 50MB). Number of .class files: 17436 @ Java 6 update 26
    private static final File RT_JAR = new File(new File(System.getProperty("java.home")), "lib/rt.jar");

    @Test
    public void testClassPathScannerRT() throws IOException {
        final long time = System.currentTimeMillis();

        final AtomicInteger counter = new AtomicInteger();
        final TypeReporter reporter = new TypeReporter() {

            @SuppressWarnings("unchecked")
            @Override
            public Class<? extends Annotation>[] annotations() {
                return new Class[]{Deprecated.class};
            }

            @Override
            public void reportTypeAnnotation(Class<? extends Annotation> annotation, String className) {
                counter.incrementAndGet();
            }

        };
        final AnnotationDetector cf = new AnnotationDetector(reporter);
        // Scan all Java Class Files in the specified files (i.e. rt.jar)
        // 420 ms
        cf.detect(RT_JAR);
        System.err.printf("Time: %d ms.\n", System.currentTimeMillis() - time);
        assertEquals(66, counter.intValue()); // Java 6 SE update 26
    }

    @Test
    public void testMethodAnnotationsOnCompleteClasspath() throws IOException {
        final long time = System.currentTimeMillis();

        final AtomicInteger counter = new AtomicInteger();
        final MethodReporter reporter = new MethodReporter() {

            @SuppressWarnings("unchecked")
            @Override
            public Class<? extends Annotation>[] annotations() {
                return new Class[]{Test.class};
            }

            @Override
            public void reportMethodAnnotation(Class<? extends Annotation> annotation,
                String className, String methodName) {
                counter.incrementAndGet();
            }
            
        };
        final AnnotationDetector cf = new AnnotationDetector(reporter);
        cf.detect();
        // 120 ms
        System.err.printf("Time: %d ms.\n", System.currentTimeMillis() - time);
        assertEquals(10, counter.intValue());
    }

    @Test
    public void testMethodAnnotationsPackageOnly() throws IOException {
        final long time = System.currentTimeMillis();

        final AtomicInteger counter = new AtomicInteger();
        final MethodReporter reporter = new MethodReporter() {

            @SuppressWarnings("unchecked")
            @Override
            public Class<? extends Annotation>[] annotations() {
                return new Class[]{Test.class};
            }

            @Override
            public void reportMethodAnnotation(Class<? extends Annotation> annotation,
                String className, String methodName) {
                counter.incrementAndGet();
            }

        };
        final AnnotationDetector cf = new AnnotationDetector(reporter);
        cf.detect("eu.infomas");
        // 6 ms
        System.err.printf("Time: %d ms.\n", System.currentTimeMillis() - time);
        assertEquals(10, counter.intValue());
    }

}
