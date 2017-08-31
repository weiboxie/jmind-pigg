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

import javax.sql.DataSource;

import jmind.base.util.Iterables;
import jmind.pigg.binding.InvocationContext;
import jmind.pigg.descriptor.MethodDescriptor;
import jmind.pigg.operator.BatchUpdateOperator;
import jmind.pigg.operator.Config;
import jmind.pigg.parser.ASTRootNode;
import jmind.pigg.stat.InvocationStat;

import jmind.pigg.util.logging.InternalLogger;
import jmind.pigg.util.logging.InternalLoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author xieweibo
 */
public class CacheableBatchUpdateOperator extends BatchUpdateOperator {

  private final static InternalLogger logger = InternalLoggerFactory.getInstance(CacheableBatchUpdateOperator.class);

  private CacheDriver driver;

  public CacheableBatchUpdateOperator(ASTRootNode rootNode, MethodDescriptor md, CacheDriver cacheDriver, Config config) {
    super(rootNode, md, config);
    this.driver = cacheDriver;
  }

  @Override
  public Object execute(Object[] values, InvocationStat stat) {
    Iterables iterables = getIterables(values);
    if (iterables.isEmpty()) {
      return transformer.transform(new int[]{});
    }

    Set<String> keys = new HashSet<String>(iterables.size() * 2);

    Map<DataSource, Group> groupMap = new HashMap<DataSource, Group>();
    int t = 0;
    for (Object obj : iterables) {
      InvocationContext context = invocationContextFactory.newInvocationContext(new Object[]{obj});
      keys.add(driver.getCacheKey(context));
      group(context, groupMap, t++);
    }
    int[] ints = executeDb(groupMap, t, stat);
    if (logger.isDebugEnabled()) {
      logger.debug("Cache delete for multiple keys {}", keys);
    }
    driver.batchDeleteFromCache(keys, stat);
    return transformer.transform(ints);
  }

}
