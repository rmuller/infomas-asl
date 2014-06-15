/* Reporter.java
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

/**
 * {@code Reporter} is used to report the detected annotations.
 * <p>
 * This interface is a so called "Single Abstract Method" (SAM) or "Functional Interface", so
 * can be used as a Lambda in Java 8 (see examples).
 *
 * @see Builder#report(eu.infomas.annotation.Reporter) 
 *
 * @author <a href="mailto:rmuller@xiam.nl">Ronald K. Muller</a>
 * @since annotation-detector 3.1.0
 */
public interface Reporter {

    /**
     * This method is called when an {@code Annotation} is detected. Invoke methods on the
     * provided {@code Cursor} reference to get more specific information about the
     * {@code Annotation}.
     *
     */
    void report(Cursor cursor);

}
