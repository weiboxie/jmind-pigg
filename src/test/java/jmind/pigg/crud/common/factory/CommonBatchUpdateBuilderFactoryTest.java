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

package jmind.pigg.crud.common.factory;

import com.google.common.collect.Lists;

import jmind.pigg.crud.Builder;
import jmind.pigg.crud.Order;
import jmind.pigg.crud.common.factory.CommonBatchUpdateBuilderFactory;
import jmind.pigg.util.reflect.DynamicTokens;
import jmind.pigg.util.reflect.TypeToken;

import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @author xieweibo
 */
public class CommonBatchUpdateBuilderFactoryTest {

  @Test
  public void test() throws Exception {
    CommonBatchUpdateBuilderFactory factory = new CommonBatchUpdateBuilderFactory();
    String name = "update";
    Class<?> entityClass = Order.class;
    Class<Integer> idClass = Integer.class;
    Type returnType = int[].class;
    List<Type> parameterTypes = Lists.newArrayList(DynamicTokens.collectionToken(TypeToken.of(entityClass)).getType());
    Builder b = factory.doTryGetBuilder(name, returnType, parameterTypes, entityClass, idClass);
    assertThat(b, notNullValue());
    assertThat(b.buildSql(), equalTo("update #table set userid = :userId, user_age = :userAge where id = :id"));
  }

}