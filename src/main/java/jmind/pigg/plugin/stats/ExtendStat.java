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

package jmind.pigg.plugin.stats;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.Sharding;
import jmind.pigg.sharding.NotUseTableShardingStrategy;
import jmind.pigg.stat.OperatorStat;
import jmind.pigg.util.Strings;
import jmind.pigg.util.ToStringHelper;

/**
 * @author xieweibo
 */
public class ExtendStat {

  private OperatorStat operatorStat;

  private Method method;

  public ExtendStat(OperatorStat operatorStat) {
    this.operatorStat = operatorStat;
    this.method = operatorStat.getMethod();
  }

  public String getSimpleClassName() {
    return operatorStat.getDaoClass().getSimpleName();
  }

  public String getSimpleMethodName() {
    return method.getName() + "(" + method.getParameterTypes().length + ")";
  }

  public String getSql() {
    String sql = operatorStat.getSql();
    DB dbAnno = operatorStat.getDaoClass().getAnnotation(DB.class);
    String table = dbAnno.table();
    if (Strings.isNotEmpty(table)) {
      Sharding shardingAnno = method.getAnnotation(Sharding.class);
      if (shardingAnno == null) {
        shardingAnno = operatorStat.getDaoClass().getAnnotation(Sharding.class);
      }
      if (shardingAnno != null &&
          !NotUseTableShardingStrategy.class.equals(shardingAnno.tableShardingStrategy())) {
        table = table + "_#";
      }
      sql = sql.replaceAll("#table", table);
    }
    return sql;
  }

  public List<String> getStrParameterTypes() {
    List<String> r = new ArrayList<String>();
    for (Type type : method.getGenericParameterTypes()) {
      r.add(ToStringHelper.toString(type));
    }
    return r;
  }

  public String getType() {
    return operatorStat.getOperatorType().name().toLowerCase();
  }

}
