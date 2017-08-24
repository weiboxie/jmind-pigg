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

package jmind.pigg.invoker.function.json;

import com.alibaba.fastjson.JSON;

import jmind.pigg.invoker.RuntimeSetterFunction;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

/**
 * json --> Object
 *
 * @author xieweibo
 */
public class FastjsonToObjectFunction implements RuntimeSetterFunction<String, Object> {

  @Nullable
  @Override
  public Object apply(@Nullable String input, Type runtimeOutputType) {
    return input == null ? null : JSON.parseObject(input, runtimeOutputType);
  }

}
