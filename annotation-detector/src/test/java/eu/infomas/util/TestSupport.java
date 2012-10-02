/* TestSupport.java
 * 
 ******************************************************************************
 *
 * Created: Oct 02, 2012
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
package eu.infomas.util;

/**
 * {@code TestSupport} offers some simple utility methods useful during development and
 * testing.
 *
 * @author <a href="mailto:rmuller@xiam.nl">Ronald K. Muller</a>
 * @since annotation-detector 3.0.2
 */
public final class TestSupport {

    // utility class
    private TestSupport() {
    } 

    /**
     * Return detailed information about the current Java VM. 
     * Output should more or less the same as running the 'java' command in your OS shell:
     * <pre>
     * $ java -version
     * java version "1.7.0_07"
     * Java(TM) SE Runtime Environment (build 1.7.0_07-b10)
     * Java HotSpot(TM) 64-Bit Server VM (build 23.3-b01, mixed mode)
     * </pre>
     */
    public static String javaVersion() {
        return String.format("java version \"%s\" (%s)\n%s (build %s)\n%s (build %s, %s)",
            System.getProperty("java.version"),
            System.getProperty("java.home"),
            System.getProperty("java.runtime.name"),
            System.getProperty("java.runtime.version"),
            System.getProperty("java.vm.name"),
            System.getProperty("java.vm.version"),
            System.getProperty("java.vm.info"));
    }
    
    /**
     * Minimalistic logger: log the String value of the supplied argument or print the
     * stack trace if it is an {@link Throwable}.
     */
    public static void log(final Object log) {
        if (log instanceof Throwable) {
            ((Throwable)log).printStackTrace(System.err);
        } else {
            System.out.println(String.valueOf(log));
        }
    }
    
    /**
     * Minimalistic logger: format the supplied message using 
     * {@link String#format(java.lang.String, java.lang.Object[]) }.
     */
    public static void log(final String msg, final Object... args) {
        System.out.println(args.length == 0 ? msg : String.format(msg, args));
    }
}
