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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import jmind.pigg.binding.BoundSql;
import jmind.pigg.datasource.DataSourceFactoryGroup;
import jmind.pigg.datasource.SimpleDataSourceFactory;
import jmind.pigg.descriptor.MethodDescriptor;
import jmind.pigg.descriptor.ParameterDescriptor;
import jmind.pigg.descriptor.ReturnDescriptor;
import jmind.pigg.exception.DescriptionException;
import jmind.pigg.interceptor.InterceptorChain;
import jmind.pigg.jdbc.exception.DataAccessException;
import jmind.pigg.operator.AbstractOperator;
import jmind.pigg.operator.Config;
import jmind.pigg.operator.OperatorFactory;
import jmind.pigg.sharding.DatabaseShardingStrategy;
import jmind.pigg.sharding.ModHundredTableShardingStrategy;
import jmind.pigg.stat.InvocationStat;
import jmind.pigg.stat.MetaStat;
import jmind.pigg.support.*;
import jmind.pigg.support.model4table.User;
import jmind.base.util.reflect.TypeToken;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author xieweibo
 */
public class BatchUpdateOperatorTest {

  @Test
  public void testExecuteReturnVoid() throws Exception {
    TypeToken<List<User>> pt = new TypeToken<List<User>>() {
    };
    TypeToken<Void> rt = TypeToken.of(void.class);
    String srcSql = "update user set name=:1.name where id=:1.id";
    AbstractOperator operator = getOperator(pt, rt, srcSql);

    final int[] expectedInts = new int[]{1, 2};

    List<User> users = Arrays.asList(new User(100, "ash"), new User(200, "lucy"));
    Object actual = operator.execute(new Object[]{users}, InvocationStat.create());
    assertThat(actual, nullValue());
  }

  @Test
  public void testExecuteReturnInt() throws Exception {
    TypeToken<List<User>> pt = new TypeToken<List<User>>() {
    };
    TypeToken<Integer> rt = TypeToken.of(int.class);
    String srcSql = "update user set name=:1.name where id=:1.id";
    AbstractOperator operator = getOperator(pt, rt, srcSql);

    final int[] expectedInts = new int[]{1, 2};

  }
  @Test
  public void testExecuteReturnIntArray() throws Exception {
    TypeToken<List<User>> pt = new TypeToken<List<User>>() {
    };
    TypeToken<int[]> rt = TypeToken.of(int[].class);
    String srcSql = "update user set name=:1.name where id=:1.id";
    AbstractOperator operator = getOperator(pt, rt, srcSql);

    final int[] expectedInts = new int[]{1, 2};


    List<User> users = Arrays.asList(new User(100, "ash"), new User(200, "lucy"));
    int[] actualInts = (int[]) operator.execute(new Object[]{users}, InvocationStat.create());
    assertThat(Arrays.toString(actualInts), equalTo(Arrays.toString(expectedInts)));
  }

  @Test
  public void testExecuteReturnIntegerArray() throws Exception {
    TypeToken<List<User>> pt = new TypeToken<List<User>>() {
    };
    TypeToken<Integer[]> rt = TypeToken.of(Integer[].class);
    String srcSql = "update user set name=:1.name where id=:1.id";
    AbstractOperator operator = getOperator(pt, rt, srcSql);

    final int[] expectedInts = new int[]{1, 2};


    List<User> users = Arrays.asList(new User(100, "ash"), new User(200, "lucy"));
    Integer[] actualInts = (Integer[]) operator.execute(new Object[]{users}, InvocationStat.create());
    assertThat(Arrays.toString(actualInts), equalTo(Arrays.toString(expectedInts)));
  }


  @Test
  public void testExecuteMulti() throws Exception {
    TypeToken<List<User>> pt = new TypeToken<List<User>>() {
    };
    TypeToken<int[]> rt = TypeToken.of(int[].class);
    String srcSql = "update #table set name=:1.name where id=:1.id";
    AbstractOperator operator = getOperator2(pt, rt, srcSql);



    List<User> users = Arrays.asList(
        new User(30, "ash"), new User(60, "lucy"), new User(10, "lily"),
        new User(20, "gill"), new User(55, "liu"));
    int[] actualInts = (int[]) operator.execute(new Object[]{users}, InvocationStat.create());
    assertThat(Arrays.toString(actualInts), equalTo(Arrays.toString(new int[]{3, 6, 1, 2, 5})));
  }

  @Test
  public void testStatsCounter() throws Exception {
    TypeToken<List<User>> pt = new TypeToken<List<User>>() {
    };
    TypeToken<int[]> rt = TypeToken.of(int[].class);
    String srcSql = "update user set name=:1.name where id=:1.id";
    AbstractOperator operator = getOperator(pt, rt, srcSql);


    List<User> users = Arrays.asList(new User(100, "ash"), new User(200, "lucy"));
    InvocationStat stat = InvocationStat.create();
    operator.execute(new Object[]{users}, stat);
    assertThat(stat.getDatabaseExecuteSuccessCount(), equalTo(1L));
    operator.execute(new Object[]{users}, stat);
    assertThat(stat.getDatabaseExecuteSuccessCount(), equalTo(2L));


    try {
      operator.execute(new Object[]{users}, stat);
    } catch (UnsupportedOperationException e) {
    }
    assertThat(stat.getDatabaseExecuteExceptionCount(), equalTo(1L));
    try {
      operator.execute(new Object[]{users}, stat);
    } catch (UnsupportedOperationException e) {
    }
    assertThat(stat.getDatabaseExecuteExceptionCount(), equalTo(2L));
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testExecuteReturnTypeError() throws Exception {
    thrown.expect(DescriptionException.class);
    thrown.expectMessage("the return type of batch update expected one of " +
        "[void, int, int[], Void, Integer, Integer[]] but class java.lang.String");

    TypeToken<List<User>> pt = new TypeToken<List<User>>() {
    };
    TypeToken<String> rt = TypeToken.of(String.class);
    String srcSql = "update user set name=:1.name where id=:1.id";
    AbstractOperator operator = getOperator(pt, rt, srcSql);

    final int[] expectedInts = new int[]{1, 2};


    List<User> users = Arrays.asList(new User(100, "ash"), new User(200, "lucy"));
    operator.execute(new Object[]{users}, InvocationStat.create());
  }

  private AbstractOperator getOperator(TypeToken<?> pt, TypeToken<?> rt, String srcSql) throws Exception {
    List<Annotation> empty = Collections.emptyList();
    ParameterDescriptor p = ParameterDescriptor.create(0, pt.getType(), empty, "1");
    List<ParameterDescriptor> pds = Arrays.asList(p);

    List<Annotation> methodAnnos = new ArrayList<Annotation>();
    methodAnnos.add(new MockDB());
    methodAnnos.add(new MockSQL(srcSql));
    ReturnDescriptor rd = ReturnDescriptor.create(rt.getType(), methodAnnos);
    MethodDescriptor md = MethodDescriptor.create(null, null, rd, pds);

    DataSourceFactoryGroup group = new DataSourceFactoryGroup();
    group.addDataSourceFactory(new SimpleDataSourceFactory(DataSourceConfig.getDataSource()));
    OperatorFactory factory = new OperatorFactory(group, null, new InterceptorChain(), new Config());

    AbstractOperator operator = factory.getOperator(md, MetaStat.create());
    return operator;
  }

  private AbstractOperator getOperator2(TypeToken<?> pt, TypeToken<?> rt, String srcSql) throws Exception {
    List<Annotation> pAnnos = new ArrayList<Annotation>();
    pAnnos.add(new MockShardingBy("id"));
    ParameterDescriptor p = ParameterDescriptor.create(0, pt.getType(), pAnnos, "1");
    List<ParameterDescriptor> pds = Arrays.asList(p);

    List<Annotation> methodAnnos = new ArrayList<Annotation>();
    methodAnnos.add(new MockDB("", "user"));
    methodAnnos.add(new MockSharding(ModHundredTableShardingStrategy.class, MyDatabaseShardingStrategy.class, null));
    methodAnnos.add(new MockSQL(srcSql));
    ReturnDescriptor rd = ReturnDescriptor.create(rt.getType(), methodAnnos);
    MethodDescriptor md = MethodDescriptor.create(null, null, rd, pds);

    DataSourceFactoryGroup group = new DataSourceFactoryGroup();
    group.addDataSourceFactory(new SimpleDataSourceFactory("l50", DataSourceConfig.getDataSource(0)));
    group.addDataSourceFactory(new SimpleDataSourceFactory("g50", DataSourceConfig.getDataSource(1)));
    OperatorFactory factory = new OperatorFactory(group, null, new InterceptorChain(), new Config());
    AbstractOperator operator = factory.getOperator(md, MetaStat.create());
    return operator;
  }

  public static class MyDatabaseShardingStrategy implements DatabaseShardingStrategy {

    @Override
    public String getDataSourceFactoryName(Object shardParam) {
      Integer i = (Integer) shardParam;
      if (i < 50) {
        return "l50";
      } else {
        return "g50";
      }
    }
  }

}
