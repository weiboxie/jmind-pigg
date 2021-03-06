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

package jmind.pigg.operator.cache;

import java.util.*;

import jmind.base.util.CollectionUtil;
import jmind.base.util.DataUtil;
import jmind.base.util.Iterables;
import jmind.pigg.binding.BindingException;
import jmind.pigg.binding.InvocationContext;
import jmind.pigg.descriptor.MethodDescriptor;
import jmind.pigg.exception.DescriptionException;
import jmind.pigg.invoker.GetterInvoker;
import jmind.pigg.invoker.InvokerCache;
import jmind.pigg.operator.Config;
import jmind.pigg.operator.QueryOperator;
import jmind.pigg.parser.ASTJDBCIterableParameter;
import jmind.pigg.parser.ASTRootNode;
import jmind.pigg.stat.InvocationStat;

import jmind.pigg.util.logging.InternalLogger;
import jmind.pigg.util.logging.InternalLoggerFactory;

/**
 * @author xieweibo
 */
public class CacheableQueryOperator extends QueryOperator {

  private final static InternalLogger logger = InternalLoggerFactory.getInstance(CacheableUpdateOperator.class);

  private CacheDriver driver;

  GetterInvoker propertyOfMapperInvoker;

  public CacheableQueryOperator(ASTRootNode rootNode, MethodDescriptor md, CacheDriver cacheDriver, Config config) {
    super(rootNode, md, config);

    this.driver = cacheDriver;

    List<ASTJDBCIterableParameter> jips = rootNode.getJDBCIterableParameters();
    if (jips.size() > 1) {
      throw new DescriptionException("if use cache, sql's in clause expected less than or equal 1 but " +
          jips.size()); // sql??????????????????in??????
    }

    if (driver.isUseMultipleKeys()) {
      String propertyOfMapper = driver.getPropertyOfMapper().toLowerCase(); //??????????????????
      List<GetterInvoker> invokers = InvokerCache.getGetterInvokers(returnDescriptor.getMappedClass());
      for (GetterInvoker invoker : invokers) {
        if (DataUtil.underscoreName(invoker.getName()).equals(propertyOfMapper)) {
          propertyOfMapperInvoker = invoker;
        }
      }
      if (propertyOfMapperInvoker == null) {
        // ????????????cache??????sql????????????in?????????mappedClass?????????????????????????????????a in (...)??????mappedClass????????????a??????
        throw new BindingException("if use cache and sql has one in clause, property "
            + propertyOfMapper + " of " + returnDescriptor.getMappedClass() + " expected readable but not");
      }
    }
  }

  @Override
  public Object execute(Object[] values, InvocationStat stat) {
    InvocationContext context = invocationContextFactory.newInvocationContext(values);
    return driver.isUseMultipleKeys() ?
        multipleKeysCache(context, rowMapper.getMappedClass(), driver.getOnlyCacheByClass(), stat) :
        singleKeyCache(context, stat);
  }

  private <T, U> Object multipleKeysCache(InvocationContext context, Class<T> mappedClass,
                                          Class<U> cacheByActualClass, InvocationStat stat) {
    boolean isDebugEnabled = logger.isDebugEnabled();
    boolean isCacheNullObj = driver.isCacheNullObject();
    Set<String> keys = driver.getCacheKeys(context);
    if (keys.isEmpty()) {
      return EmptyObject();
    }

    // ???????????????????????????
    Map<String, Object> cachedResults = driver.getBulkFromCache(keys, stat);

    AddableObject<T> addableObj = new AddableObject<T>(mappedClass); // ???????????????????????????
    int hitNum = 0;

    // ?????? debug log
    List<String> hitKeys = isDebugEnabled ? new ArrayList<String>() : null;
    List<String> missKeys = isDebugEnabled ? new ArrayList<String>() : null;

    // ???????????????????????????????????????
    Set<U> missCacheByActualObjs = new HashSet<U>();
    for (Object cacheByActualObj : new Iterables(driver.getOnlyCacheByObj(context))) {
      String key = driver.getCacheKey(cacheByActualObj);
      Object value = cachedResults != null ? cachedResults.get(key) : null;
      if (value == null) {
        missCacheByActualObjs.add(cacheByActualClass.cast(cacheByActualObj));
        if (isDebugEnabled) {
          missKeys.add(key);
        }
      } else {
        hitNum++;
        if (!isNullObject(value)) {
          addableObj.add(mappedClass.cast(value));
        }
        if (isDebugEnabled) {
          hitKeys.add(key);
        }
      }
    }
    stat.recordHits(hitNum);
    stat.recordMisses(missCacheByActualObjs.size());
    if (isDebugEnabled) {
      if (!hitKeys.isEmpty()) {
        logger.debug("Cache hit for multiple keys {}", hitKeys);
      }
      if (!missKeys.isEmpty()) {
        logger.debug("Cache miss for multiple keys {}", missKeys);
      }
    }

    if (!missCacheByActualObjs.isEmpty()) { // ???????????????????????????
      driver.setOnlyCacheByObj(context, missCacheByActualObjs);
      Object dbValues = execute(context, stat);

      // ?????? debug log
      List<String> needSetKeys = isDebugEnabled ? new ArrayList<String>() : null;
      for (Object dbValue : new Iterables(dbValues)) {
        // db?????????????????????
        addableObj.add(mappedClass.cast(dbValue));
        // ???????????????
        Object propertyObj = propertyOfMapperInvoker.invoke(dbValue);
        if (propertyObj == null) {
          throw new NullPointerException("property " + propertyOfMapperInvoker.getName() + " of " +
              mappedClass + " is null, please check return type");
        }
        U cacheByActualObj = cacheByActualClass.cast(propertyObj);
        String key = driver.getCacheKey(cacheByActualObj);
        driver.setToCache(key, dbValue, stat);
        if (isCacheNullObj) {
          missCacheByActualObjs.remove(cacheByActualObj);
        }
        if (isDebugEnabled) {
          needSetKeys.add(key);
        }
      }
      if (isDebugEnabled && !needSetKeys.isEmpty()) {
        logger.debug("Cache set for multiple keys {}, exptime: {}",
            needSetKeys, driver.getExptimeSeconds());
      }

      if (isCacheNullObj && !missCacheByActualObjs.isEmpty()) {
        // ?????? debug log
        List<String> needAddKeys = isDebugEnabled ? new ArrayList<String>() : null;
        for (U missCacheByActualObj : missCacheByActualObjs) {
          String key = driver.getCacheKey(missCacheByActualObj);
          driver.addToCache(key, createNullObject(), stat);
          if (isDebugEnabled) {
            needAddKeys.add(key);
          }
        }
        if (isDebugEnabled && !needAddKeys.isEmpty()) {
          logger.debug("Cache add for multiple keys {}, exptime: {}",
              needAddKeys, driver.getExptimeSeconds());
        }
      }
    }
    return addableObj.getReturn();
  }

  private Object singleKeyCache(InvocationContext context, InvocationStat stat) {
    boolean isDebugEnabled = logger.isDebugEnabled();
    String key = driver.getCacheKey(context);
    Object value = driver.getFromCache(key, stat);
    if (value == null) {
      stat.recordMisses(1);
      if (isDebugEnabled) {
        logger.debug("Cache miss for single key [{}]", key);
      }
      value = execute(context, stat);
      if (value != null) {
        if (driver.isCacheEmptyList() || isNotEmptyList(value)) {
          driver.setToCache(key, value, stat);
          if (isDebugEnabled) {
            logger.debug("Cache set for single key [{}], exptime: {}",
                key, driver.getExptimeSeconds());
          }
        }
      } else if (driver.isCacheNullObject()) { // ??????null??????
        driver.addToCache(key, createNullObject(), stat);
        if (isDebugEnabled) {
          logger.debug("Cache add for single key [{}], exptime: {}",
              key, driver.getExptimeSeconds());
        }
      }
    } else {
      stat.recordHits(1);
      if (isDebugEnabled) {
        logger.debug("Cache hit for single key [{}]", key);
      }
      if (isNullObject(value)) {
        value = null;
      }
    }
    return value;
  }

  private boolean isNotEmptyList(Object value) {
    Iterables iterables = new Iterables(value);
    return !(iterables.isIterable() && iterables.isEmpty());
  }

  private NullObject createNullObject() {
    return new NullObject();
  }

  private boolean isNullObject(Object obj) {
    return obj instanceof NullObject;
  }

  private class AddableObject<T> {

    List<T> hitValueList;
    Set<T> hitValueSet;
    Class<T> valueClass;

    private AddableObject(Class<T> valueClass) {
      if (returnDescriptor.isSetAssignable()) {

        hitValueSet = new HashSet<T>();

      } else if (returnDescriptor.isArrayList()) {

        hitValueList = new ArrayList<T>();

      } else { // Collection,List,LinkedList?????????????????????????????????LinkedList

        hitValueList = new LinkedList<T>();

      }
      this.valueClass = valueClass;
    }

    public void add(T v) {
      if (hitValueList != null) {
        hitValueList.add(v);
      } else {
        hitValueSet.add(v);
      }
    }

    public Object getReturn() {
      if (returnDescriptor.isListAssignable()
          || returnDescriptor.isCollection()) {

        return hitValueList;

      } else if (returnDescriptor.isSetAssignable()) {

        return hitValueSet;

      } else if (returnDescriptor.isArray()) {

        return CollectionUtil.toArray(hitValueList, valueClass);

      } else {
        return !hitValueList.isEmpty() ? hitValueList.get(0) : null;
      }
    }

    @Override
    public String toString() {
      return hitValueList != null ? hitValueList.toString() : hitValueSet.toString();
    }
  }

}
