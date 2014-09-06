package eu.infomas.annotation;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ClassFileIteratorTest {

    @Test
    public void testJarFile() throws IOException {
        File [] f = {new File("./src/test/resources/test.jar.extension")};
        String [] s = {"eu/infomas/annotation"};

        InputStream stream = new ClassFileIterator(f, s).next();
        assertNotNull(stream);
        stream.close();
    }

}
