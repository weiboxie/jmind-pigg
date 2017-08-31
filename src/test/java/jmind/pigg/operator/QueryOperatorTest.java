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

import org.junit.Test;

import jmind.pigg.binding.BoundSql;
import jmind.pigg.datasource.DataSourceFactoryGroup;
import jmind.pigg.datasource.SimpleDataSourceFactory;
import jmind.pigg.descriptor.MethodDescriptor;
import jmind.pigg.descriptor.ParameterDescriptor;
import jmind.pigg.descriptor.ReturnDescriptor;
import jmind.pigg.interceptor.InterceptorChain;
import jmind.pigg.jdbc.ListSupplier;
import jmind.pigg.jdbc.SetSupplier;
import jmind.pigg.mapper.RowMapper;
import jmind.pigg.operator.AbstractOperator;
import jmind.pigg.operator.Config;
import jmind.pigg.operator.OperatorFactory;
import jmind.pigg.stat.InvocationStat;
import jmind.pigg.stat.MetaStat;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.JdbcOperationsAdapter;
import jmind.pigg.support.MockDB;
import jmind.pigg.support.MockSQL;
import jmind.pigg.support.model4table.User;
import jmind.base.util.reflect.TypeToken;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * @author xieweibo
 */
public class QueryOperatorTest {

  @Test
  public void testQueryObject() throws Exception {
    TypeToken<User> t = TypeToken.of(User.class);
    String srcSql = "select * from user where id=:1.id and name=:1.name";
    AbstractOperator operator = getOperator(t, t, srcSql, new ArrayList<Annotation>());



    User user = new User();
    user.setId(100);
    user.setName("ash");
    operator.execute(new Object[]{user}, InvocationStat.create());
  }

  @Test
  public void testQueryList() throws Exception {
    TypeToken<User> pt = TypeToken.of(User.class);
    TypeToken<List<User>> rt = new TypeToken<List<User>>() {
    };
    String srcSql = "select * from user where id=:1.id and name=:1.name";
    AbstractOperator operator = getOperator(pt, rt, srcSql, new ArrayList<Annotation>());



    User user = new User();
    user.setId(100);
    user.setName("ash");
    operator.execute(new Object[]{user}, InvocationStat.create());
  }

  @Test
  public void testQuerySet() throws Exception {
    TypeToken<User> pt = TypeToken.of(User.class);
    TypeToken<Set<User>> rt = new TypeToken<Set<User>>() {
    };
    String srcSql = "select * from user where id=:1.id and name=:1.name";
    AbstractOperator operator = getOperator(pt, rt, srcSql, new ArrayList<Annotation>());



    User user = new User();
    user.setId(100);
    user.setName("ash");
    operator.execute(new Object[]{user}, InvocationStat.create());
  }

  @Test
  public void testQueryArray() throws Exception {
    TypeToken<User> pt = TypeToken.of(User.class);
    TypeToken<User[]> rt = TypeToken.of(User[].class);
    String srcSql = "select * from user where id=:1.id and name=:1.name";
    AbstractOperator operator = getOperator(pt, rt, srcSql, new ArrayList<Annotation>());


    User user = new User();
    user.setId(100);
    user.setName("ash");
    operator.execute(new Object[]{user}, InvocationStat.create());
  }

  @Test
  public void testQueryIn() throws Exception {
    TypeToken<List<Integer>> pt = new TypeToken<List<Integer>>() {
    };
    TypeToken<List<User>> rt = new TypeToken<List<User>>() {
    };
    String srcSql = "select * from user where id in (:1)";
    AbstractOperator operator = getOperator(pt, rt, srcSql, new ArrayList<Annotation>());



    List<Integer> ids = Arrays.asList(100, 200, 300);
    operator.execute(new Object[]{ids}, InvocationStat.create());
  }

  @Test
  public void testQueryInCount() throws Exception {
    TypeToken<List<Integer>> pt = new TypeToken<List<Integer>>() {
    };
    TypeToken<Integer> rt = new TypeToken<Integer>() {
    };
    String srcSql = "select count(1) from user where id in (:1)";
    AbstractOperator operator = getOperator(pt, rt, srcSql, new ArrayList<Annotation>());



    List<Integer> ids = Arrays.asList(100, 200, 300);
    Integer r = (Integer) operator.execute(new Object[]{ids}, InvocationStat.create());
    assertThat(r, is(3));
  }

  @Test
  public void testStatsCounter() throws Exception {
    TypeToken<User> t = TypeToken.of(User.class);
    String srcSql = "select * from user where id=:1.id and name=:1.name";
    AbstractOperator operator = getOperator(t, t, srcSql, new ArrayList<Annotation>());

    User user = new User();
    user.setId(100);
    user.setName("ash");


    InvocationStat stat = InvocationStat.create();
    operator.execute(new Object[]{user}, stat);
    assertThat(stat.getDatabaseExecuteSuccessCount(), equalTo(1L));
    operator.execute(new Object[]{user}, stat);
    assertThat(stat.getDatabaseExecuteSuccessCount(), equalTo(2L));

    try {
      operator.execute(new Object[]{user}, stat);
    } catch (UnsupportedOperationException e) {
    }
    assertThat(stat.getDatabaseExecuteExceptionCount(), equalTo(1L));
    try {
      operator.execute(new Object[]{user}, stat);
    } catch (UnsupportedOperationException e) {
    }
    assertThat(stat.getDatabaseExecuteExceptionCount(), equalTo(2L));
  }

  private AbstractOperator getOperator(TypeToken<?> pt, TypeToken<?> rt, String srcSql, List<Annotation> annos)
      throws Exception {
    List<Annotation> empty = Collections.emptyList();
    ParameterDescriptor p = ParameterDescriptor.create(0, pt.getType(), empty, "1");
    List<ParameterDescriptor> pds = Arrays.asList(p);

    List<Annotation> methodAnnos = new ArrayList<Annotation>();
    methodAnnos.add(new MockDB());
    methodAnnos.add(new MockSQL(srcSql));
    for (Annotation anno : annos) {
      methodAnnos.add(anno);
    }
    ReturnDescriptor rd = ReturnDescriptor.create(rt.getType(), methodAnnos);
    MethodDescriptor md = MethodDescriptor.create(null, null, rd, pds);
    DataSourceFactoryGroup group = new DataSourceFactoryGroup();
    group.addDataSourceFactory(new SimpleDataSourceFactory(DataSourceConfig.getDataSource()));

    OperatorFactory factory = new OperatorFactory(group, null, new InterceptorChain(), new Config());

    AbstractOperator operator = factory.getOperator(md, MetaStat.create());
    return operator;
  }

}
