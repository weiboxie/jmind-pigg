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

package jmind.pigg.exception;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import jmind.pigg.annotation.Cache;
import jmind.pigg.annotation.CacheBy;
import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.SQL;
import jmind.pigg.operator.Pigg;
import jmind.pigg.operator.cache.LocalCacheHandler;
import jmind.pigg.parser.SqlParserException;
import jmind.pigg.support.DataSourceConfig;

/**
 * 测试{@link jmind.pigg.operator.IncorrectSqlException}
 *
 * @author xieweibo
 */
public class IncorrectSqlExceptionTest {

  private final static Pigg pigg = Pigg.newInstance(DataSourceConfig.getDataSource());

  static {
    pigg.setLazyInit(true);
    pigg.setCacheHandler(new LocalCacheHandler());
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void test() {
    thrown.expect(DescriptionException.class);
    thrown.expectMessage("if use cache, sql's in clause expected less than or equal 1 but 2");
    Dao dao = pigg.create(Dao.class);
    dao.add(new ArrayList<Integer>(), new ArrayList<Integer>());
  }


  @Test
  public void test2() {
    thrown.expect(DescriptionException.class);
    thrown.expectMessage("sql is null or empty");
    Dao dao = pigg.create(Dao.class);
    dao.add2();
  }

  @Test
  public void test4() {
    thrown.expect(SqlParserException.class);
    Dao dao = pigg.create(Dao.class);
    dao.add3();
  }

  @Test
  public void test5() {
    thrown.expect(DescriptionException.class);
    thrown.expectMessage("if use cache, sql's in clause expected less than or equal 1 but 2");
    Dao dao = pigg.create(Dao.class);
    dao.gets(new ArrayList<Integer>(), new ArrayList<Integer>());
  }

  @DB
  @Cache(prefix = "dao_", expire = 100)
  static interface Dao {
    @SQL("update ... where a in (:1) and b in (:2)")
    public int add(@CacheBy List<Integer> a, List<Integer> b);

    @SQL("")
    public int add2();

    @SQL("test")
    public int add3();

    @SQL("select ... where a in (:1) and b in (:2)")
    public List<Integer> gets(@CacheBy List<Integer> a, List<Integer> b);
  }

  static class Model {
    int id;
    List<Integer> list;

    public int getId() {
      return id;
    }

    public List<Integer> getList() {
      return list;
    }
  }
}
