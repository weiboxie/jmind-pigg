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

package jmind.pigg.operator.generator;

import javax.annotation.Nullable;

import jmind.pigg.binding.BindingParameterInvoker;
import jmind.pigg.binding.InvocationContext;
import jmind.pigg.sharding.TableShardingStrategy;

/**
 * 分表表名生成器，以从{@link jmind.pigg.annotation.DB#table()}取得的表名作为原始表名，
 * 使用{@link jmind.pigg.annotation.TableShardingBy}或{@link jmind.pigg.annotation.ShardingBy}
 * 修饰的参数作为分表参数，
 * 使用{@link jmind.pigg.sharding.TableShardingStrategy}作为分表策略，
 * 共同生成分表后表名
 *
 * @author xieweibo
 */
public class ShardedTableGenerator implements TableGenerator {

  private final String table; // 原始表名称
  private final BindingParameterInvoker bindingParameterInvoker; // 绑定参数执行器
  private final TableShardingStrategy tableShardingStrategy; // 分表策略

  public ShardedTableGenerator(
      String table, BindingParameterInvoker bindingParameterInvoker, TableShardingStrategy tableShardingStrategy) {
    this.table = table;
    this.bindingParameterInvoker = bindingParameterInvoker;
    this.tableShardingStrategy = tableShardingStrategy;
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public String getTable(InvocationContext context) {
    Object shardParam = context.getBindingValue(bindingParameterInvoker);
    return tableShardingStrategy.getTargetTable(table, shardParam);
  }

}
