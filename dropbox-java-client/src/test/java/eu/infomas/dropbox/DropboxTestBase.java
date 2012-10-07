package eu.infomas.dropbox;

import java.io.IOException;
import org.junit.Before;

import static org.junit.Assume.*;

/**
 * {@code DropboxTestBase} is used as abstract base class for junit tests.
 * A fresh {@link Dropbox} instance is created for every test and some simple logging 
 * methods are available, only used during development within unit tests.
 *
 * @author <a href="mailto:rmuller@xiam.nl">Ronald K. Muller</a>
 * @since dropbox-java-client 3.0.2
 */
public abstract class DropboxTestBase {

    private static final Object[] NO_ARGS = new Object[0];
    protected Dropbox dropbox;

    /**
     * If the configuration data (here: "~/.dropbox.config") is not found, the tests are
     * ignored (Netbeans / Eclipse report a pass, Maven 3 correctly reports "skipped").
     */
    @Before
    public void setUpDropbox() throws IOException {
        try {
            dropbox = new Dropbox("~/.dropbox.config");
        } catch (IOException ex) {
            assumeNotNull(dropbox);
        }
    }
    
    protected void error(final String msg, final Throwable t) {
        System.err.println("ERROR: " + msg);
        if (t != null) {
            t.printStackTrace(System.err);
        }
    }
    
    protected void error(final Throwable t) {
        t.printStackTrace(System.err);
    }

    protected void info(final Object msg) {
        info(String.valueOf(msg), NO_ARGS);
    }

    protected void info(final String msg, final Object... args) {
        System.out.println("INFO: " + (args.length == 0 ? msg : String.format(msg, args)));
    }
    
}
