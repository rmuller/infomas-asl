package eu.infomas.annotation;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static eu.infomas.util.TestSupport.log;

public final class PerformanceTest {

    private static File RT_JAR;

    @BeforeClass
    public static void setup() {
        File jre6 = new File("/usr/lib/jvm/java-6-sun/jre/lib/rt.jar");
        if (jre6.isFile()) {
            RT_JAR = jre6;
        } else {
            RT_JAR = new File(new File(System.getProperty("java.home")), "lib/rt.jar");
        }
        log("Using %s", RT_JAR);
    }

    // Notes: No significant performance when:
    // filtering enabled
    // using lambda's (Java 8)
    @Test
    public void testPerformance() throws IOException {
        long total = 0L;
        for (int i = 0; i < 20; ++i) {
            long time = System.currentTimeMillis();
            final AtomicInteger count = new AtomicInteger();
            AnnotationDetector.scanFiles(RT_JAR)
                .forAnnotations(Deprecated.class)
                .on(ElementType.METHOD)
                .report(new Reporter() {

                    @Override
                    public void report(Cursor cursor) {
                        count.incrementAndGet();
                    }
                });
            // Oracle JDK 6_u45: 196 MB/s with java-6-sun JDK
            // Oracle JDK 8_u05: 168 MB/s with java-6-sun JDK
            //assertEquals(395, count.get());
            assertTrue(count.get() > 300);
            time = System.currentTimeMillis() - time;
            if (i > 9) {
                total += time;
            }
            log("Time (filter): %d ms.", time);
        }
        log("Avg MB/s: %d", RT_JAR.length() / 100 / total);
    }

}
