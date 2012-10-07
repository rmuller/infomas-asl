/* Utils.java
 * 
 ******************************************************************************
 *
 * Created: Oct 01, 2012
 * Character encoding: UTF-8
 * 
 * Copyright (c) 2012 - XIAM Solutions B.V. The Netherlands, http://www.xiam.nl
 * 
 ********************************* LICENSE ************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.infomas.dropbox;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code Utils} offers some utility methods for classes in this package.
 * 
 * @author <a href="mailto:rmuller@xiam.nl">Ronald K. Muller</a>
 * @since dropbox-java-client 3.0.2
 */
final class Utils {

    /**
     * The canonical character set name for {@code UTF-8}.
     * {@code UTF-8} is the only character set used by JSON.
     */
    static final String UTF8 = "UTF-8";        
    
    private static final Logger LOG = Logger.getLogger(Utils.class.getName());
    
    private static final Integer DEFAULT_NUMBER = Integer.valueOf(0);
    private static final int BUFFER_SIZE = 64 * 1024;
    private static final String PREFIX_CLASSPATH = "classpath:";
    private static final Pattern KEY_VALUES = Pattern.compile("([^&]+)=([^&]+)");

    private Utils() { } // utility class

    /**
     * Parse the given {@code application/x-www-form-urlencoded} String and return the
     * parameters as a (name/value) Map. If the String is empty or {@code null} an empty
     * Map is returned.
     */
    static Map<String, String> parseParameters(final String value) {
        if (value == null || value.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, String> map = new HashMap<String, String>();
        final Matcher m = KEY_VALUES.matcher(value);
        while (m.find()) {
            map.put(decodeRfc5849(m.group(1)), decodeRfc5849(m.group(2)));
        }
        return map;
    }

    static Credentials parseCredentials(final String encoded) {
        final Map<String, String> params = parseParameters(encoded);
        final Credentials credentials = Credentials.of(
            params.get("oauth_token"), params.get("oauth_token_secret"));
        return credentials;
    }

    /**
     * Percent encodeRfc5849 the value as specified by the RFC5849 (3.6).
     */
    static String encodeRfc5849(final String value) {
        try {
            return URLEncoder.encode(value, "UTF-8")
                // now correct difference between URLEncoder and RFC5849 ...
                // not efficient, but working :)
                .replace("+", "%20") 
                .replace("*", "%2A")
                .replace("%7E", "~");
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex); // UTF-8 is always supported
        }
    }

    /**
     * Percent decodeRfc5849 the value as specified by the RFC5849 (3.6).
     */
    static String decodeRfc5849(final String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex); // UTF-8 is always supported
        }
    }

    static String asString(final Map<String, Object> map, final String key) {
        final Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String)value;
        }
        throw illegalArgumentException(key, value, String.class);
    }
    
    static Number asNumber(final Map<String, Object> map, final String key) {
        final Object value = map.get(key);
        if (value == null) {
            return DEFAULT_NUMBER;
        }
        if (value instanceof Number) {
            return (Number)value;
        }
        throw illegalArgumentException(key, value, Number.class);
    }

    static boolean asBoolean(final Map<String, Object> map, final String key) {
        final Object value = map.get(key);
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return ((Boolean)value).booleanValue();
        }
        throw illegalArgumentException(key, value, Boolean.class);
    }
    
    static Date asDate(final Map<String, Object> map, final String key) {
        final Object value = map.get(key);
        if (value == null) {
            return null; 
        }
        if (value instanceof String) {
            try {
                return dateFormat().parse((String) value);
            } catch (ParseException ex) {
                throw new IllegalArgumentException(
                    "'" + key + "' has not a valid Date format: '" + value + "'");
            }
        }
        throw illegalArgumentException(key, value, String.class);
    }

    static String notNullOrBlank(final String name, final String value) {
        assert name != null;
        if (value == null) {
            throw new IllegalArgumentException("'" + name + "' is null");
        }
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("'" + name + "' is empty");
        }
        return value;
    }
    
    static InputStream getResourceAsStream(final String resourcePath, final Class<?> clz) 
        throws IOException {
        
        final InputStream stream;
        final String path;
        if (resourcePath.startsWith(PREFIX_CLASSPATH)) {
            path = resourcePath;
            stream = clz.getResourceAsStream(path.substring(PREFIX_CLASSPATH.length()));
        } else {
            path = resourcePath.startsWith("~") ?
                System.getProperty("user.home") + resourcePath.substring(1) :
                resourcePath;
            stream = new FileInputStream(path);
        }
        LOG.log(Level.INFO, "Load Resource from ''{0}''", path);
        return stream;
    }

    static Properties loadProperties(final String configurationPath, final Class<?> clz) 
        throws IOException {
        
        final InputStream stream = getResourceAsStream(configurationPath, clz);
        final Properties props = new Properties();
        if (stream != null) {
            props.load(stream);
        }
        return props;
    }    
    
    /**
     * Read the supplied {@code InputStream} assuming the specified character encoding
     * and return it as a {@code String}.
     * The input is always closed, also if an error occurs.
     */
    static String toString(final InputStream input, final String charSet)
        throws IOException {

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copyStream(input, output);
        return output.toString(charSet == null ? UTF8 : charSet);
    }
    
    /**
     * Copies the supplied {@code InputStream} to the provided {@code OutputStream}. 
     * Both streams are always closed, before this method returns.
     */
    static void copyStream(final InputStream source, final OutputStream target)
        throws IOException {

        try {
            final byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = source.read(buffer, 0, buffer.length)) >= 0) {
                target.write(buffer, 0, length);
            }
        } finally {
            close(source);
            close(target);
        }
    }
    
    /**
     * {@code null} safe closes the supplied {@code Closeable}.
     */
    static void close(final Closeable closeable) throws IOException {
        if (closeable != null) {
            closeable.close();
        }
    }
    
    // private
    
    // SimpleDateFormat is not thread safe, so ALWAYS create a new instance
    private static DateFormat dateFormat() {
        return new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss ZZZZZ", Locale.US);
    }
        
    private static IllegalArgumentException illegalArgumentException(final String key, 
        final Object value, final Class expectedType) {
        
        return new IllegalArgumentException("'" + key + "' is not a " + 
            expectedType.getName() + " type . Value = " + value + 
            " (" + value.getClass().getName());
    }
}
