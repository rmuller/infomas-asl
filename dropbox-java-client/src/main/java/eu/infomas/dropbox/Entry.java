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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static eu.infomas.dropbox.Utils.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;

/**
 * {@code Entry} describes the metadata of a Dropbox file or folder. It is just a simple
 * POJO, offering a type safe interface to the JSON response returned by the Dropbox REST
 * API.
 * 
 * @author Original Author is Dropbox
 * @author <a href="mailto:rmuller@xiam.nl">Ronald K. Muller</a> (refactoring)
 */
public final class Entry implements Serializable {

    private static final long serialVersionUID = 1L;

    private final long bytes;
    private final String hash;
    private final String icon;
    private final boolean isDir;
    private final Date modified;
    private final String clientMtime;
    private final String path;
    private final String root;
    private final String size;
    private final String mimeType;
    private final String rev;
    private final boolean thumbExists;
    private final boolean isDeleted;
    private final List<Entry> contents;

    /**
     * Creates an entry from a map.
     * Only called by {@link Dropbox}.
     *
     * @param map the map representation of the JSON received from the metadata call,
     * which should look like this:
     * <pre>
     * {
     *    "hash": "528dda36e3150ba28040052bbf1bfbd1",
     *    "thumb_exists": false,
     *    "bytes": 0,
     *    "modified": "Sat, 12 Jan 2008 23:10:10 +0000",
     *    "path": "/Public",
     *    "is_dir": true,
     *    "size": "0 bytes",
     *    "root": "dropbox",
     *    "contents": [
     *    {
     *        "thumb_exists": false,
     *        "bytes": 0,
     *        "modified": "Wed, 16 Jan 2008 09:11:59 +0000",
     *        "path": "/Public/\u2665asdas\u2665",
     *        "is_dir": true,
     *        "icon": "folder",
     *        "size": "0 bytes"
     *    },
     *    {
     *        "thumb_exists": false,
     *        "bytes": 4392763,
     *        "modified": "Thu, 15 Jan 2009 02:52:43 +0000",
     *        "path": "/Public/\u540d\u79f0\u672a\u8a2d\u5b9a\u30d5\u30a9\u30eb\u30c0.zip",
     *        "is_dir": false,
     *        "icon": "page_white_compressed",
     *        "size": "4.2MB"
     *    }
     *    ],
     *    "icon": "folder_public"
     * }
     * </pre>
     */
    Entry(final Map<String, Object> map) {
        bytes = asNumber(map, "bytes").longValue();
        hash = asString(map, "hash");
        icon = asString(map, "icon");
        isDir = asBoolean(map, "is_dir");
        modified = asDate(map, "modified");
        clientMtime = asString(map, "client_mtime");
        path = asString(map, "path");
        root = asString(map, "root");
        size = asString(map, "size");
        mimeType = asString(map, "mime_type");
        rev = asString(map, "rev");
        thumbExists = asBoolean(map, "thumb_exists");
        isDeleted = asBoolean(map, "is_deleted");

        final List<Map> array = (List<Map>)map.get("contents");
        if (array != null) {
            final List<Entry> tmp = new ArrayList<Entry>(array.size());
            for (final Map element : array) {
                tmp.add(new Entry(element));
            }
            contents = Collections.unmodifiableList(tmp);
        } else {
            contents = Collections.emptyList();
        }
    }
    
    /**
     * Size of the file in bytes.
     */
    public long getBytes() {
        return bytes;
    }
    
    /**
     * If a directory, the hash is its "current version". If the hash changes between
     * calls, then one of the directory's immediate children has changed.
     */
    public String getHash() {
        return hash;
    }
    
    /**
     * Name of the icon to display for this entry. Corresponds to filenames (without an
     * extension) in the icon library available at
     * https://www.dropbox.com/static/images/dropbox-api-icons.zip.
     */
    public String getIcon() {
        return icon;
    }
    
    /**
     * Returns {@code true} if this entry is a directory, or {@code false} if it's a file.
     */
    public boolean isDir() {
        return isDir;
    }
    
    /**
     * Last modified date.
     */
    public Date getModified() {
        return modified;
    }
    
    /**
     * For a file, this is the modification time set by the client when the file was added
     * to Dropbox. Since this time is not verified (the Dropbox server stores whatever the
     * client sends up) this should only be used for display purposes (such as sorting)
     * and not, for example, to determine if a file has changed or not.
     * <br/>
     * NOTE: This value is <b>not</b> set for folders.
     */
    public String getClientMtime() {
        return clientMtime;
    }
    
    /**
     * Path to the file from the root.
     */
    public String getPath() {
        return path;
    }
    
    /**
     * Name of the root, usually either "dropbox" or "app_folder".
     */
    public String getRoot() {
        return root;
    }
    
    /**
     * Human-readable (and localized, if possible) description of the file size.
     */
    public String getSize() {
        return size;
    }
    
    /**
     * The file's MIME type.
     */
    public String getMimeType() {
        return mimeType;
    }
    
    /**
     * Full unique ID for this file's revision. This is a string, and not equivalent to
     * the old revision integer.
     */
    public String getRev() {
        return rev;
    }
    
    /**
     * Whether a thumbnail for this is available.
     */
    public boolean isThumbExists() {
        return thumbExists;
    }
    
    /**
     * Whether this entry has been deleted but not removed from the metadata yet. Most
     * likely you'll only want to show entries with isDeleted == false.
     */
    public boolean isDeleted() {
        return isDeleted;
    }
    
    /**
     * A list of immediate children if this is a directory.
     */
    public List<Entry> getContents() {
        return contents;
    }

    /**
     * Returns the file name if this is a file (the part after the last slash in the
     * path).
     */
    public String fileName() {
        int index = path.lastIndexOf('/');
        return path.substring(index + 1, path.length());
    }

    /**
     * Returns the path of the parent directory if this is a file.
     */
    public String parentPath() {
        if (path.equals("/")) {
            return "";
        } else {
            int index = path.lastIndexOf('/');
            return path.substring(0, index + 1);
        }
    }

    /**
     * Return a human String with all data hold by this instance. Only for debugging.
     */
    @Override
    public String toString() {
        return "Entry{" + "bytes=" + bytes + ", hash=" + hash + ", icon=" + icon +
            ", isDir=" + isDir + ", modified=" + modified + ", clientMtime=" + clientMtime +
            ", path=" + path + ", root=" + root + ", size=" + size +
            ", mimeType=" + mimeType + ", rev=" + rev + ", thumbExists=" + thumbExists +
            ", isDeleted=" + isDeleted + 
            ", contents.size=" + (contents == null ? "null" : contents.size()) + '}';
    }
}
