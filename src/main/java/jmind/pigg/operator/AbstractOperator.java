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

import jmind.pigg.annotation.UseTransactionForBatchUpdate;
import jmind.pigg.binding.InvocationContextFactory;
import jmind.pigg.descriptor.MethodDescriptor;
import jmind.pigg.interceptor.InvocationInterceptorChain;
import jmind.pigg.jdbc.JdbcOperations;
import jmind.pigg.operator.generator.DataSourceGenerator;
import jmind.pigg.operator.generator.TableGenerator;
import jmind.pigg.parser.ASTRootNode;

/**
 * @author xieweibo
 */
public abstract class AbstractOperator implements Operator {

  /**
   * 渲染sql的树节点
   */
  protected final ASTRootNode rootNode;

  /**
   * dao操作所在类
   */
  protected final Class<?> daoClass;

  /**
   * 拦截器链
   */
  protected InvocationInterceptorChain invocationInterceptorChain;

  /**
   * 全局表
   */
  protected TableGenerator tableGenerator;

  /**
   * 数据源
   */
  protected DataSourceGenerator dataSourceGenerator;

  /**
   * 运行时环境工厂
   */
  protected InvocationContextFactory invocationContextFactory;

  /**
   * jdbc操作
   */
  protected JdbcOperations jdbcOperations;

  /**
   * pigg全局配置信息
   */
  protected final Config config;

  /**
   * 用于对db进行操作
   */
  protected AbstractOperator(ASTRootNode rootNode, MethodDescriptor md, Config config) {
    this.rootNode = rootNode;
    this.daoClass = md.getDaoClass();
    this.config = config.copy();
    mergeConfig(md);
  }

  public void setJdbcOperations(JdbcOperations jdbcOperations) {
    this.jdbcOperations = jdbcOperations;
  }

  public void setInvocationContextFactory(InvocationContextFactory invocationContextFactory) {
    this.invocationContextFactory = invocationContextFactory;
  }

  public void setTableGenerator(TableGenerator tableGenerator) {
    this.tableGenerator = tableGenerator;
  }

  public void setDataSourceGenerator(DataSourceGenerator dataSourceGenerator) {
    this.dataSourceGenerator = dataSourceGenerator;
  }

  public void setInvocationInterceptorChain(InvocationInterceptorChain invocationInterceptorChain) {
    this.invocationInterceptorChain = invocationInterceptorChain;
  }

  private void mergeConfig(MethodDescriptor md) {
    UseTransactionForBatchUpdate anno = md.getAnnotation(UseTransactionForBatchUpdate.class);
    if (anno != null) {
      config.setUseTransactionForBatchUpdate(anno.value());
    }
  }

}
