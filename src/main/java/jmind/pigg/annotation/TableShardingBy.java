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

package jmind.pigg.annotation;

import java.lang.annotation.*;

/**
 * 用此注解修饰的方法参数或参数中的某个属性将被作为参数传入
 * {@link jmind.pigg.sharding.TableShardingStrategy#getTargetTable(String, Object)}中
 *
 * @author xieweibo
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TableShardingBy {

  /**
   * 如果value等于""，直接取被修饰的参数<br>
   * 如果value不等于""，取被修饰参数的value属性
   *
   * @return
   */
  String value() default "";

}
