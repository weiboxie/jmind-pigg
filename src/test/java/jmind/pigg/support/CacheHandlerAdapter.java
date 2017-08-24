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

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import jmind.pigg.operator.cache.CacheHandler;

/**
 * @author xieweibo
 */
public class CacheHandlerAdapter implements CacheHandler {

  @Override
  public Object get(String key, Type type, Class<?> daoClass) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, Object> getBulk(Set<String> keys, Type type, Class<?> daoClass) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void set(String key, Object value, int expires, Class<?> daoClass) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(String key, Class<?> daoClass) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(String key, Object value, int expires, Class<?> daoClass) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void batchDelete(Set<String> keys, Class<?> daoClass) {
    throw new UnsupportedOperationException();
  }

}
