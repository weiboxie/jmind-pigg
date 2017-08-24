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

package jmind.pigg.crud.custom.parser;

import org.junit.Test;

import jmind.pigg.crud.custom.parser.OpUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author xieweibo
 */
public class OpUnitTest {

  @Test
  public void test() throws Exception {
    OpUnit u = OpUnit.create("UserName");
    assertThat(u.getOp().keyword(), equalTo("Equals"));
    assertThat(u.getProperty(), equalTo("userName"));

    u = OpUnit.create("IdIsNull");
    assertThat(u.getOp().keyword(), equalTo("IsNull"));
    assertThat(u.getProperty(), equalTo("id"));

    u = OpUnit.create("UserAgeLessThan");
    assertThat(u.getOp().keyword(), equalTo("LessThan"));
    assertThat(u.getProperty(), equalTo("userAge"));

    u = OpUnit.create("ageBetween");
    assertThat(u.getOp().keyword(), equalTo("Between"));
    assertThat(u.getProperty(), equalTo("age"));
  }

}