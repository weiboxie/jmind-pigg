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

import org.junit.Test;

import jmind.pigg.crud.common.builder.CommonDeleteBuilder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author xieweibo
 */
public class CommonDeleteBuilderTest {

  @Test
  public void build() throws Exception {
    CommonDeleteBuilder b = new CommonDeleteBuilder("id2");
    assertThat(b.buildSql(), equalTo("delete from #table where id2 = :1"));
  }

}