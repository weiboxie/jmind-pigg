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

import jmind.base.util.reflect.ClassUtil;
import jmind.base.util.reflect.TypeToken;
import jmind.base.util.reflect.TypeWrapper;
import jmind.pigg.annotation.Sharding;
import jmind.pigg.annotation.ShardingBy;
import jmind.pigg.annotation.TableShardingBy;
import jmind.pigg.binding.BindingParameter;
import jmind.pigg.binding.BindingParameterInvoker;
import jmind.pigg.binding.ParameterContext;
import jmind.pigg.descriptor.ParameterDescriptor;
import jmind.pigg.exception.DescriptionException;
import jmind.pigg.exception.IncorrectParameterTypeException;
import jmind.pigg.sharding.NotUseShardingStrategy;
import jmind.pigg.sharding.NotUseTableShardingStrategy;
import jmind.pigg.sharding.TableShardingStrategy;


import java.lang.reflect.Type;

/**
 * @author xieweibo
 */
public class TableGeneratorFactory {

  public TableGenerator getTableGenerator(
      @Nullable Sharding shardingAnno,
      @Nullable String table,
      boolean isSqlUseGlobalTable,
      ParameterContext context) {

    TableShardingStrategy strategy = getTableShardingStrategy(shardingAnno);
    TypeToken<?> strategyToken = null;
    if (strategy != null) {
      strategyToken = TypeToken.of(strategy.getClass()).resolveFatherClass(TableShardingStrategy.class);
    }

    // 是否配置使用全局表
    boolean isUseGlobalTable = table != null;

    // 是否配置使用表切分
    boolean isUseTableShardingStrategy = strategy != null;

    if (isSqlUseGlobalTable && !isUseGlobalTable) {
      throw new DescriptionException("if sql use global table '#table'," +
          " @DB.table must be defined");
    }
    if (isUseTableShardingStrategy && !isUseGlobalTable) {
      throw new DescriptionException("if @Sharding.tableShardingStrategy is defined, " +
          "@DB.table must be defined");
    }

    int num = 0;
    String parameterName = null;
    String propertyPath = null;
    for (ParameterDescriptor pd : context.getParameterDescriptors()) {
      TableShardingBy tableShardingByAnno = pd.getAnnotation(TableShardingBy.class);
      if (tableShardingByAnno != null) {
        parameterName = context.getParameterNameByPosition(pd.getPosition());
        propertyPath = tableShardingByAnno.value();
        num++;
        continue; // 有了@TableShardingBy，则忽略@ShardingBy
      }
      ShardingBy shardingByAnno = pd.getAnnotation(ShardingBy.class);
      if (shardingByAnno != null) {
        parameterName = context.getParameterNameByPosition(pd.getPosition());
        propertyPath = shardingByAnno.value();
        num++;
      }
    }
    TableGenerator tableGenerator;
    if (isUseTableShardingStrategy) {
      if (num == 1) {
        BindingParameter bp = BindingParameter.create(parameterName, propertyPath, null);
        BindingParameterInvoker invoker = context.getBindingParameterInvoker(bp);
        Type targetType = invoker.getTargetType();
        TypeWrapper tw = new TypeWrapper(targetType);
        Class<?> mappedClass = tw.getMappedClass();
        if (mappedClass == null || tw.isIterable()) {
          throw new IncorrectParameterTypeException("the type of parameter Modified @TableShardingBy is error, " +
              "type is " + targetType + ", " +
              "please note that @ShardingBy = @TableShardingBy + @DatabaseShardingBy");
        }
        TypeToken<?> shardToken = TypeToken.of(targetType);
        if (!strategyToken.isAssignableFrom(shardToken.wrap())) {
          throw new ClassCastException("TableShardingStrategy[" + strategy.getClass() + "]'s " +
              "generic type[" + strategyToken.getType() + "] must be assignable from " +
              "the type of parameter Modified @TableShardingBy [" + shardToken.getType() + "], " +
              "please note that @ShardingBy = @TableShardingBy + @DatabaseShardingBy");
        }
        tableGenerator = new ShardedTableGenerator(table, invoker, strategy);
      } else {
        throw new DescriptionException("if @Sharding.tableShardingStrategy is defined, " +
            "need one and only one @TableShardingBy on method's parameter but found " + num + ", " +
            "please note that @ShardingBy = @TableShardingBy + @DatabaseShardingBy");
      }
    } else {
      tableGenerator = new SimpleTableGenerator(table);
    }
    return tableGenerator;
  }

  @Nullable
  private TableShardingStrategy getTableShardingStrategy(@Nullable Sharding shardingAnno) {
    if (shardingAnno == null) {
      return null;
    }
    Class<? extends TableShardingStrategy> strategyClass = shardingAnno.tableShardingStrategy();
    if (!strategyClass.equals(NotUseTableShardingStrategy.class)) {
      TableShardingStrategy strategy = ClassUtil.instantiateClass(strategyClass);
      return strategy;
    }
    strategyClass = shardingAnno.shardingStrategy();
    if (!strategyClass.equals(NotUseShardingStrategy.class)) {
      TableShardingStrategy strategy = ClassUtil.instantiateClass(strategyClass);
      return strategy;
    }
    return null;
  }

}
