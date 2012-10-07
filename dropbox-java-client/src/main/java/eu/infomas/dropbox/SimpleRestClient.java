/* SimpleRestClient.java
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@code SimpleRestClient} is a basic implementation of the {@link RestClient}, based on
 * {@code java.net} so it can be used without problems in <a
 * href="https://developers.google.com/appengine/docs/java/urlfetch/usingjavanet">Google
 * App Engine</a>.
 *
 * @author <a href="mailto:rmuller@xiam.nl">Ronald K. Muller</a>
 * @since dropbox-java-client 3.0.2
 */
public final class SimpleRestClient extends RestClient {
    
    private static final Logger LOG = Logger.getLogger(SimpleRestClient.class.getName());

    @Override
    public void toOutputStream(Request request, OutputStream responseStream) throws IOException {
        final HttpURLConnection c  = execute(request);
        Utils.copyStream(c.getInputStream(), responseStream);
    }
    
    @Override
    public String asString(Request request) throws IOException {
        final HttpURLConnection c  = execute(request);
        return Utils.toString(c.getInputStream(), c.getContentEncoding());
    }

    private HttpURLConnection execute(final Request request) throws IOException {
        
        final long time = System.currentTimeMillis();

        final URL url = request.getUrl();
        final HttpURLConnection c = (HttpURLConnection)url.openConnection();
        c.setRequestMethod(request.getMethod());
        for (final Map.Entry<String, String> entry : request.getHeaders()) {
            c.setRequestProperty(entry.getKey(), entry.getValue());
        }
        final InputStream requestStream = request.getPayload();
        if (requestStream != null) {
            c.setDoOutput(true);
            Utils.copyStream(requestStream, c.getOutputStream());
        }
        LOG.log(Level.FINE, "Request:\n    {0} {1}\n    headers: {2}\n    payload: {3}", 
            new Object[]{request.getMethod(), url, request.getHeaders(), 
            requestStream != null});
        
        int responseCode = c.getResponseCode();
        if (responseCode != 200 && responseCode != 206) {
            final String body = c.getErrorStream() == null ? "" : 
                "\n" + Utils.toString(c.getErrorStream(), c.getContentEncoding());
            throw new IOException(request.toString() + " FAILED: " + 
                responseCode + ' ' +  c.getResponseMessage() + body);
        }
        
        LOG.log(Level.INFO, "{0} executed in {1} ms.", 
            new Object[] {request, System.currentTimeMillis() - time});
        return c;
    }
    
}
