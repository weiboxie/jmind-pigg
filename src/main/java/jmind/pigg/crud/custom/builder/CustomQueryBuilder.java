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

import java.util.List;

import jmind.pigg.util.Joiner;

/**
 * @author xieweibo
 */
public class CustomQueryBuilder extends AbstractCustomBuilder {

  private final static String SQL_TEMPLATE = "select %s from #table %s";

  private final List<String> columns;

  private final String tailOfSql;

  public CustomQueryBuilder(List<String> columns, String tailOfSql) {
    this.columns = columns;
    this.tailOfSql = tailOfSql;
  }

  @Override
  public String buildSql() {
    String s1 = Joiner.on(", ").join(columns);
    return String.format(SQL_TEMPLATE, s1, tailOfSql);
  }

}
