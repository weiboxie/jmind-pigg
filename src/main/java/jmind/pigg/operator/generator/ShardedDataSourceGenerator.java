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

import jmind.pigg.binding.BindingParameterInvoker;
import jmind.pigg.binding.InvocationContext;
import jmind.pigg.datasource.DataSourceFactoryGroup;
import jmind.pigg.datasource.DataSourceType;
import jmind.pigg.sharding.DatabaseShardingStrategy;

/**
 * @author xieweibo
 */
public class ShardedDataSourceGenerator extends AbstractDataSourceGenerator {

  private final BindingParameterInvoker bindingParameterInvoker;
  private final DatabaseShardingStrategy databaseShardingStrategy;

  protected ShardedDataSourceGenerator(
      DataSourceFactoryGroup dataSourceFactoryGroup,
      DataSourceType dataSourceType,
      BindingParameterInvoker bindingParameterInvoker,
      DatabaseShardingStrategy databaseShardingStrategy) {
    super(dataSourceFactoryGroup, dataSourceType);
    this.bindingParameterInvoker = bindingParameterInvoker;
    this.databaseShardingStrategy = databaseShardingStrategy;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String getDataSourceFactoryName(InvocationContext context) {
    Object shardParam = context.getBindingValue(bindingParameterInvoker);
    return databaseShardingStrategy.getDataSourceFactoryName(shardParam);
  }

}
