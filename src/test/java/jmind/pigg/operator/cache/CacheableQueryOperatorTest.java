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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.hamcrest.Matchers;
import org.junit.Test;

import jmind.pigg.binding.BoundSql;
import jmind.pigg.datasource.DataSourceFactoryGroup;
import jmind.pigg.datasource.SimpleDataSourceFactory;
import jmind.pigg.descriptor.MethodDescriptor;
import jmind.pigg.descriptor.ParameterDescriptor;
import jmind.pigg.descriptor.ReturnDescriptor;
import jmind.pigg.interceptor.InterceptorChain;
import jmind.pigg.jdbc.ListSupplier;
import jmind.pigg.mapper.RowMapper;
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
@SuppressWarnings("unchecked")
public class CacheableQueryOperatorTest {

  @Test
  public void testQuerySingleKeyHit() throws Exception {
    TypeToken<Integer> pt = TypeToken.of(Integer.class);
    TypeToken<User> rt = TypeToken.of(User.class);
    String srcSql = "select * from user where id=:1";

    AbstractOperator operator = getOperator(pt, rt, srcSql, new CacheHandlerAdapter() {
      @Override
      public Object get(String key, Type type, Class<?> daoClass) {
        assertThat(key, Matchers.equalTo("user_1"));
        return new User();
      }
    }, new MockCacheBy(""));



    InvocationStat stat = InvocationStat.create();
    operator.execute(new Object[]{1}, stat);
    assertThat(stat.getHitCount(), Matchers.equalTo(1L));
  }

  @Test
  public void testQuerySingleKeyMiss() throws Exception {
    TypeToken<Integer> pt = TypeToken.of(Integer.class);
    TypeToken<User> rt = TypeToken.of(User.class);
    String srcSql = "select * from user where id=:1";

    AbstractOperator operator = getOperator(pt, rt, srcSql, new CacheHandlerAdapter() {
      @Override
      public Object get(String key, Type type, Class<?> daoClass) {
        assertThat(key, Matchers.equalTo("user_1"));
        return null;
      }

      @Override
      public void set(String key, Object value, int expires, Class<?> daoClass) {
        assertThat(key, Matchers.equalTo("user_1"));
        assertThat(expires, Matchers.equalTo((int) TimeUnit.DAYS.toSeconds(1)));
      }
    }, new MockCacheBy(""));



    InvocationStat stat = InvocationStat.create();
    operator.execute(new Object[]{1}, stat);
    assertThat(stat.getMissCount(), Matchers.equalTo(1L));
  }

  @Test
  public void testQueryMultiKeyAllHit() throws Exception {
    TypeToken<List<Integer>> pt = new TypeToken<List<Integer>>() {
    };
    TypeToken<List<User>> rt = new TypeToken<List<User>>() {
    };
    String srcSql = "select * from user where id in (:1)";

    AbstractOperator operator = getOperator(pt, rt, srcSql, new CacheHandlerAdapter() {
      @Override
      public Map<String, Object> getBulk(Set<String> keys, Type type, Class<?> daoClass) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user_1", new User());
        map.put("user_2", new User());
        map.put("user_3", new User());
        assertThat(keys, Matchers.equalTo(map.keySet()));
        return map;
      }
    }, new MockCacheBy(""));


    InvocationStat stat = InvocationStat.create();
    operator.execute(new Object[]{Arrays.asList(1, 2, 3)}, stat);
    assertThat(stat.getHitCount(), equalTo(3L));
    assertThat(((CacheableQueryOperator) operator).propertyOfMapperInvoker.getName(), equalTo("id"));
  }

  @Test
  public void testQueryMultiKeyAllMiss() throws Exception {
    TypeToken<List<Integer>> pt = new TypeToken<List<Integer>>() {
    };
    TypeToken<List<User>> rt = new TypeToken<List<User>>() {
    };
    String srcSql = "select * from user where id in (:1)";
    final Set<String> keys = new HashSet<String>();
    final Set<String> setKeys = new HashSet<String>();
    keys.add("user_1");
    keys.add("user_2");
    keys.add("user_3");

    AbstractOperator operator = getOperator(pt, rt, srcSql, new CacheHandlerAdapter() {
      @Override
      public Map<String, Object> getBulk(Set<String> keys, Type type, Class<?> daoClass) {
        assertThat(keys, Matchers.equalTo(keys));
        return null;
      }

      @Override
      public void set(String key, Object value, int expires, Class<?> daoClass) {
        setKeys.add(key);
      }
    }, new MockCacheBy(""));



    InvocationStat stat = InvocationStat.create();
    operator.execute(new Object[]{Arrays.asList(1, 2, 3)}, stat);
    assertThat(stat.getMissCount(), Matchers.equalTo(3L));
    assertThat(keys, Matchers.equalTo(setKeys));
  }

  @Test
  public void testQueryMultiKey() throws Exception {
    TypeToken<List<Integer>> pt = new TypeToken<List<Integer>>() {
    };
    TypeToken<List<User>> rt = new TypeToken<List<User>>() {
    };
    String srcSql = "select * from user where id in (:1)";
    final Set<String> keys = new HashSet<String>();
    final Set<String> setKeys = new HashSet<String>();
    keys.add("user_1");
    keys.add("user_2");
    keys.add("user_3");

    AbstractOperator operator = getOperator(pt, rt, srcSql, new CacheHandlerAdapter() {
      @Override
      public Map<String, Object> getBulk(Set<String> keys, Type type, Class<?> daoClass) {
        assertThat(keys, Matchers.equalTo(keys));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user_2", new User());
        return map;
      }

      @Override
      public void set(String key, Object value, int expires, Class<?> daoClass) {
        setKeys.add(key);
      }
    }, new MockCacheBy(""));



    InvocationStat stat = InvocationStat.create();
    operator.execute(new Object[]{Arrays.asList(1, 2, 3)}, stat);
    assertThat(stat.getHitCount(), Matchers.equalTo(1L));
    assertThat(stat.getMissCount(), Matchers.equalTo(2L));
    keys.remove("user_2");
    assertThat(keys, Matchers.equalTo(setKeys));
  }

  @Test
  public void testQueryMultiKeyPropertyOfMapper() throws Exception {
    TypeToken<List<Integer>> pt = new TypeToken<List<Integer>>() {
    };
    TypeToken<List<X>> rt = new TypeToken<List<X>>() {
    };
    String srcSql = "select * from user where msg_id in (:1)";
    AbstractOperator operator = getOperator(pt, rt, srcSql, new CacheHandlerAdapter() {
    }, new MockCacheBy(""));
    assertThat(((CacheableQueryOperator) operator).propertyOfMapperInvoker.getName(), equalTo("msgId"));
  }

  private static class X {

    private int msgId;
    private String content;

    public int getMsgId() {
      return msgId;
    }

    public void setMsgId(int msgId) {
      this.msgId = msgId;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }
  }


  private AbstractOperator getOperator(TypeToken<?> pt, TypeToken<?> rt, String srcSql,
                                       CacheHandler ch, MockCacheBy cacheBy) throws Exception {
    List<Annotation> pAnnos = new ArrayList<Annotation>();
    pAnnos.add(cacheBy);
    ParameterDescriptor p = ParameterDescriptor.create(0, pt.getType(), pAnnos, "1");
    List<ParameterDescriptor> pds = Arrays.asList(p);

    List<Annotation> methodAnnos = new ArrayList<Annotation>();
    methodAnnos.add(new MockDB());
    methodAnnos.add(new MockCache("user",1000));
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
