package eu.infomas.dropbox;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.junit.Ignore;
import org.junit.Test;

public final class DropboxOAuthTest extends DropboxTestBase {

    @Ignore
    @Test
    public void testSetUpTokenCredentials() throws IOException, URISyntaxException {
        Credentials temporary = dropbox.requestTemporaryCredentials();
        URL endpoint = dropbox.getResourceOwnerAuthorizationEndpoint(temporary);
        Desktop.getDesktop().browse(endpoint.toURI());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            error(ex);
        }

        Credentials tokenCredentials = dropbox.requestTokenCredentials(temporary);
        // Link successful with uid 92201971: 0pcnc54zp78u8hx dekw6ry4ai48ij3
        info("Link successful with: %s", tokenCredentials);
    }
    
}
