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

package jmind.pigg.invoker.function.enums;

import javax.annotation.Nullable;

import jmind.pigg.invoker.GetterFunction;

/**
 * Enum --> Integer
 *
 * @author xieweibo
 */
public class EnumToIntegerFunction implements GetterFunction<Enum, Integer> {

  @Nullable
  @Override
  public Integer apply(@Nullable Enum input) {
    return input == null ? null : input.ordinal();
  }

}
