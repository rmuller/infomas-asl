package eu.infomas.dropbox;

import eu.infomas.test.Order;
import eu.infomas.test.OrderedRunner;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Assumption: there is no "/testTEST" folder in the configured application when the
 * tests start.
 * This test removes all (temporary) test files.
 * 
 * @author <a href="mailto:rmuller@xiam.nl">Ronald K. Muller</a>
 */
@RunWith(OrderedRunner.class)
public final class DropboxTest extends DropboxTestBase {

    private static final String DROPBOX_DIR = "/testTEST/";
    private static final File LOCAL_FILE = new File("./target/home_logo.png");
    private static final String DROPBOX_FILE_PATH = DROPBOX_DIR + LOCAL_FILE.getName();
    
    @Ignore // first adapt the assertions for your own situation!
    @Test
    @Order(1)
    public void testAccountInfo() throws IOException, URISyntaxException {
        Account account = dropbox.accountInfo();
        info(account);
        
        assertEquals("NL", account.getCountry());
        assertEquals("Ronald Muller", account.getDisplayName());
        assertEquals("https://www.dropbox.com/referrals/NTkyMjAxOTcxOQ", account.getReferralLink());
    }
    
    /**
     * Download a test file from the Internet. Use this file for the Dropbox tests.
     */
    @Test
    @Order(2)
    public void getTestFile() throws IOException, URISyntaxException {
        // size: 280 x 77
        Request.withMethod("GET")
            .withHost("www.dropbox.com")
            .withPath("/static/images/home_logo.png")
            .toFile(RestClient.newInstance(), LOCAL_FILE);
        
        assertTrue(LOCAL_FILE.isFile());
    }
    
    /**
     * Create the test directory. It will be deleted at the end of the tests.
     */
    @Test
    @Order(3)
    public void createFolder() throws IOException, URISyntaxException {
        Entry entry = dropbox.createFolder(DROPBOX_DIR);
        info("Entry: " + entry);
        
        assertEquals(0L, entry.getBytes());
        assertTrue(entry.isDir());
    }

    @Test
    @Order(4)
    public void testPutFile() throws IOException, URISyntaxException {
        Entry entry = dropbox.filesPut(DROPBOX_FILE_PATH).fromFile(LOCAL_FILE);
        info("Entry: " + entry);
        
        assertEquals(LOCAL_FILE.length(), entry.getBytes());
        assertFalse(entry.isDir());
        assertEquals("image/png", entry.getMimeType());
    }
    
    @Test
    @Order(5)
    public void testGetFileWithRange() throws IOException, URISyntaxException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8);
        dropbox.filesGet(DROPBOX_FILE_PATH)
            .withRange(0, 7) // from (inclusive) - to (inclusive)
            .toOutputStream(baos);
        // png file, see 
        // http://en.wikipedia.org/wiki/Portable_Network_Graphics
        final byte[] magic = baos.toByteArray();
        assertEquals(0x89, magic[0] & 0xFF);
        assertEquals('P', magic[1] & 0xFF);
        assertEquals('N', magic[2] & 0xFF);
        assertEquals('G', magic[3] & 0xFF);
        assertEquals(0x0D, magic[4] & 0xFF);
        assertEquals(0x0A, magic[5] & 0xFF);
        assertEquals(0x1A, magic[6] & 0xFF);
        assertEquals(0x0A, magic[7] & 0xFF);
    }

    @Test
    @Order(6)
    public void testMedia() throws IOException, URISyntaxException {
        String publicUrl = dropbox.media(DROPBOX_FILE_PATH);
        info(publicUrl);
        
        assertTrue(publicUrl.startsWith("https://dl.dropbox.com"));
    }

    @Test
    @Order(7)
    public void testGetThumbnail() throws IOException, URISyntaxException {
        // note that the filesGet must have a valid (recognized) extension!
        final ThumbFormat format = ThumbFormat.JPEG;
        final File localFile = new File("./target/thumb" + format.getExtension());
        
        dropbox.getThumbnail(DROPBOX_FILE_PATH, ThumbSize.ICON_64x64, format, 
            new FileOutputStream(localFile));

        assertTrue(localFile.length() > 0);
    }
    
    @Test
    @Order(90)
    public void testCopy() throws IOException, URISyntaxException {
        Entry entry = dropbox.copy(DROPBOX_FILE_PATH, DROPBOX_DIR + "CopyOfImage.png");
        info("Entry: " + entry);
    }
    
    @Test
    @Order(91)
    public void testMove() throws IOException, URISyntaxException {
        Entry entry = dropbox.move(DROPBOX_FILE_PATH, DROPBOX_DIR + "Renamed_" + LOCAL_FILE.getName());
        info("Entry: " + entry);
    }
    
    @Test
    @Order(92)
    public void testMetadata() throws IOException, URISyntaxException {
        Entry entry = dropbox.metadata(DROPBOX_DIR)
            .withList()
            .asEntry();
        info(entry);
        
        assertEquals(0L, entry.getBytes());
        assertTrue(entry.isDir());
        List<Entry> children = entry.getContents();
        
        assertEquals(2, children.size());
    }
    
    @Test
    @Order(99)
    public void testDelete() throws IOException, URISyntaxException {
        Entry entry = dropbox.delete(DROPBOX_DIR);
        
        info("Entry: " + entry);
    }
    
}
