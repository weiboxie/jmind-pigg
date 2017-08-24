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
import jmind.pigg.util.reflect.TypeToken;

/**
 * @author xieweibo
 */
public class CacheableUpdateOperatorTest {

  @Test
  public void testUpdate() throws Exception {
    TypeToken<User> pt = TypeToken.of(User.class);
    TypeToken<Integer> rt = TypeToken.of(int.class);
    String srcSql = "update user set name=:1.name where id=:1.id";

    AbstractOperator operator = getOperator(pt, rt, srcSql, new CacheHandlerAdapter() {
      @Override
      public void delete(String key, Class<?> daoClass) {
        assertThat(key, equalTo("user_100"));
      }
    }, new MockCacheBy("id"));

    operator.setJdbcOperations(new JdbcOperationsAdapter() {
      @Override
      public int update(DataSource ds, BoundSql boundSql) {
        String sql = boundSql.getSql();
        Object[] args = boundSql.getArgs().toArray();
        String descSql = "update user set name=? where id=?";
        assertThat(sql, equalTo(descSql));
        assertThat(args.length, equalTo(2));
        assertThat(args[0], equalTo((Object) "ash"));
        assertThat(args[1], equalTo((Object) 100));
        return 1;
      }
    });

    User user = new User();
    user.setId(100);
    user.setName("ash");
    InvocationStat stat = InvocationStat.create();
    operator.execute(new Object[]{user}, stat);
    assertThat(stat.getCacheDeleteSuccessCount(), equalTo(1L));
  }

  @Test
  public void testUpdateWithIn() throws Exception {
    TypeToken<List<Integer>> pt = new TypeToken<List<Integer>>() {
    };
    TypeToken<Integer> rt = TypeToken.of(int.class);
    String srcSql = "update user set name=ash where id in (:1)";

    AbstractOperator operator = getOperator(pt, rt, srcSql, new CacheHandlerAdapter() {
      @Override
      public void batchDelete(Set<String> keys, Class<?> daoClass) {
        Set<String> set = new HashSet<String>();
        set.add("user_100");
        set.add("user_200");
        assertThat(keys, equalTo(set));
      }
    }, new MockCacheBy(""));

    operator.setJdbcOperations(new JdbcOperationsAdapter() {
      @Override
      public int update(DataSource ds, BoundSql boundSql) {
        String sql = boundSql.getSql();
        Object[] args = boundSql.getArgs().toArray();
        String descSql = "update user set name=ash where id in (?,?)";
        assertThat(sql, equalTo(descSql));
        assertThat(args.length, equalTo(2));
        assertThat(args[0], equalTo((Object) 100));
        assertThat(args[1], equalTo((Object) 200));
        return 1;
      }
    });

    List<Integer> ids = Arrays.asList(100, 200);
    InvocationStat stat = InvocationStat.create();
    operator.execute(new Object[]{ids}, stat);
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
    methodAnnos.add(new MockCache("user", 1000000));
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
