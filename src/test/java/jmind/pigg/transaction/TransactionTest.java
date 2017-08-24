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

package jmind.pigg.transaction;

import org.junit.Before;
import org.junit.Test;

import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.SQL;
import jmind.pigg.datasource.AbstractDataSourceFactory;
import jmind.pigg.operator.Pigg;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Table;
import jmind.pigg.support.model4table.Account;
import jmind.pigg.transaction.ConnectionHolder;
import jmind.pigg.transaction.Transaction;
import jmind.pigg.transaction.TransactionFactory;
import jmind.pigg.transaction.TransactionIsolationLevel;
import jmind.pigg.transaction.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * 测试事务
 *
 * @author xieweibo
 */
public class TransactionTest {

  private final static DataSource ds = DataSourceConfig.getDataSource();
  private final static Pigg pigg = Pigg.newInstance(ds);
  private final static AccountDao dao = pigg.create(AccountDao.class);

  @Before
  public void before() throws Exception {
    Connection conn = ds.getConnection();
    Table.ACCOUNT.load(conn);
    conn.close();
  }

  @Test
  public void testCommit() throws Exception {
    int previousLevel = getPreviousLevel();

    Account x = new Account(1, 1000);
    Account y = new Account(2, 2000);
    dao.insert(x);
    dao.insert(y);

    int num = 50;
    x.add(num);
    y.sub(num);
    TransactionIsolationLevel level = TransactionIsolationLevel.SERIALIZABLE;
    Transaction tx = TransactionFactory.newTransaction(pigg, AbstractDataSourceFactory.DEFULT_NAME, level);
    ConnectionHolder connHolder = TransactionSynchronizationManager.getConnectionHolder(ds);
    assertThat(connHolder, notNullValue());
    assertThat(connHolder.getConnection(), notNullValue());

    dao.update(x);
    checkConn(connHolder.getConnection(), false, level.getLevel());
    dao.update(y);
    checkConn(connHolder.getConnection(), false, level.getLevel());
    tx.commit();

    connHolder = TransactionSynchronizationManager.getConnectionHolder(ds);
    assertThat(connHolder, nullValue());
    Connection conn = ds.getConnection();
    checkConn(conn, true, previousLevel);
    conn.close();

    assertThat(dao.getAccount(1), equalTo(x));
    assertThat(dao.getAccount(2), equalTo(y));
  }

  @Test
  public void testRollback() throws Exception {
    int previousLevel = getPreviousLevel();

    Account x = new Account(1, 1000);
    Account y = new Account(2, 2000);
    dao.insert(x);
    dao.insert(y);

    int num = 50;
    x.add(num);
    y.sub(num);
    TransactionIsolationLevel level = TransactionIsolationLevel.SERIALIZABLE;
    Transaction tx = TransactionFactory.newTransaction(pigg, AbstractDataSourceFactory.DEFULT_NAME, level);
    ConnectionHolder connHolder = TransactionSynchronizationManager.getConnectionHolder(ds);
    assertThat(connHolder, notNullValue());
    assertThat(connHolder.getConnection(), notNullValue());

    dao.update(x);
    checkConn(connHolder.getConnection(), false, level.getLevel());
    dao.update(y);
    checkConn(connHolder.getConnection(), false, level.getLevel());
    tx.rollback();

    connHolder = TransactionSynchronizationManager.getConnectionHolder(ds);
    assertThat(connHolder, nullValue());
    Connection conn = ds.getConnection();
    checkConn(conn, true, previousLevel);
    conn.close();

    x.sub(num);
    y.add(num);
    assertThat(dao.getAccount(1), equalTo(x));
    assertThat(dao.getAccount(2), equalTo(y));
  }

  @Test
  public void testRollback2() throws Exception {
    int previousLevel = getPreviousLevel();

    Account x = new Account(1, 1000);
    Account y = new Account(2, 2000);
    dao.insert(x);
    dao.insert(y);

    int num = 50;
    x.add(num);
    y.sub(num);
    TransactionIsolationLevel level = TransactionIsolationLevel.SERIALIZABLE;
    Transaction tx = TransactionFactory.newTransaction(pigg, AbstractDataSourceFactory.DEFULT_NAME, level);
    ConnectionHolder connHolder = TransactionSynchronizationManager.getConnectionHolder(ds);
    assertThat(connHolder, notNullValue());
    assertThat(connHolder.getConnection(), notNullValue());

    dao.update(x);
    checkConn(connHolder.getConnection(), false, level.getLevel());
    tx.rollback();

    connHolder = TransactionSynchronizationManager.getConnectionHolder(ds);
    assertThat(connHolder, nullValue());

    Connection conn = ds.getConnection();
    checkConn(conn, true, previousLevel);
    conn.close();

    x.sub(num);
    y.add(num);
    assertThat(dao.getAccount(1), equalTo(x));
    assertThat(dao.getAccount(2), equalTo(y));
  }

  @Test
  public void testCommitEmpty() throws Exception {
    int previousLevel = getPreviousLevel();

    TransactionIsolationLevel level = TransactionIsolationLevel.SERIALIZABLE;
    Transaction tx = TransactionFactory.newTransaction(pigg, AbstractDataSourceFactory.DEFULT_NAME, level);
    ConnectionHolder connHolder = TransactionSynchronizationManager.getConnectionHolder(ds);
    assertThat(connHolder, notNullValue());
    assertThat(connHolder.getConnection(), notNullValue());

    tx.commit();

    connHolder = TransactionSynchronizationManager.getConnectionHolder(ds);
    assertThat(connHolder, nullValue());
    Connection conn = ds.getConnection();
    checkConn(conn, true, previousLevel);
    conn.close();
  }


  @Test
  public void testRollbackEmpty() throws Exception {
    int previousLevel = getPreviousLevel();

    TransactionIsolationLevel level = TransactionIsolationLevel.SERIALIZABLE;
    Transaction tx = TransactionFactory.newTransaction(pigg, AbstractDataSourceFactory.DEFULT_NAME, level);
    ConnectionHolder connHolder = TransactionSynchronizationManager.getConnectionHolder(ds);
    assertThat(connHolder, notNullValue());
    assertThat(connHolder.getConnection(), notNullValue());

    tx.rollback();

    connHolder = TransactionSynchronizationManager.getConnectionHolder(ds);
    assertThat(connHolder, nullValue());
    Connection conn = ds.getConnection();
    checkConn(conn, true, previousLevel);
    conn.close();
  }

  @Test
  public void testDefaultLevel() throws Exception {
    int previousLevel = getPreviousLevel();
    Account x = new Account(1, 1000);
    Account y = new Account(2, 2000);
    dao.insert(x);
    dao.insert(y);

    int num = 50;
    x.add(num);
    y.sub(num);

    Transaction tx = TransactionFactory.newTransaction(pigg, AbstractDataSourceFactory.DEFULT_NAME);
    ConnectionHolder connHolder = TransactionSynchronizationManager.getConnectionHolder(ds);

    dao.update(x);
    checkConn(connHolder.getConnection(), false, previousLevel);
    dao.update(y);
    checkConn(connHolder.getConnection(), false, previousLevel);
    tx.commit();

    Connection conn = ds.getConnection();
    checkConn(conn, true, previousLevel);
    conn.close();

  }

  private int getPreviousLevel() throws Exception {
    Connection conn = ds.getConnection();
    int level = conn.getTransactionIsolation();
    conn.close();
    return level;
  }

  private void checkConn(Connection conn, boolean autoCommit, int level) throws Exception {
    assertThat(conn.getAutoCommit(), is(autoCommit));
    assertThat(conn.getTransactionIsolation(), is(level));
  }


  @DB(table = "account")
  interface AccountDao {

    @SQL("insert into #table(id, balance) values(:1.id, :1.balance)")
    public int insert(Account account);

    @SQL("update #table set balance=:1.balance where id=:1.id")
    public int update(Account account);

    @SQL("select id, balance from #table where id=:1")
    public Account getAccount(int id);

  }

}
