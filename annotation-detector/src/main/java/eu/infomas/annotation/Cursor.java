/* Cursor.java
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

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * {@code Cursor} offers a "cursor interface" for working with {@link AnnotationDetector}.
 *
 * @author <a href="mailto:rmuller@xiam.nl">Ronald K. Muller</a>
 * @since annotation-detector 3.1.0
 */
public interface Cursor {

    /**
     * Return the type name of the currently reported Java Class File.
     */
    String getTypeName();

    /**
     * Return the Annotation Type currently reported.
     */
    Class<? extends Annotation> getAnnotationType();

    /**
     * Return the {@code ElementType} of the currently reported {@code Annotation}.
     */
    ElementType getElementType();

    /**
     * Return the member name of the currently reported {@code Annotation}.
     * In case of an annotation on type level, "&lt;clinit&gt;" is reported.
     */
    String getMemberName();

    /**
     * Return the {@link Class type} of the currently reported Java Class File.
     */
    Class<?> getType();

    /**
     * Return the {@link Field} instance of the currently reported annotated Field.
     */
    Field getField();

    /**
     * Return the {@link Method} instance of the currently reported annotated Method.
     */
    Method getMethod();

    /**
     * Return the {@code Annotation} of the reported Annotated Element.
     */
    <T extends Annotation> T getAnnotation(Class<T> annotationClass);

}
