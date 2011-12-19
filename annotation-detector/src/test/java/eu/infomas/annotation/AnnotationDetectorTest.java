package eu.infomas.annotation;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;

import org.junit.Test;

@RuntimeInvisibleTestAnnotation
// This annotation is only used to test a complex annotation type
@RuntimeVisibleTestAnnotations({
    @RuntimeVisibleTestAnnotation(name="a"),
    @RuntimeVisibleTestAnnotation(name="b")
})
public final class AnnotationDetectorTest {
    
    private static final boolean DEBUG = false;
    
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

    @Test
    public void testClassPathScannerRT() throws IOException {
        final long time = System.currentTimeMillis();

        final CountingReporter counter = new CountingReporter(Deprecated.class);
        final AnnotationDetector cf = new AnnotationDetector(counter);
        // Scan all Java Class Files in the specified files (i.e. rt.jar)
        // 380 ms
        cf.detect(RT_JAR);
        if (DEBUG) System.err.printf("Time: %d ms.\n", System.currentTimeMillis() - time);
        assertEquals(66, counter.getTypeCount()); // Java 6 SE update 26
    }

    @Test
    public void testMethodAnnotationsOnCompleteClasspath() throws IOException {
        final long time = System.currentTimeMillis();

        final CountingReporter counter = new CountingReporter(Test.class);
        final AnnotationDetector cf = new AnnotationDetector(counter);
        cf.detect();
        // 120 ms
        if (DEBUG) System.err.printf("Time: %d ms.\n", System.currentTimeMillis() - time);
        assertEquals(0, counter.getTypeCount());
        assertEquals(0, counter.getFieldCount());
        assertEquals(10, counter.getMethodCount());
    }

    @Test
    public void testMethodAnnotationsPackageOnly() throws IOException {
        final long time = System.currentTimeMillis();

        @SuppressWarnings("unchecked")
        final CountingReporter counter = new CountingReporter(Test.class);
        final AnnotationDetector cf = new AnnotationDetector(counter);
        cf.detect("eu.infomas");
        // 6 ms
        if (DEBUG) System.err.printf("Time: %d ms.\n", System.currentTimeMillis() - time);
        assertEquals(0, counter.getTypeCount());
        assertEquals(0, counter.getFieldCount());
        assertEquals(10, counter.getMethodCount());
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
//        final File file = new File("./target/test-classes/eu/infomas/annotation/AnnotationDetectorTest.class");
//        new AnnotationDetector(counter).detect(file);
        // only in this package == only this class!
        new AnnotationDetector(counter).detect("eu.infomas.annotation");
        
        assertEquals(2, counter.getTypeCount());
        assertEquals(1, counter.getFieldCount());
        assertEquals(2, counter.getMethodCount());
    }
    
}
