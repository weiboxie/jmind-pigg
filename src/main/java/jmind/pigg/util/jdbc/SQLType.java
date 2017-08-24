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

package jmind.pigg.util.jdbc;

/**
 * @author xieweibo
 */
public enum SQLType {

  /**
   * 增
   */
  INSERT(true),

  /**
   * 删
   */
  DELETE(true),

  /**
   * 改
   */
  UPDATE(true),

  /**
   * 查
   */
  SELECT(false),

  /**
   * mysql中的replace
   */
  REPLACE(true),

  /**
   * oracle中的merge
   */
  MERGE(true),

  /**
   * 清空表
   */
  TRANCATE(true);

  private boolean needChangeData;

  private SQLType(boolean needChangeData) {
    this.needChangeData = needChangeData;
  }

  public boolean needChangeData() {
    return needChangeData;
  }
}
