/* VfsResourceIterator.java
 *
 * Created: 2015-10-17 (Year-Month-Day)
 * Character encoding: UTF-8
 *
 ****************************************** LICENSE *******************************************
 *
 * Copyright (c) 2015 XIAM Solutions B.V. (http://www.xiam.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.infomas.annotation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.jboss.vfs.VirtualFile;

/**
 * {@code VfsResourceIterator} is a {@link ResourceIterator} for the JBoss Virtual File System
 * (VFS) as used by JBoss AS and JBoss WildFly.
 *
 * VFS URL's look like:
 * <code>
 * vfs:/foo/bar/website.war/WEB-INF/classes/nl/dvelop/
 * vfs:/foo/bar/website.war/WEB-INF/lib/dwebcore-0.0.1.jar/nl/dvelop/
 * </code>
 *
 * Known VFS protocols are "vfs", "vfsfile", "vfszip", "vfsjar", and "vfsmemory".
 *
 * Also see
 * <li>https://github.com/rmuller/infomas-asl/issues/29
 * <li>https://github.com/jersey/jersey/pull/100
 *
 * <b>NOTICE</b>: Only tested with WildFly 8.2.0.Final and 9.0.1.Final.
 * 
 * @since annotation-detector 3.0.5
 */
final class VfsResourceIterator implements ResourceIterator {


    private final List<VirtualFile> files;
    private int index = -1;

    VfsResourceIterator(final URL url) throws IOException {
        final VirtualFile vFile = (VirtualFile)url.getContent();
        files = vFile.getChildrenRecursively();
    }

    @Override
    public InputStream next() throws IOException {
        while (true) {
            if (++index >= files.size()) {
                // no files
                return null;
            }
            final VirtualFile f = files.get(index);
            if (f.isFile() && f.getName().endsWith(".class")) {
                return f.openStream();
            }
        }
    }

}
