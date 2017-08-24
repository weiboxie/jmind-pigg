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

package jmind.pigg.stat;

import org.junit.Test;

import jmind.pigg.stat.InitStat;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author xieweibo
 */
public class InitStatTest {

  @Test
  public void testRecordInit() throws Exception {
    InitStat stat = InitStat.create();
    long a = 1000;
    long b = 500;
    stat.recordInit(a);
    assertThat(stat.getInitCount(), equalTo(1L));
    assertThat(stat.getTotalInitTime(), equalTo(a));
    stat.recordInit(b);
    assertThat(stat.getInitCount(), equalTo(2L));
    assertThat(stat.getTotalInitTime(), equalTo(a + b));
  }

}
