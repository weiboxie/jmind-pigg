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

import java.util.ArrayList;
import java.util.List;

import jmind.pigg.crud.Builder;
import jmind.pigg.util.Joiner;

/**
 * @author xieweibo
 */
public class CommonSaveBuilder implements Builder {

  private final static String SQL_TEMPLATE = "insert into #table(%s) values(%s)";

  private final List<String> properties;

  private final List<String> columns;

  public CommonSaveBuilder(String propId, List<String> props,
                          List<String> cols, boolean isAutoGenerateId) {
    int index = props.indexOf(propId);
    if (index < 0) {
      throw new IllegalArgumentException("error property id [" + propId + "]");
    }
    properties = new ArrayList<String>(props);
    columns = new ArrayList<String>(cols);
    if (isAutoGenerateId) {
      properties.remove(index);
      columns.remove(index);
    }
  }

  @Override
  public String buildSql() {
    String s1 = Joiner.on(", ").join(columns);
    List<String> cps = new ArrayList<String>();
    for (String prop : properties) {
      cps.add(":" + prop);
    }
    String s2 = Joiner.on(", ").join(cps);
    return String.format(SQL_TEMPLATE, s1, s2);
  }

}
