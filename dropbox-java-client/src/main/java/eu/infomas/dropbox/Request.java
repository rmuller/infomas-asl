/* Request.java
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static eu.infomas.dropbox.Utils.UTF8;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

/**
 * {@code Request} holds all data for a HTTP(S) REST Request. A Builder is used for
 * creating new {@code Request} instances.
 *
 * @see RestClient
 *
 * @author <a href="mailto:rmuller@xiam.nl">Ronald K. Muller</a>
 * @since dropbox-java-client 3.0.2
 */
public final class Request {
    
    private String method;
    private String scheme = "https";
    private String host;
    private int port = -1;
    private String path;
    private InputStream payload;
    private final Map<String, String> parameters = new HashMap<String, String>();
    private final Map<String, String> headers = new HashMap<String, String>();

    public static final class Builder {

        private final Request request = new Request();

        private Builder(final String method) {
            assert "GET".equals(method) || "POST".equals(method) || "PUT".equals(method);
            request.method = method;
        }
        
        /**
         * Specify the scheme (protocol) of the request. This value is optional. If not
         * specified, {@code https} is assumed. {@link MalformedURLException}.
         */
        public Builder withSchema(final String scheme) {
            assert "http".equals(scheme) || "https".equals(scheme);
            request.scheme = scheme;
            return this;
        }

        /**
         * Specify the host (server address) of the request endpoint (URL). This value is
         * mandatory.
         */
        public Builder withHost(final String host) {
            request.host = host;
            return this;
        }

        /**
         * Specify the port of the request endpoint (URL). If not specified, the port
         * number is omitted from the URL.
         */
        public Builder withPort(final int port) {
            assert port > 0;
            request.port = port;
            return this;
        }
        
        /**
         * Specify the absolute path of the request endpoint (URL). This value is
         * optional. It specified, the path must be absolute (starting with a "/").
         */
        public Builder withPath(final String path) {
            if (!path.startsWith("/")) {
                throw new IllegalArgumentException("'path' must be absolute: " + path);
            }
            request.path = path;
            return this;
        }
        
        /**
         * Add a query parameter. Note that 'value' is always converted to a String by
         * calling {@link Object#toString() }. If the 'value' is {@code null} the query
         * parameter is not added to the URL.
         */
        public Builder withParameter(final String name, final Object value) {
            assert name != null && !name.trim().isEmpty();
            if (value != null) {
                request.parameters.put(name, value.toString());
            }
            return this;
        }

        /**
         * Add a header name-value pair. If 'value' is {@code null} the header name-value
         * pair is not added.
         */
        public Builder withHeader(final String name, final String value) {
            assert name != null && !name.trim().isEmpty();
            if (value != null) {
                request.headers.put(name, value);
            }
            return this;
        }
        
        /**
         * Specify the payload (request body) data. This is optional.
         */
        public Builder withPayload(final InputStream payload) {
            request.payload = payload;
            return this;
        }
        
        /**
         * Return the URL for this request.
         */
        public URL toURL() throws MalformedURLException {
            return request.getUrl();
        }

        public void toFile(final RestClient client, final String path) throws IOException {
            toOutputStream(client, new FileOutputStream(path));
        }
        
        public void toFile(final RestClient client, final File file) throws IOException {
            toOutputStream(client, new FileOutputStream(file));
        }
        
        public String asString(final RestClient client) throws IOException {
            return client.asString(request);
        }
 
        public void toOutputStream(final RestClient client, final OutputStream responseStream) 
            throws IOException {
            
            client.toOutputStream(request, responseStream);
        }
    
    }

    /**
     * Must be created by the {@link Builder}.
     */
    private Request() {
    }
        
    /**
     * Factory method, starts the building of a HTTP REST Request.
     * Specify the HTTP method (i.e "GET", "POST" or "PUT") of the request.
     */
    public static Builder withMethod(final String method) {
        return new Builder(method);
    }
    
    /**
     * Return the HTTP method as String (i.e. "GET", "POST", "PUT").
     */
    public String getMethod() {
        return method;
    }
    
    /**
     * Return the URL for this Request, including query parameters.
     */
    public URL getUrl() throws MalformedURLException {
        if (parametersAsPayload()) {
            return new URL(scheme, host, port, getPath());
        } else {
            return new URL(scheme, host, port, getPath() + getQuery(true));
        }
    }
    
    /**
     * Return the HTTP Headers for this request.
     */
    public Collection<Map.Entry<String, String>> getHeaders() {
        return headers.entrySet();
    }

    /**
     * Return the payload, may be {@code null}.
     */
    public InputStream getPayload() {
        if (parametersAsPayload()) {
            // In case of a HTTP POST: parameters are Url Encoded Form Parameters, 
            // put in the message request body 
            try {
                return new ByteArrayInputStream(getQuery(false).getBytes(UTF8));
            } catch (UnsupportedEncodingException ex) {
                throw new AssertionError(ex); // UTF-8 is always supported
            }
        } else {
            return payload;
        }
    }

    @Override
    public String toString() {
        try {
            return method + ' ' + getUrl();
        } catch (MalformedURLException ex) {
            return ex.toString();
        }
    }

    // private
    
    private String getPath() {
        return path == null ? "" : path;
    }

    private String getQuery(final boolean isQuery) {
        if (parameters.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        if (isQuery) {
            sb.append('?');
        }
        for (final Map.Entry<String, String> pair : parameters.entrySet()) {
            if (sb.length() > 1) {
                sb.append('&');
            }
            sb.append(encodeRfc3986(pair.getKey())).append('=').append(encodeRfc3986(pair.getValue()));
        }
        return sb.toString();
    }
    
    private boolean parametersAsPayload() {
        return payload == null && !"GET".equals(method);
    }

    /**
     * URLEncoder is not strictly conform RFC 3986, but okay in practice. Note that the
     * percent encoding described in RFC 5849 (OAuth 1.0) is not used for URI/URL
     * construction!
     */
    private static String encodeRfc3986(String value) {
        try {
            return URLEncoder.encode(value, UTF8);
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        }
    }
}
