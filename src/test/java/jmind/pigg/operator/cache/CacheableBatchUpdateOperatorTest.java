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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.junit.Test;

import jmind.pigg.binding.BoundSql;
import jmind.pigg.datasource.DataSourceFactoryGroup;
import jmind.pigg.datasource.SimpleDataSourceFactory;
import jmind.pigg.descriptor.MethodDescriptor;
import jmind.pigg.descriptor.ParameterDescriptor;
import jmind.pigg.descriptor.ReturnDescriptor;
import jmind.pigg.interceptor.InterceptorChain;
import jmind.pigg.jdbc.exception.DataAccessException;
import jmind.pigg.operator.AbstractOperator;
import jmind.pigg.operator.Config;
import jmind.pigg.operator.OperatorFactory;
import jmind.pigg.stat.InvocationStat;
import jmind.pigg.stat.MetaStat;
import jmind.pigg.support.CacheHandlerAdapter;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.JdbcOperationsAdapter;
import jmind.pigg.support.MockCache;
import jmind.pigg.support.MockCacheBy;
import jmind.pigg.support.MockDB;
import jmind.pigg.support.MockSQL;
import jmind.pigg.support.model4table.User;
import jmind.base.util.reflect.TypeToken;

/**
 * @author xieweibo
 */
public class CacheableBatchUpdateOperatorTest {

  @Test
  public void testBatchUpdate() throws Exception {
    TypeToken<List<User>> pt = new TypeToken<List<User>>() {
    };
    TypeToken<int[]> rt = TypeToken.of(int[].class);
    String srcSql = "update user set name=:1.name where id=:1.id";
    AbstractOperator operator = getOperator(pt, rt, srcSql, new CacheHandlerAdapter() {
      @Override
      public void batchDelete(Set<String> keys, Class<?> daoClass) {
        Set<String> set = new HashSet<String>();
        set.add("user_100");
        set.add("user_200");
        assertThat(keys, equalTo(set));
      }
    }, new MockCacheBy("id"));
    final int[] expectedInts = new int[]{1, 2};

    List<User> users = Arrays.asList(new User(100, "ash"), new User(200, "lucy"));
    InvocationStat stat = InvocationStat.create();
    int[] actualInts = (int[]) operator.execute(new Object[]{users}, stat);
    assertThat(Arrays.toString(actualInts), equalTo(Arrays.toString(expectedInts)));
    assertThat(stat.getCacheBatchDeleteSuccessCount(), equalTo(1L));
  }

  private AbstractOperator getOperator(TypeToken<?> pt, TypeToken<?> rt, String srcSql,
                                       CacheHandler ch, MockCacheBy cacheBy) throws Exception {
    List<Annotation> pAnnos = new ArrayList<Annotation>();
    pAnnos.add(cacheBy);
    ParameterDescriptor p = ParameterDescriptor.create(0, pt.getType(), pAnnos, "1");
    List<ParameterDescriptor> pds = Arrays.asList(p);

    List<Annotation> methodAnnos = new ArrayList<Annotation>();
    methodAnnos.add(new MockDB());
    methodAnnos.add(new MockCache("user", 50000));
    methodAnnos.add(new MockSQL(srcSql));
    ReturnDescriptor rd = ReturnDescriptor.create(rt.getType(), methodAnnos);
    MethodDescriptor md = MethodDescriptor.create(null, null, rd, pds);
    DataSourceFactoryGroup group = new DataSourceFactoryGroup();
    group.addDataSourceFactory(new SimpleDataSourceFactory(DataSourceConfig.getDataSource()));

    OperatorFactory factory = new OperatorFactory(group, ch, new InterceptorChain(), new Config());

    AbstractOperator operator = factory.getOperator(md, MetaStat.create());
    return operator;
  }

}
