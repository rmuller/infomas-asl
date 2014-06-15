/* ClassFileIterator.java
 *
 * Created: 2011-10-10 (Year-Month-Day)
 * Character encoding: UTF-8
 *
 ****************************************** LICENSE *******************************************
 *
 * Copyright (c) 2011 - 2013 XIAM Solutions B.V. (http://www.xiam.nl)
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@code ClassFileIterator} is used to iterate over all Java ClassFile files available within
 * a specific context.
 * <p>
 * For every Java ClassFile ({@code .class}) an {@link InputStream} is returned.
 *
 * @author <a href="mailto:rmuller@xiam.nl">Ronald K. Muller</a>
 * @since annotation-detector 3.0.0
 */
final class ClassFileIterator {

    private final FileIterator fileIter;
    private final String[] pkgNameFilter;

    private ZipFileIterator zipIter;
    private boolean isFile;

    /**
     * Create a new {@code ClassFileIterator} returning all Java ClassFile files available
     * from the specified files and/or directories, including sub directories.
     * <p>
     * If the (optional) package filter is defined, only class files staring with one of the
     * defined package names are returned.
     * NOTE: package names must be defined in the native format (using '/' instead of '.').
     */
    ClassFileIterator(final File[] filesOrDirectories, final String[] pkgNameFilter) {
        this.fileIter = new FileIterator(filesOrDirectories);
        this.pkgNameFilter = pkgNameFilter;
    }

    /**
     * Return the name of the Java ClassFile returned from the last call to {@link #next()}.
     * The name is either the path name of a file or the name of an ZIP/JAR file entry.
     */
    String getName() {
        // Both getPath() and getName() are very light weight method calls
        return zipIter == null ?
            fileIter.getFile().getPath() :
            zipIter.getEntry().getName();
    }

    /**
     * Return {@code true} if the current {@link InputStream} is reading from a plain
     * {@link File}.
     * Return {@code false} if the current {@link InputStream} is reading from a
     * ZIP File Entry.
     */
    boolean isFile() {
        return isFile;
    }

    /**
     * Return the next Java ClassFile as an {@code InputStream}.
     * <p>
     * NOTICE: Client code MUST close the returned {@code InputStream}!
     */
    InputStream next(final FilenameFilter filter) throws IOException {
        while (true) {
            if (zipIter == null) {
                final File file = fileIter.next();
                if (file == null) {
                    return null;
                } else {
                    final String path = file.getPath();
                    if (path.endsWith(".class") && (filter == null ||
                        filter.accept(fileIter.getRootFile(), fileIter.relativize(path)))) {
                        isFile = true;
                        return new FileInputStream(file);
                    } else if (fileIter.isRootFile() && endsWithIgnoreCase(path, ".jar")) {
                        zipIter = new ZipFileIterator(file, pkgNameFilter);
                    } // else just ignore
                }
            } else {
                final InputStream is = zipIter.next(filter);
                if (is == null) {
                    zipIter = null;
                } else {
                    isFile = false;
                    return is;
                }
            }
        }
    }

    // private

    private static boolean endsWithIgnoreCase(final String value, final String suffix) {
        final int n = suffix.length();
        return value.regionMatches(true, value.length() - n, suffix, 0, n);
    }

}
