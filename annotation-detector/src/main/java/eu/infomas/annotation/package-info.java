/* package-info.java
 *
 * Created: 2013-07-09 (Year-Month-Day)
 * Character encoding: UTF-8
 *
 ****************************************** LICENSE *******************************************
 *
 * Copyright (c) 2013 XIAM Solutions B.V. (http://www.xiam.nl)
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
/**
 * This library can be used to scan (part of) the class path for annotated classes, 
 * methods or instance variables. 
 * Main advantages of this library compared with similar solutions are: 
 * <ul>
 * <li>light weight (**no dependencies**, simple API, 16 kb jar file) 
 * <li>very fast (fastest annotation detection library as far as I know).
 * </ul>
 * <h4>Maven configuration</h4>
 * <pre>
 * &lt;dependency>
 *    &lt;groupId>eu.infomas&lt;/groupId>
 *    &lt;artifactId>annotation-detector&lt;/artifactId>
 *    &lt;version>3.0.2&lt;/version>
 * &lt;/dependency>
 * </pre>
 * <h4>Example Usage</h4>
 * <p>
 * Put the {@code annotation-detector-3.0.2.jar} in the class path. No other dependencies are 
 * required! 
 * You can either scan the complete class path or only scan specified packages (see JavaDoc 
 * for more details).
 * <pre>
 * // Scan all .class files on the class path
 * // Report all .class files, with org.junit.Test annotated methods
 * final MethodReporter reporter = new MethodReporter() {
 * 
 *   {@literal @}SuppressWarnings("unchecked")
 *   {@literal @}Override
 *   public Class<? extends Annotation>[] annotations() {
 *     return new Class[]{Test.class};
 *   } 
 * 
 *   {@literal @}Override
 *   public void reportMethodAnnotation(Class<? extends Annotation> annotation,
 *     String className, String methodName) {
 *     // do something
 * 
 *   }
 * };
 * final AnnotationDetector cf = new AnnotationDetector(reporter);
 * cf.detect();
 * </pre>
 */
package eu.infomas.annotation;
