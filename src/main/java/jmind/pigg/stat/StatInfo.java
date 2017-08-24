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

import java.util.List;

/**
 * @author xieweibo
 */
public class StatInfo {

  private final long statBeginTime;

  private final long statEndTime;

  private final List<OperatorStat> stats;

  private StatInfo(long statBeginTime, long statEndTime, List<OperatorStat> stats) {
    this.statBeginTime = statBeginTime;
    this.statEndTime = statEndTime;
    this.stats = stats;
  }

  public static StatInfo create(long statBeginTime, long statEndTime, List<OperatorStat> stats) {
    return new StatInfo(statBeginTime, statEndTime, stats);
  }

  public long getStatBeginTime() {
    return statBeginTime;
  }

  public long getStatEndTime() {
    return statEndTime;
  }

  public List<OperatorStat> getStats() {
    return stats;
  }

}
