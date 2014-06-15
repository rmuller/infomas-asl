/* Builder.java
 *
 * Created: 2014-06-15 (Year-Month-Day)
 * Character encoding: UTF-8
 *
 ****************************************** LICENSE *******************************************
 *
 * Copyright (c) 2014 XIAM Solutions B.V. (http://www.xiam.nl)
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

import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.util.List;

/**
 * {@code Builder} offers a fluent API for using {@link AnnotationDetector}.
 * Its only role is to offer a more clean API.
 *
 * @author <a href="mailto:rmuller@xiam.nl">Ronald K. Muller</a>
 * @since annotation-detector 3.1.0
 */
public interface Builder {

    /**
     * Specify the annotation types to report.
     */
    Builder forAnnotations(final Class<? extends Annotation>... a);

    /**
     * Specify the Element Types to scan. If this method is not called,
     * {@link ElementType#TYPE} is used as default.
     * <p>
     * Valid types are:
     * <ul>
     * <li>{@link ElementType#TYPE}
     * <li>{@link ElementType#METHOD}
     * <li>{@link ElementType#FIELD}
     * </ul>
     * An {@code IllegalArgumentException} is thrown if another Element Type is specified or
     * no types are specified.
     */
    Builder on(final ElementType... types);

    /**
     * Filter the scanned Class Files based on its name and the directory or jar file it is
     * stored.
     * <p>
     * If the Class File is stored as a single file in the file system the {@code File}
     * argument in {@link FilenameFilter#accept(java.io.File, java.lang.String) } is the
     * absolute path to the root directory scanned.
     * <p>
     * If the Class File is stored in a jar file the {@code File} argument in
     * {@link FilenameFilter#accept(java.io.File, java.lang.String)} is the absolute path of
     * the jar file.
     * <p>
     * The {@code String} argument is the full name of the ClassFile in native format,
     * including package name, like {@code eu/infomas/annotation/AnnotationDetector$1.class}.
     * <p>
     * Note that all non-Class Files are already filtered and not seen by the filter.
     *
     * @param filter The filter, never {@code null}
     */
    Builder filter(final FilenameFilter filter);

    /**
     * Report the detected annotations to the specified {@code Reporter} instance.
     * 
     * @see Reporter#report(eu.infomas.annotation.Cursor) 
     * @see #collect(eu.infomas.annotation.ReporterFunction) 
     */
    void report(final Reporter reporter) throws IOException;

    /**
     * Report the detected annotations to the specified {@code ReporterFunction} instance and 
     * collect the returned values of
     * {@link ReporterFunction#report(eu.infomas.annotation.Cursor) }.
     * The collected values are returned as a {@code List}.
     * 
     * @see #report(eu.infomas.annotation.Reporter) 
     */
    <T> List<T> collect(final ReporterFunction<T> reporter) throws IOException;

}
