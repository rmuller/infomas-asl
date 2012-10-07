package eu.infomas.dropbox;

import java.net.MalformedURLException;
import java.util.Locale;
import org.junit.Test;
import static org.junit.Assert.*;

public final class URLBuilderTest {

    @Test(expected=IllegalArgumentException.class)
    public void testRelativePath() throws MalformedURLException {
        Request.withMethod("GET").withPath("index.html");
    }
    
    @Test
    public void test() throws MalformedURLException {
        assertEquals("https://www.xiam.nl", Request
            .withMethod("GET")
            .withHost("www.xiam.nl")
            .toURL().toString());

        assertEquals("http://www.xiam.nl", Request
            .withMethod("GET")
            .withSchema("http")
            .withHost("www.xiam.nl")
            .toURL().toString());

        assertEquals("https://www.xiam.nl:8443", Request
            .withMethod("GET")
            .withHost("www.xiam.nl")
            .withPort(8443)
            .toURL().toString());

        assertEquals("https://www.xiam.nl/index.html", Request
            .withMethod("GET")
            .withPath("/index.html")
            .withHost("www.xiam.nl")
            .toURL().toString());

        // TODO: "http://www.xiam.nl/?q=ronald+muller" ??
        assertEquals("https://www.xiam.nl?q=ronald+muller", Request
            .withMethod("GET")
            .withHost("www.xiam.nl")
            .withParameter("q", "ronald muller")
            .toURL().toString());

        assertEquals("https://www.xiam.nl/index.html?q=ronald+muller", Request
            .withMethod("GET")
            .withPath("/index.html")
            .withHost("www.xiam.nl")
            .withParameter("q", "ronald muller")
            .toURL().toString());

        assertEquals("https://api.dropbox.com/1/oauth/request_token?locale=en", Request
            .withMethod("GET").withPath("/1/oauth/request_token")
            .withHost("api.dropbox.com")
            .withParameter("locale", Locale.ENGLISH)
            .toURL().toString());

    }
}
