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

package jmind.pigg.crud.common.builder;

import com.google.common.collect.Lists;

import jmind.pigg.crud.common.builder.CommonSaveBuilder;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


/**
 * @author xieweibo
 */
public class CommonSaveBuilderTest {

  @Test
  public void build() throws Exception {
    List<String> properties = Lists.newArrayList("id", "name", "age");
    List<String> columns = Lists.newArrayList("id2", "name2", "age2");
    CommonSaveBuilder b = new CommonSaveBuilder("id", properties, columns, true);
    assertThat(b.buildSql(), equalTo("insert into #table(name2, age2) values(:name, :age)"));

    properties = Lists.newArrayList("id", "name", "age");
    columns = Lists.newArrayList("id2", "name2", "age2");
    b = new CommonSaveBuilder("id", properties, columns, false);
    assertThat(b.buildSql(), equalTo("insert into #table(id2, name2, age2) values(:id, :name, :age)"));
  }

}