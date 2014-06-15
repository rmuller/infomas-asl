package eu.infomas.annotation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.junit.Test;

import static eu.infomas.util.TestSupport.javaVersion;
import static eu.infomas.util.TestSupport.log;

/**
 * This class is used for simple benchmarking different ways to read a ZIP file in Java.
 * {@code rt.jar} is our test file. Always available and BIG .
 * When using an Oracle JVM using ZipFile is (considerable) faster<b>on older VM's</b>.
 * Note that when using OpenJDK this is <b>not</b> the case. Also note that the OpenJDK
 * is (also in this case) MUCH slower than the Oracle JVM's.
 * <br/>
 * Some example timings (on same hardware, Ubuntu 12.04 LTS x64):
 * <pre>
 * java version "1.6.0_26" (/usr/lib/jvm/java-6-sun-1.6.0.26/jre)
 * Java(TM) SE Runtime Environment (build 1.6.0_26-b03)
 * Java HotSpot(TM) 64-Bit Server VM (build 20.1-b02, mixed mode)
 * Timing using ZipFile: 78 ms, using ZipInputStream: 176 ms.
 *
 * java version "1.7.0_07" (/usr/lib/jvm/jdk1.7.0_07/jre)
 * Java(TM) SE Runtime Environment (build 1.7.0_07-b10)
 * Java HotSpot(TM) 64-Bit Server VM (build 23.3-b01, mixed mode)
 * Timing using ZipFile: 120 ms, using ZipInputStream: 154 ms.
 *
 * java version "1.7.0_07" (/usr/lib/jvm/java-7-openjdk-amd64/jre)
 * OpenJDK Runtime Environment (build 1.7.0_07-b30)
 * OpenJDK 64-Bit Server VM (build 23.2-b09, mixed mode)
 * Timing using ZipFile: 797 ms, using ZipInputStream: 724 ms.
 *
 * java version "1.8.0_05" (/usr/lib/jvm/jdk1.8.0_05/jre)
 * Java(TM) SE Runtime Environment (build 1.8.0_05-b13)
 * Java HotSpot(TM) 64-Bit Server VM (build 25.5-b02, mixed mode)
 * Timing using ZipFile: 169 ms, using ZipInputStream: 173 ms.
 * </pre>
 */
public final class ZipFileIteratorTest {

    private static final boolean DEBUG = false;

    // count=17436, bytes=49115087 @ Java 6 update 26 (Oracle)
    // count=19002, bytes=59124405 @ Java 7 update 7 (Oracle)
    // count=18322, bytes=63983889 @ OpenJDK 7 update 7
    // count=19993, bytes=61636100 @ Java 8 update 05 (Oracle)
    private static final File RT_JAR =
        new File(new File(System.getProperty("java.home")), "lib/rt.jar");

    @Test
    public void benchmarkReadZip() throws IOException {
        // warm up VM
        for (int i = 0; i < 5; ++i) {
            testReadZipUsingZipFile(); // about up to 3 times faster! 60 ms versus 170 ms
            testReadZipUsingZipInputStream();
        }
        log(javaVersion());
        log("Timing using ZipFile: %d ms, using ZipInputStream: %d ms.",
            testReadZipUsingZipFile(),
            testReadZipUsingZipInputStream());
    }

    private long testReadZipUsingZipFile() throws IOException {
        ClassFileBuffer buffer = new ClassFileBuffer(128 * 1024);

        long time = System.currentTimeMillis();
        int count = 0;
        long bytes = 0;
        final ZipFile zf = new ZipFile(RT_JAR); // open in OPEN_READ mode
        try {
            final Enumeration<? extends ZipEntry> e = zf.entries();
            while (e.hasMoreElements()) {
                final ZipEntry ze = e.nextElement();
                if (ze.isDirectory()) {
                    continue;
                }
                final InputStream is = zf.getInputStream(ze);
                buffer.readFrom(is);
                bytes += buffer.size();
                ++count;
            }
        } finally {
            zf.close();
        }

        time = System.currentTimeMillis() - time;
        if (DEBUG) {
            log("Time: %d ms, count=%d, bytes=%d ZipFile", time, count, bytes);
        }
        return time;
    }

    private long testReadZipUsingZipInputStream() throws IOException {
        ClassFileBuffer buffer = new ClassFileBuffer(128 * 1024);

        long time = System.currentTimeMillis();
        int count = 0;
        long bytes = 0;
        final ZipInputStream zi = new ZipInputStream(new FileInputStream(RT_JAR));
        try {
            ZipEntry ze;
            while ((ze = zi.getNextEntry()) != null) {
                if (ze.isDirectory()) {
                    continue;
                }
                final InputStream is = zi;
                buffer.readFrom(is);
                bytes += buffer.size();
                ++count;
            }
        } finally {
            zi.close();
        }

        time = System.currentTimeMillis() - time;
        if (DEBUG) {
            log("Time: %d ms, count=%d, bytes=%d ZipInputStream", time, count, bytes);
        }
        return time;
    }

}
