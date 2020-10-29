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

package jmind.pigg.operator;

import javax.sql.DataSource;

import jmind.base.util.ToStringHelper;
import jmind.pigg.binding.BoundSql;
import jmind.pigg.binding.InvocationContext;
import jmind.pigg.descriptor.MethodDescriptor;
import jmind.pigg.exception.DescriptionException;
import jmind.pigg.jdbc.GeneratedKeyHolder;
import jmind.pigg.jdbc.JdbcOperationsFactory;
import jmind.pigg.parser.ASTRootNode;
import jmind.pigg.parser.EmptyObjectException;
import jmind.pigg.stat.InvocationStat;
import jmind.pigg.type.TypeHandler;
import jmind.pigg.type.TypeHandlerRegistry;
import jmind.pigg.util.jdbc.SQLType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xieweibo
 */
public class UpdateOperator extends AbstractOperator {

  private boolean returnGeneratedId;

  private Transformer transformer;

  private TypeHandler<? extends Number> generatedKeyTypeHandler;

  public UpdateOperator(ASTRootNode rootNode, MethodDescriptor md, Config config) {
    super(rootNode, md, config);
    init(md, rootNode.getSQLType());
  }

  private void init(MethodDescriptor md, SQLType sqlType) {
    returnGeneratedId = md.isReturnGeneratedId() // 要求返回自增id
        && (sqlType == SQLType.INSERT||sqlType==SQLType.REPLACE); // 是插入语句

    Class<?> returnRawType = md.getReturnRawType();
    if (returnGeneratedId) {
      GeneratedTransformer gt = GENERATED_TRANSFORMERS.get(returnRawType);
      if (gt == null) {
        String expected = ToStringHelper.toString(GENERATED_TRANSFORMERS.keySet());
        throw new DescriptionException("the return type of update(returnGeneratedId) " +
            "expected one of " + expected + " but " + returnRawType);
      }
      generatedKeyTypeHandler = TypeHandlerRegistry.getTypeHandler(gt.getRawType());
      transformer = gt;
    } else {
      transformer = TRANSFORMERS.get(returnRawType);
      if (transformer == null) {
        String expected = ToStringHelper.toString(TRANSFORMERS.keySet());
        throw new DescriptionException("the return type of update " +
            "expected one of " + expected + " but " + returnRawType);
      }
    }
  }

  @Override
  public Object execute(Object[] values, InvocationStat stat) {
    InvocationContext context = invocationContextFactory.newInvocationContext(values);
    return execute(context, stat);
  }

  public Object execute(InvocationContext context, InvocationStat stat) {
    context.setGlobalTable(tableGenerator.getTable(context));

    try {
      rootNode.render(context);
    } catch (EmptyObjectException e) {
      if (config.isCompatibleWithEmptyList()) {
        return transformer.transform(0);
      } else {
        throw e;
      }
    }


    DataSource ds = dataSourceGenerator.getDataSource(context, daoClass);
    invocationInterceptorChain.preIntercept(context, methodDescriptor,ds);  // 拦截器
    Number r = executeDb(ds, context.getBoundSql(), stat);
    invocationInterceptorChain.postIntercept(context,methodDescriptor,ds, r);  // 拦截器
    return transformer.transform(r);
  }

  private Number executeDb(DataSource ds, BoundSql boundSql, InvocationStat stat) {
    Number r = null;
    long now = System.nanoTime();
    try {
      if (returnGeneratedId) {
        GeneratedKeyHolder holder = new GeneratedKeyHolder(generatedKeyTypeHandler);
        JdbcOperationsFactory.getJdbcOperations().update(ds, boundSql, holder);
        r = holder.getKey();
      } else {
        r = JdbcOperationsFactory.getJdbcOperations().update(ds, boundSql);
      }
    } finally {
      long cost = System.nanoTime() - now;
      if (r != null) {
        stat.recordDatabaseExecuteSuccess(cost);
      } else {
        stat.recordDatabaseExecuteException(cost);
      }
    }
    return r;
  }

  /**
   * 更新操作支持的返回类型
   */
  private final static Map<Class, Transformer> TRANSFORMERS = new LinkedHashMap<Class, Transformer>();

  static {
    TRANSFORMERS.put(void.class, VoidTransformer.INSTANCE);
    TRANSFORMERS.put(int.class, IntegerTransformer.INSTANCE);
    TRANSFORMERS.put(long.class, LongTransformer.INSTANCE);
    TRANSFORMERS.put(boolean.class, BooleanTransformer.INSTANCE);
    TRANSFORMERS.put(Void.class, VoidTransformer.INSTANCE);
    TRANSFORMERS.put(Integer.class, IntegerTransformer.INSTANCE);
    TRANSFORMERS.put(Long.class, LongTransformer.INSTANCE);
    TRANSFORMERS.put(Boolean.class, BooleanTransformer.INSTANCE);
  }

  /**
   * 生成自增id的更新操作支持的返回类型
   */
  private final static Map<Class, GeneratedTransformer> GENERATED_TRANSFORMERS =
      new LinkedHashMap<Class, GeneratedTransformer>();

  static {
    GENERATED_TRANSFORMERS.put(int.class, IntegerTransformer.INSTANCE);
    GENERATED_TRANSFORMERS.put(long.class, LongTransformer.INSTANCE);
    GENERATED_TRANSFORMERS.put(Integer.class, IntegerTransformer.INSTANCE);
    GENERATED_TRANSFORMERS.put(Long.class, LongTransformer.INSTANCE);
  }

  interface Transformer {
    Object transform(Number n);
  }

  interface GeneratedTransformer extends Transformer {
    Class<? extends Number> getRawType();
  }

  enum IntegerTransformer implements GeneratedTransformer {
    INSTANCE;

    @Override
    public Object transform(Number n) {
      return n.intValue();
    }

    @Override
    public Class<? extends Number> getRawType() {
      return int.class;
    }
  }

  enum LongTransformer implements GeneratedTransformer {
    INSTANCE;

    @Override
    public Object transform(Number n) {
      return n.longValue();
    }

    @Override
    public Class<? extends Number> getRawType() {
      return long.class;
    }
  }

  enum VoidTransformer implements Transformer {
    INSTANCE;

    @Override
    public Object transform(Number n) {
      return null;
    }
  }

  enum BooleanTransformer implements Transformer {
    INSTANCE;

    @Override
    public Object transform(Number n) {
      return n.intValue() > 0 ? Boolean.TRUE : Boolean.FALSE;
    }
  }

}
