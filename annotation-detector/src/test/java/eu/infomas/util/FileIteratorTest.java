package eu.infomas.util;

import static org.junit.Assert.assertEquals;

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
        assertEquals(3, countFiles(iter));
    }
    
    @Test
    public void testMixed() throws IOException {
        FileIterator iter = new FileIterator(
            new File("./src/test/java/eu/infomas/util/FileIteratorTest.java"), 
            new File("./src/test/java/eu/infomas/annotation/"));
        assertEquals(3, countFiles(iter));
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
