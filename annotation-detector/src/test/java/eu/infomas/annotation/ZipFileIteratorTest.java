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

public final class ZipFileIteratorTest {
    
    private static final boolean DEBUG = false;
    
    // rt.jar is our test file. Always available and BIG (about 50MB)
    // count=17436, bytes=49115087 @ Java 6 update 26
    private static final File RT_JAR = new File(new File(System.getProperty("java.home")), "lib/rt.jar");

    @Test
    public void benchmarkReadZip() throws IOException {
        for (int i = 0; i < 5; ++i) {
            testReadZipUsingZipFile(); // about up to 3 times faster! 60 ms versus 170 ms
            testReadZipUsingZipInputStream();
        }
    }

    private void testReadZipUsingZipFile() throws IOException {
        ClassFileBuffer buffer = new ClassFileBuffer(128 * 1024);

        final long time = System.currentTimeMillis();
        int count = 0;
        long bytes = 0;
        ZipFile zf = new ZipFile(RT_JAR); // open in OPEN_READ mode
        Enumeration<? extends ZipEntry> e = zf.entries();
        while (e.hasMoreElements()) {
            ZipEntry ze = e.nextElement();
            if (ze.isDirectory()) {
                continue;
            }
            InputStream is = zf.getInputStream(ze);
            buffer.readFrom(is);
            bytes += buffer.size();
            ++count;
        }
        if (DEBUG) System.out.printf("Time: %d ms, count=%d, bytes=%d ZipFile\n", 
            System.currentTimeMillis() - time, count, bytes);
    }

    private void testReadZipUsingZipInputStream() throws IOException {
        ClassFileBuffer buffer = new ClassFileBuffer(128 * 1024);
        
        final long time = System.currentTimeMillis();
        int count = 0;
        long bytes = 0;
        ZipInputStream zi = new ZipInputStream(new FileInputStream(RT_JAR));
        ZipEntry ze;
        while ((ze = zi.getNextEntry()) != null) {
            if (ze.isDirectory()) {
                continue;
            }
            InputStream is = zi;
            buffer.readFrom(is);
            bytes += buffer.size();
            ++count;
        }
        if (DEBUG) System.out.printf("Time: %d ms, count=%d, bytes=%d ZipInputStream\n", 
            System.currentTimeMillis() - time, count, bytes);
    }
   
}
