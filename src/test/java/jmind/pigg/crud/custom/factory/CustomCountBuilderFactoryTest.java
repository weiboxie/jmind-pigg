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

package jmind.pigg.crud.custom.factory;

import com.google.common.collect.Lists;

import jmind.pigg.crud.Builder;
import jmind.pigg.crud.Order;
import jmind.pigg.crud.custom.factory.CustomCountBuilderFactory;

import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @author xieweibo
 */
public class CustomCountBuilderFactoryTest {

  @Test
  public void test() throws Exception {
    CustomCountBuilderFactory factory = new CustomCountBuilderFactory();
    String name = "countByIdIsNullAndIdLessThanAndUserIdOrUserAgeBetween";
    Class<?> entityClass = Order.class;
    Class<Integer> intClass = Integer.class;
    Type intType = intClass;
    List<Type> types = Lists.newArrayList(intType, intType, intType, intType);
    Builder b = factory.doTryGetBuilder(name, entityClass, types, entityClass, intClass);
    assertThat(b, notNullValue());
    assertThat(b.buildSql(), equalTo("select count(1) from #table where id is null and id < :1 and userid = :2 or user_age between :3 and :4"));
  }

}