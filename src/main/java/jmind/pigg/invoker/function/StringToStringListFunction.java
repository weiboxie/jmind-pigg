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

package jmind.pigg.invoker.function;

import javax.annotation.Nullable;

import jmind.pigg.invoker.SetterFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * String --> List<String>
 *
 * @author xieweibo
 */
public class StringToStringListFunction implements SetterFunction<String, List<String>> {

  private final static String SEPARATOR = ",";

  @Nullable
  @Override
  public List<String> apply(@Nullable String input) {
    if (input == null) {
      return null;
    }
    if (input.length() == 0) {
      return new ArrayList<String>();
    }
    String[] ss = input.split(SEPARATOR);
    List<String> r = new ArrayList<String>();
    for (int i = 0; i < ss.length; i++) {
      r.add(ss[i]);
    }
    return r;
  }

}
