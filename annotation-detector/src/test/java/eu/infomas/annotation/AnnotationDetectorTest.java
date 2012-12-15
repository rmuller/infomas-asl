package eu.infomas.annotation;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;

import static eu.infomas.util.TestSupport.*;
import static org.junit.Assert.*;

@RuntimeInvisibleTestAnnotation
// This annotation is only used to test a complex annotation type
@RuntimeVisibleTestAnnotations({
    @RuntimeVisibleTestAnnotation(name="a"),
    @RuntimeVisibleTestAnnotation(name="b")
})
public final class AnnotationDetectorTest {
    
    private static final boolean DEBUG = false;
    
    @SuppressWarnings("unused") // used for testing only
    @RuntimeVisibleTestAnnotation
    private String fieldWithAnnotation;
    
    static class CountingReporter 
        implements AnnotationDetector.TypeReporter,  AnnotationDetector.MethodReporter, AnnotationDetector.FieldReporter {

        private final Class<? extends Annotation>[] annotations;
        private int typeCount;
        private int fieldCount;
        private int methodCount;
        
        CountingReporter(Class<? extends Annotation>... annotations) {
            this.annotations = annotations;
        }
        
        public final Class<? extends Annotation>[] annotations() {
            return annotations;
        }
        
        public final void reportTypeAnnotation(Class<? extends Annotation> annotation, String className) {
            ++typeCount;
            if (DEBUG) System.out.printf("%d reportTypeAnnotation on type '%s': @%s\n", 
                typeCount, className, annotation.getName());
        }

        @Override
        public void reportFieldAnnotation(Class<? extends Annotation> annotation, 
            String className, String fieldName) {
            
            ++fieldCount;
            if (DEBUG) System.out.printf("%d reportFieldAnnotation on field '%s#%s': @%s\n", 
                fieldCount, className, fieldName, annotation.getName());
        }
        
        public final void reportMethodAnnotation(Class<? extends Annotation> annotation, 
            String className, String methodName) {
            
            ++methodCount;
            if (DEBUG) System.out.printf("%d reportMethodAnnotation on method '%s#%s': @%s\n", 
                methodCount, className, methodName, annotation.getName());
        }
        
        public final int getTypeCount() {
            return typeCount;
        }
        
        public int getFieldCount() {
            return fieldCount;
        }
        
        public final int getMethodCount() {
            return methodCount;
        }

    }
    
    // rt.jar is our test file: always available when running the unit tests 
    // and BIG (about 50MB). Number of .class files: 17436 @ Java 6 update 26
    private static final File RT_JAR = new File(new File(System.getProperty("java.home")), "lib/rt.jar");

    // Mainly used as benchmark (timing) method
    @Test
    public void testClassPathScannerRT() throws IOException {
        for (int i = 0; i < 6; ++i) {
            final long time = System.currentTimeMillis();
            final CountingReporter counter = new CountingReporter(Deprecated.class);
            final AnnotationDetector cf = new AnnotationDetector(counter);
            // Scan all Java Class Files in the specified files (i.e. rt.jar)
            cf.detect(RT_JAR); // scan specific files and directories
            if (i == 5) {
                // report, first 5 iterations where for warming up VM
                // java-6-oracle (u26): Time: 255 ms. Type Count: 66, Method Count: 395
                // java-7-oracle (u7): Time: 315 ms. Type Count: 83, Method Count: 435
                // java-7-openjdk (u7): Time: 994 ms. Type Count: 70, Method Count: 427
                log("Time: %d ms. Type Count: %d, Method Count: %d", 
                    System.currentTimeMillis() - time, counter.getTypeCount(), 
                    counter.methodCount);
                
                // we cannot use the returned count as useful value, because it differs from 
                // JDK version to JDK version, but The Deprecated class must be detected
                assertTrue(counter.getTypeCount() > 0);                
            }
        }
    }

    @Test
    public void testMethodAnnotationsOnCompleteClasspath() throws IOException {
        final long time = System.currentTimeMillis();

        final CountingReporter counter = new CountingReporter(Test.class);
        final AnnotationDetector cf = new AnnotationDetector(counter);
        cf.detect(); // complete class path is scanned
        // 120 ms
        if (DEBUG) log("Time: %d ms.", System.currentTimeMillis() - time);
        assertEquals(0, counter.getTypeCount());
        assertEquals(0, counter.getFieldCount());
        assertEquals(15, counter.getMethodCount());
    }

    @Test
    public void testMethodAnnotationsPackageOnly() throws IOException {
        final long time = System.currentTimeMillis();

        @SuppressWarnings("unchecked")
        final CountingReporter counter = new CountingReporter(Test.class);
        final AnnotationDetector cf = new AnnotationDetector(counter);
        cf.detect("eu.infomas"); // only this package and sub package(s) are scanned
        // 6 ms
        if (DEBUG) log("Time: %d ms.", System.currentTimeMillis() - time);
        assertEquals(0, counter.getTypeCount());
        assertEquals(0, counter.getFieldCount());
        assertEquals(15, counter.getMethodCount());
    }
    
    /**
     * Test the more complex annotation on this class (RuntimeVisibleTestAnnotations).
     * Ensure that both visible and invisible annotations are reported.
     */
    @Test
    @RuntimeVisibleTestAnnotation
    @RuntimeInvisibleTestAnnotation
    public void testTestComplexAnnotations() throws IOException {
        
        @SuppressWarnings("unchecked")
        final CountingReporter counter = new CountingReporter(
            RuntimeVisibleTestAnnotations.class,
            RuntimeVisibleTestAnnotation.class,
            RuntimeInvisibleTestAnnotation.class);
        // only in this package == only this class!
        new AnnotationDetector(counter).detect("eu.infomas.annotation");
        
        assertEquals(2, counter.getTypeCount());
        assertEquals(1, counter.getFieldCount());
        assertEquals(2, counter.getMethodCount());
    }


    /**
     * Test checking ClassCheckFilter behavior when excluding detection in this class
     */
    @Test
    public void testClassCheckFilterExcludesThisClass() throws IOException{
        @SuppressWarnings("unchecked")
        final CountingReporter counter = new CountingReporter(
                RuntimeVisibleTestAnnotations.class,
                RuntimeVisibleTestAnnotation.class,
                RuntimeInvisibleTestAnnotation.class);
        // only in this package == only this class!
        new AnnotationDetector(counter, new AnnotationDetector.ClassCheckFilter() {
            @Override
            public boolean isEligibleForScanning(final String fileName) {
                return !fileName.contains(AnnotationDetectorTest.class.getSimpleName());
            }
        }).detect("eu.infomas.annotation");

        assertEquals(0, counter.getTypeCount());
        assertEquals(0, counter.getFieldCount());
        assertEquals(0, counter.getMethodCount());
    }
    
}
