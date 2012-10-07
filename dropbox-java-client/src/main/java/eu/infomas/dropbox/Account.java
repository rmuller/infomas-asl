/*
 * Copyright (c) 2009-2011 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package eu.infomas.dropbox;

import java.io.Serializable;
import java.util.Map;
import static eu.infomas.dropbox.Utils.*;

/**
 * Information about a user's account.
 * 
 * @author Original Author is Dropbox
 * @author <a href="mailto:rmuller@xiam.nl">Ronald K. Muller</a> (small modifications)
 */
public final class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String country;
    private final String displayName;
    private final long quota;
    private final long quotaNormal;
    private final long quotaShared;
    private final long uid;
    private final String referralLink;

    /**
     * Creates an account from a Map.
     *
     * @param map a Map that looks like:      
     * <pre>
     * {"country": "",
     *  "display_name": "John Q. User",
     *  "quota_info": {
     *    "shared": 37378890,
     *    "quota": 62277025792,
     *    "normal": 263758550
     *   },
     *  "uid": "174"}
     * </pre>
     */
    Account(Map<String, Object> map) {
        country = asString(map, "country");
        displayName = asString(map, "display_name");
        uid = asNumber(map, "uid").longValue();
        referralLink = asString(map, "referral_link");
        
        final Map quotamap = (Map<String, Object>) map.get("quota_info");
        quota = asNumber(quotamap, "quota").longValue();
        quotaNormal = asNumber(quotamap, "normal").longValue();
        quotaShared = asNumber(quotamap, "shared").longValue();
    }
    
    /**
     * The user's ISO country code.
     */
    public String getCountry() {
        return country;
    }
    
    /**
     * The user's "real" name.
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * The user's quota, in bytes.
     */
    public long getQuota() {
        return quota;
    }
    
    /**
     * The user's quota excluding shared files.
     */
    public long getQuotaNormal() {
        return quotaNormal;
    }
    
    /**
     * The user's quota of shared files.
     */
    public long getQuotaShared() {
        return quotaShared;
    }
    
    /**
     * The user's account ID.
     */
    public long getUid() {
        return uid;
    }
    
    /**
     * The url the user can give to get referral credit.
     */
    public String getReferralLink() {
        return referralLink;
    }

    /**
     * Return a human String with all data hold by this instance.
     * Only for debugging.
     */
    @Override
    public String toString() {
        return "Account{" + "country=" + country + ", displayName=" + displayName + 
            ", quota=" + quota + ", quotaNormal=" + quotaNormal + 
            ", quotaShared=" + quotaShared + ", uid=" + uid + 
            ", referralLink=" + referralLink + '}';
    }
 
    
}
