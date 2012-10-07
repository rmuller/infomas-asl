package eu.infomas.dropbox;

import org.junit.Test;
import static org.junit.Assert.*;
import static eu.infomas.dropbox.Utils.*;

public final class UtilsTest {

    // Check RFC 5849 compliance
    @Test
    public void encodeDecodeTest() {
        final String text = "azAZ09-._~* @|+";
        assertEquals("azAZ09-._~%2A%20%40%7C%2B", encodeRfc5849(text));
        assertEquals(text, decodeRfc5849(encodeRfc5849(text)));
    }
    
}
