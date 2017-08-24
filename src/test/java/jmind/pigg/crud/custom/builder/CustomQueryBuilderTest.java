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

package jmind.pigg.crud.custom.builder;

import com.google.common.collect.Lists;

import jmind.pigg.crud.common.builder.CommonGetBuilder;
import jmind.pigg.crud.custom.builder.CustomQueryBuilder;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author xieweibo
 */
public class CustomQueryBuilderTest {

  @Test
  public void buildSql() throws Exception {
    List<String> columns = Lists.newArrayList("id2", "user_name", "user_age");
    CustomQueryBuilder b = new CustomQueryBuilder(columns, "where id = :1");
    assertThat(b.buildSql(), equalTo("select id2, user_name, user_age from #table where id = :1"));
  }

}