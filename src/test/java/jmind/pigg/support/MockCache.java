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

package jmind.pigg.support;

import java.lang.annotation.Annotation;

import jmind.pigg.annotation.Cache;

/**
 * @author xieweibo
 */
public class MockCache implements Annotation, Cache {

  private String prefix;
  private int expire;

  public MockCache(String prefix, int expire) {
    this.prefix = prefix;
    this.expire = expire;
  }

 

  @Override
  public String prefix() {
    return prefix;
  }

  @Override
  public int expire() {
    return expire;
  }

 
  @Override
  public boolean cacheNullObject() {
    return false;
  }

  @Override
  public boolean cacheEmptyList() {
    return true;
  }

  @Override
  public Class<? extends Annotation> annotationType() {
    throw new UnsupportedOperationException();
  }

}
