/*
 *  
 *
 * The jmind-pigg Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package jmind.pigg.util;

import jmind.base.util.DataUtil;
import org.slf4j.helpers.MessageFormatter;



/**
 * @author xieweibo
 */
public class Strings {


  public static String getFullName(String name, String path) {
    return ":" + (DataUtil.isNotEmpty(path) ? name + "." + path : name);
  }

  public static String format(String pattern, Object... arguments) {
    return MessageFormatter.arrayFormat(pattern, arguments).getMessage();
  }



}
