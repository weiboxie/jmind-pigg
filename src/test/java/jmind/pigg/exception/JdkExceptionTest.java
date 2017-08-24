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

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import jmind.pigg.annotation.Cache;
import jmind.pigg.annotation.CacheBy;
import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.SQL;
import jmind.pigg.binding.BindingException;
import jmind.pigg.mapper.MappingException;
import jmind.pigg.operator.Pigg;
import jmind.pigg.operator.cache.LocalCacheHandler;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Table;
import jmind.pigg.support.model4table.Msg;

/**
 * 测试java.lang包下的常规异常
 *
 * @author xieweibo
 */
public class JdkExceptionTest {

  private final static DataSource ds = DataSourceConfig.getDataSource();
  private final static Pigg pigg = Pigg.newInstance(ds);

  static {
    pigg.setLazyInit(true);
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void before() throws Exception {
    Connection conn = ds.getConnection();
    Table.MSG.load(conn);
    Table.ACCOUNT.load(conn);
    conn.close();
  }

  @Test
  public void testBatchUpdateParameterNull() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("batchUpdate's parameter can't be null");
    MsgDao dao = pigg.create(MsgDao.class);
    dao.batchInsert(null);
  }

  @Test
  public void testIterableParameterNull() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("value of :1 can't be null");
    MsgDao dao = pigg.create(MsgDao.class);
    dao.getMsgs(null);
  }

  @Test
  public void testIterableParameterNullWithCache() {
    thrown.expect(BindingException.class);
    thrown.expectMessage("Parameter ':1' need a non-null value");
    Pigg pigg = Pigg.newInstance(ds);
    pigg.setLazyInit(true);
    pigg.setCacheHandler(new LocalCacheHandler());
    MsgCacheDao dao = pigg.create(MsgCacheDao.class);
    dao.getMsgs(null);
  }

  @Test
  public void testNoData() throws Exception {
    thrown.expect(MappingException.class);
    thrown.expectMessage("no data, can't cast null to primitive type int");
    AccountDao dao = pigg.create(AccountDao.class);
    dao.getBalance(1);
  }

  @Test
  public void testDataIsNull() throws Exception {
    thrown.expect(MappingException.class);
    thrown.expectMessage("data is null, can't cast null to primitive type int");
    AccountDao dao = pigg.create(AccountDao.class);
    int id = 1;
    dao.insert(id, null);
    dao.getBalance(id);
  }

  @DB(table = "account")
  interface AccountDao {

    @SQL("insert into #table(id, balance) values(:1, :2)")
    public int insert(int id, Integer balance);

    @SQL("select balance from #table where id=:1")
    public int getBalance(int id);

  }

  @DB
  interface MsgDao {

    @SQL("insert into msg(uid, content) values(:1.uid, :1.content)")
    public int[] batchInsert(List<Msg> msgs);

    @SQL("select id, uid, content from msg where id in (:1)")
    public List<Msg> getMsgs(List<Integer> ids);

  }

  @DB
  @Cache(prefix = "msg_", expire = 100)
  interface MsgCacheDao {

    @SQL("select id, uid, content from msg where id in (:1)")
    public List<Msg> getMsgs(@CacheBy List<Integer> ids);

  }

}
