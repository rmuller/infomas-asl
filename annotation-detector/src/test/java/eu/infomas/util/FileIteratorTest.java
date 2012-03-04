package eu.infomas.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public final class FileIteratorTest {

    @Test
    public void testNoFile() throws IOException {
        FileIterator iter = new FileIterator();
        assertEquals(0, countFiles(iter));
    }
    
    @Test
    public void testSingleFile() throws IOException {
        FileIterator iter = new FileIterator(
            new File("./src/test/java/eu/infomas/util/FileIteratorTest.java"));
        assertEquals(1, countFiles(iter));
    }
    
    @Test
    public void testSingleDirectory1() throws IOException {
        FileIterator iter = new FileIterator(
            new File("./src/test/java/eu/infomas/util"));
        assertEquals(1, countFiles(iter));
    }
    
    @Test
    public void testSingleDirectory4() throws IOException {
        FileIterator iter = new FileIterator(
            new File("./src/test/java/eu/infomas"));
        // 5 in annotation and 1 in util
        assertEquals(6, countFiles(iter));
    }
    
    @Test
    public void testMixed() throws IOException {
        FileIterator iter = new FileIterator(
            new File("./src/test/java/eu/infomas/util/FileIteratorTest.java"), 
            new File("./src/test/java/eu/infomas/annotation/"));
        assertEquals(6, countFiles(iter));
    }
    @Test
    public void testIsRoot1() throws IOException {
        FileIterator iter = new FileIterator(new File("./src/test/java/eu/infomas/util/FileIteratorTest.java"));
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
            new File("./src/test/java/eu/infomas/util/FileIteratorTest.java"), 
            new File("./src/test/java/eu/infomas/annotation/") 
            );
        while (iter.next() != null) {
            if ("FileIteratorTest.java".equals(iter.getFile().getName())) {
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
            new File("./src/test/java/eu/infomas/util/FileIteratorTest.java") 
            );
        while (iter.next() != null) {
            if ("FileIteratorTest.java".equals(iter.getFile().getName())) {
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
