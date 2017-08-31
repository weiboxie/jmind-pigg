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

import jmind.base.cache.CacheLoader;
import jmind.base.cache.DoubleCheckCache;
import jmind.base.cache.LoadingCache;
import jmind.base.util.ToStringHelper;
import jmind.base.util.reflect.AbstractInvocationHandler;
import jmind.base.util.reflect.ClassUtil;
import jmind.pigg.annotation.Cache;
import jmind.pigg.annotation.DB;
import jmind.pigg.datasource.DataSourceFactory;
import jmind.pigg.datasource.DataSourceFactoryGroup;
import jmind.pigg.datasource.SimpleDataSourceFactory;
import jmind.pigg.descriptor.MethodDescriptor;
import jmind.pigg.descriptor.Methods;
import jmind.pigg.exception.InitializationException;
import jmind.pigg.interceptor.Interceptor;
import jmind.pigg.interceptor.InterceptorChain;
import jmind.pigg.operator.cache.CacheHandler;
import jmind.pigg.stat.*;
import jmind.pigg.util.logging.InternalLogger;
import jmind.pigg.util.logging.InternalLoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * pigg框架DAO工厂
 *
 * @author xieweibo
 */
public class Pigg extends Config {

  private final static InternalLogger logger = InternalLoggerFactory.getInstance(Pigg.class);

  /**
   * 数据源工厂组
   */
  private DataSourceFactoryGroup dataSourceFactoryGroup;

  /**
   * 缓存处理器
   */
  private CacheHandler cacheHandler;



  /**
   * 拦截器链，默认为空
   */
  private InterceptorChain interceptorChain ;

  /**
   * 统计收集器
   */
  private final StatCollector statCollector = new StatCollector();

  /**
   * pigg实例
   */
  private  static  Pigg instance;

  private Pigg() {
  }

  public synchronized static Pigg newInstance() {
      instance= new Pigg();
      return instance;
  }

  public static Pigg newInstance(DataSource dataSource) {
    Pigg pigg = newInstance();
    pigg.setDataSource(dataSource);
    return pigg;
  }

  public static Pigg newInstance(DataSourceFactory dataSourceFactory) {
    Pigg pigg = newInstance();
    pigg.setDataSourceFactory(dataSourceFactory);
    return pigg;
  }



  public static Pigg newInstance(List<DataSourceFactory> dataSourceFactories) {
    Pigg pigg = newInstance();
    pigg.setDataSourceFactories(dataSourceFactories);
    return pigg;
  }



  /**
   * 获得pigg实例
   */
  public static Pigg getInstance() {
    return instance;
  }

  /**
   * 添加拦截器
   */
  public void addInterceptor(Interceptor interceptor) {
    if (interceptor == null) {
      throw new NullPointerException("interceptor can't be null");
    }
    if (interceptorChain == null) {
      interceptorChain = new InterceptorChain();
    }
    interceptorChain.addInterceptor(interceptor);

  }

  /**
   * 创建代理DAO类
   */
  public <T> T create(Class<T> daoClass) {
    if (daoClass == null) {
      throw new NullPointerException("dao interface can't be null");
    }

    if (!daoClass.isInterface()) {
      throw new IllegalArgumentException("expected an interface to proxy, but " + daoClass);
    }

    DB dbAnno = daoClass.getAnnotation(DB.class);
    if (dbAnno == null) {
      throw new IllegalStateException("dao interface expected one @DB " +
          "annotation but not found");
    }

    Cache cacheAnno = daoClass.getAnnotation(Cache.class);
    if (cacheAnno != null && cacheHandler == null) {
      throw new IllegalStateException("if @Cache annotation on dao interface, " +
          "cacheHandler can't be null");
    }

    if (dataSourceFactoryGroup == null) {
      throw new IllegalArgumentException("please set dataSource or dataSourceFactory or dataSourceFactories");
    }

    piggInvocationHandler handler = new piggInvocationHandler(
        daoClass, dataSourceFactoryGroup, cacheHandler, interceptorChain, statCollector, this);
    if (!isLazyInit()) { // 不使用懒加载，则提前加载
      List<Method> methods = Methods.listMethods(daoClass);
      for (Method method : methods) {
        try {
          handler.getOperator(method);
        } catch (Throwable e) {
          throw new InitializationException("initialize " + ToStringHelper.toString(method) + " error", e);
        }
      }
    }

    return ClassUtil.newProxy(daoClass, handler);
  }

  /**
   * 返回状态信息
   */
  public StatInfo getStatInfo() {
    return statCollector.getStatInfo();
  }

  /**
   * 根据数据源工厂名字获得主库数据源
   */
  public DataSource getMasterDataSource(String name) {
    return dataSourceFactoryGroup.getMasterDataSource(name);
  }

  public void setDataSource(DataSource dataSource) {
    if (dataSource == null) {
      throw new NullPointerException("dataSource can't be null");
    }
    setDataSourceFactory(new SimpleDataSourceFactory(dataSource));
  }

  public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
    if (dataSourceFactory == null) {
      throw new NullPointerException("dataSourceFactory can't be null");
    }
    setDataSourceFactories(Arrays.asList(dataSourceFactory));
  }

  public void addDataSourceFactory(DataSourceFactory dataSourceFactory) {
    if (dataSourceFactory == null) {
      throw new NullPointerException("dataSourceFactory can't be null");
    }
    if (dataSourceFactoryGroup == null) {
      dataSourceFactoryGroup = new DataSourceFactoryGroup();
    }
    dataSourceFactoryGroup.addDataSourceFactory(dataSourceFactory);
  }

  public void setDataSourceFactories(List<DataSourceFactory> dataSourceFactories) {
    if (dataSourceFactories == null || dataSourceFactories.isEmpty()) {
      throw new IllegalArgumentException("dataSourceFactories can't be null or empty");
    }
    dataSourceFactoryGroup = new DataSourceFactoryGroup(dataSourceFactories);
  }

  public CacheHandler getCacheHandler() {
    return cacheHandler;
  }

  public void setCacheHandler(CacheHandler cacheHandler) {
    if (cacheHandler == null) {
      throw new NullPointerException("cacheHandler can't be null");
    }
    this.cacheHandler = cacheHandler;
  }



  public void setInterceptorChain(InterceptorChain interceptorChain) {
    if (interceptorChain == null) {
      throw new NullPointerException("interceptorChain can't be null");
    }
    this.interceptorChain = interceptorChain;
  }

  public void setStatMonitor(StatMonitor statMonitor) {
    statCollector.initStatMonitor(statMonitor);
  }

  public void shutDownStatMonitor() {
    statCollector.shutDown();
  }

  private static class piggInvocationHandler extends AbstractInvocationHandler implements InvocationHandler {

    private final Class<?> daoClass;
    private final StatCollector statCollector;
    private final OperatorFactory operatorFactory;
    private final boolean isUseActualParamName;

    private final LoadingCache<Method, Operator> cache = new DoubleCheckCache<Method, Operator>(
        new CacheLoader<Method, Operator>() {
          public Operator load(Method method) {
            if (logger.isInfoEnabled()) {
              logger.info("Initializing operator for {}", ToStringHelper.toString(method));
            }
            CombinedStat combinedStat = statCollector.getCombinedStat(method);
            MetaStat metaStat = combinedStat.getMetaStat();
            InitStat initStat = combinedStat.getInitStat();
            long now = System.nanoTime();
            MethodDescriptor md = Methods.getMethodDescriptor(daoClass, method, isUseActualParamName);
            Operator operator = operatorFactory.getOperator(md, metaStat);
            initStat.recordInit(System.nanoTime() - now);
            metaStat.setDaoClass(daoClass);
            metaStat.setMethod(method);
            metaStat.setSql(md.getSQL());
            return operator;
          }
        });

    private piggInvocationHandler(
        Class<?> daoClass,
        DataSourceFactoryGroup dataSourceFactoryGroup,
        CacheHandler cacheHandler,
        InterceptorChain interceptorChain,
        StatCollector statCollector,
        Config config) {
      this.daoClass = daoClass;
      this.statCollector = statCollector;
      this.isUseActualParamName = config.isUseActualParamName();
      operatorFactory = new OperatorFactory(dataSourceFactoryGroup, cacheHandler, interceptorChain, config);
    }

    @Override
    protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
      if (logger.isDebugEnabled()) {
        logger.debug("Invoking {}", ToStringHelper.toString(method));
      }
      Operator operator = getOperator(method);
      InvocationStat stat = InvocationStat.create();
      try {
        Object r = operator.execute(args, stat);
        return r;
      } finally {
        statCollector.getCombinedStat(method).getExecuteStat().accumulate(stat);
      }
    }

    Operator getOperator(Method method) {
      return cache.get(method);
    }

  }

}
