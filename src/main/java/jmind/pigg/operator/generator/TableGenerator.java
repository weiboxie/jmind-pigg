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

package jmind.pigg.operator.generator;

import javax.annotation.Nullable;

import jmind.pigg.binding.InvocationContext;

/**
 * 表名生成器，生成的表名用于替换掉SQL中的#table
 *
 * @author xieweibo
 */
public interface TableGenerator {

  @Nullable
  public String getTable(InvocationContext context);

}
