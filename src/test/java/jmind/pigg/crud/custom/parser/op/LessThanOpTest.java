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

package jmind.pigg.crud.custom.parser.op;

import org.junit.Test;

import jmind.pigg.crud.custom.parser.op.LessThanOp;
import jmind.pigg.crud.custom.parser.op.Op;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author xieweibo
 */
public class LessThanOpTest {

  @Test
  public void test() throws Exception {
    Op op = new LessThanOp();
    assertThat(op.keyword(), equalTo("LessThan"));
    assertThat(op.paramCount(), equalTo(1));
    assertThat(op.render("id", new String[] {":1"}), equalTo("id < :1"));
  }

}