/* RestClient.java
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
import java.io.OutputStream;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@code RestClient} defines the interface for a simple HTTP REST client, used by
 * {@link Dropbox}. It is <b>not</b> a general purpose REST Client! 
 * Functionality is very simple and "just enough" for getting {@link Dropbox} working. 
 * Actual implementations must extend this class and is discovered via the standard Java
 * {@link ServiceLoader service-provider loading facility}.
 *
 * @author <a href="mailto:rmuller@xiam.nl">Ronald K. Muller</a>
 * @since dropbox-java-client 3.0.2
 */
public abstract class RestClient {

    private static final Logger LOG = Logger.getLogger(RestClient.class.getName());

    /**
     * Return an concrete implementation of a {@code RestClient}. The standard Java
     * {@link ServiceLoader service-provider loading facility} is used. If no
     * implementation is found, {@link SimpleRestClient} is used. If several
     * implementations are available, the first found on the class path is used and a
     * warning is logged. 
     * <br/>
     * Note that this factory method always returns a newly created instance.
     */
    public static RestClient newInstance() {
        final Iterator<RestClient> clients = ServiceLoader.load(RestClient.class).iterator();
        final RestClient client;
        if (clients.hasNext()) {
            client  = clients.next();
            while (clients.hasNext()) {
                LOG.log(Level.WARNING, "RestClient implementation SKIPPED: {0}",
                    clients.next().getClass().getName());
            }
            return client;
        } else {
            client = new SimpleRestClient();
        }
        LOG.log(Level.INFO, "RestClient implementation used: {0}.", 
            client.getClass().getName());
        return client;
    }
    
    /**
     * Execute the specified {@link Request} and return the response body (payload) as
     * a String.
     * 
     * @param request The request
     * 
     * @return The response message body as String
     */
    public abstract String asString(final Request request) 
        throws IOException;
    
    /**
     * Execute the specified {@link Request} and write the response body (payload) to the 
     * specified OurputStream.
     * 
     * @param request The request
     * @param responseStream The data from the response message body (payload)
     */    
    public abstract void toOutputStream(final Request request, final OutputStream responseStream)
        throws IOException;
    
}
