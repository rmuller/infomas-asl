package eu.infomas.annotation;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public final class FileIteratorTest {

    @Test
    public void testNoFile() throws IOException {
        FileIterator iter = new FileIterator();
        assertEquals(0, countFiles(iter));
    }

    @Test
    public void testSingleFile() throws IOException {
        FileIterator iter = new FileIterator(
            new File("./src/test/java/eu/infomas/annotation/FileIteratorTest.java"));
        assertEquals(1, countFiles(iter));
    }

    @Test
    public void testSingleDirectory1() throws IOException {
        FileIterator iter = new FileIterator(
            new File("./src/test/java/eu/infomas/annotation"));
        assertEquals(7, countFiles(iter));
    }

    @Test
    public void testSingleDirectory4() throws IOException {
        FileIterator iter = new FileIterator(
            new File("./src/test/java/eu/infomas"));
        // 5 in annotation and 2 in util
        assertEquals(8, countFiles(iter));
    }

    @Test
    public void testMixed() throws IOException {
        FileIterator iter = new FileIterator(
            new File("./src/test/java/eu/infomas/util/TestSupport.java"),
            new File("./src/test/java/eu/infomas/annotation/"));
        assertEquals(8, countFiles(iter));
    }
    
    @Test
    public void testIsRoot1() throws IOException {
        FileIterator iter = new FileIterator(
            new File("./src/test/java/eu/infomas/util/FileIteratorTest.java"));
        assertNotNull(iter.next());
        assertTrue(iter.isRootFile());
        assertNull(iter.next());
    }

    @Test
    public void testIsRoot2() throws IOException {
        FileIterator iter = new FileIterator(new File("./src/test/java/eu/infomas/util/"));
        assertNotNull(iter.next());
        assertFalse(iter.isRootFile());
        assertNull(iter.next());
    }

    @Test
    public void testIsRoot3() throws IOException {
        FileIterator iter = new FileIterator(
            new File("./src/test/java/eu/infomas/util/TestSupport.java"),
            new File("./src/test/java/eu/infomas/annotation/")
            );
        while (iter.next() != null) {
            if ("TestSupport.java".equals(iter.getFile().getName())) {
                assertTrue(iter.isRootFile());
            } else {
                assertFalse(iter.getFile().toString(), iter.isRootFile());
            }
        }
    }

    @Test
    public void testIsRoot4() throws IOException {
        FileIterator iter = new FileIterator(
            new File("./src/test/java/eu/infomas/annotation/"),
            new File("./src/test/java/eu/infomas/util/TestSupport.java")
            );
        while (iter.next() != null) {
            if ("TestSupport.java".equals(iter.getFile().getName())) {
                assertTrue(iter.isRootFile());
            } else {
                assertFalse(iter.getFile().toString(), iter.isRootFile());
            }
        }
    }

    private int countFiles(final FileIterator iter) throws IOException {
        int counter = 0;
        while (iter.next() != null) {
            ++counter;
            //System.out.println(iter.getName());
        }
        return counter;
    }

}
