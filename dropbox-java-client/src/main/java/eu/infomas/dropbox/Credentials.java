/* Credentials.java
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

import java.io.Serializable;

/**
 * {@code Credentials} holds the key (identifier) and secret token pair used in the OAuth
 * protocol and REST requests.
 *
 * @author <a href="mailto:rmuller@xiam.nl">Ronald K. Muller</a>
 * @since dropbox-java-client 3.0.2
 */
public final class Credentials implements Serializable {

    private final String key;
    private final String secret;

    /**
     * See factory method {@link Credentials#of(java.lang.String, java.lang.String) }.
     */
    private Credentials(final String key, final String secret) {
        this.key = Utils.notNullOrBlank("key", key);
        this.secret = Utils.notNullOrBlank("secret", secret);
    }

    /**
     * Create a new {@code Credentials} object. If both 'key' and 'secret' are
     * {@code null}, {@code null} is returned. Otherwise both 'key' and 'secret' must be
     * non-{@code null} and non-empty Strings.
     *
     * @throws IllegalArgumentException if key or secret is {@code null} or empty.
     */
    public static Credentials of(final String key, final String secret) {
        if (key == null && secret == null) {
            return null;
        }
        return new Credentials(key, secret);
    }

    /**
     * Return the key (identifier) of this {@code Credentials}
     */
    public String getKey() {
        return key;
    }

    /**
     * Return the secret token of this {@code Credentials}
     */
    public String getSecret() {
        return secret;
    }

    @Override
    public int hashCode() {
        return key.hashCode() ^ secret.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof Credentials) {
            Credentials t = (Credentials) other;
            return t.key.equals(key) && t.secret.equals(secret);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Credentials[key=" + key + ", secret=" + secret + ']';
    }
}
