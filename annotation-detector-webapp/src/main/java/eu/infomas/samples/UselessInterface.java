/* UselessInterface.java
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
package eu.infomas.samples;

import javax.inject.Named;

/**
 * {@code UselessInterface} is only used for testing the INFOMAS annotation detector.
 *
 * @author <a href="mailto:rmuller@xiam.nl">Ronald K. Muller</a>
 */
@FunctionalInterface
public interface UselessInterface {

    @Named("sample")
    void justDoIt(String actionName);

}
