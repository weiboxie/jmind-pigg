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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指明该DAO需要集成cache
 *
 * @author xieweibo
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {

  /**
   * 缓存key前缀
   *
   * @return
   */
  String prefix();

  /**
   * 缓存过期时间,单位秒
   *
   * @return
   */
 int  expire();

 

  /**
   * 是否缓存null对象
   *
   * @return
   */
  boolean cacheNullObject() default false;

  /**
   * 是否缓存空列表
   *
   * @return
   */
  boolean cacheEmptyList() default true;

}
