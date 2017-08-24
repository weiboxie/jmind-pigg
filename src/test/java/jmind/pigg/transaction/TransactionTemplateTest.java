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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.SQL;
import jmind.pigg.datasource.AbstractDataSourceFactory;
import jmind.pigg.operator.Pigg;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Table;
import jmind.pigg.support.model4table.Account;
import jmind.pigg.transaction.TransactionAction;
import jmind.pigg.transaction.TransactionStatus;
import jmind.pigg.transaction.TransactionTemplate;

import javax.sql.DataSource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author xieweibo
 */
public class TransactionTemplateTest {

  private final static DataSource ds = DataSourceConfig.getDataSource();
  private final static Pigg pigg = Pigg.newInstance(ds);
  private final static AccountDao dao = pigg.create(AccountDao.class);

  @Before
  public void before() throws Exception {
    Table.ACCOUNT.load(ds);
  }

  @Test
  public void testCommit() throws Exception {
    final Account x = new Account(1, 1000);
    final Account y = new Account(2, 1000);
    final Account z = new Account(3, 1000);
    dao.insert(x);
    dao.insert(y);
    dao.insert(z);

    TransactionTemplate.execute(pigg, AbstractDataSourceFactory.DEFULT_NAME, new TransactionAction() {
      @Override
      public void doInTransaction(TransactionStatus status) {
        x.add(50);
        dao.update(x);

        TransactionTemplate.execute(pigg, AbstractDataSourceFactory.DEFULT_NAME, new TransactionAction() {

          @Override
          public void doInTransaction(TransactionStatus status) {
            TransactionTemplate.execute(pigg, AbstractDataSourceFactory.DEFULT_NAME, new TransactionAction() {

              @Override
              public void doInTransaction(TransactionStatus status) {
                z.sub(30);
                dao.update(z);
              }
            });

            y.sub(20);
            dao.update(y);
          }
        });
      }
    });

    assertThat(dao.getAccount(1), equalTo(x));
    assertThat(dao.getAccount(2), equalTo(y));
    assertThat(dao.getAccount(3), equalTo(z));
  }

  @Test
  public void testRollback() throws Exception {
    final Account x = new Account(1, 1000);
    final Account y = new Account(2, 2000);
    final Account z = new Account(3, 3000);
    dao.insert(x);
    dao.insert(y);
    dao.insert(z);

    TransactionTemplate.execute(pigg, AbstractDataSourceFactory.DEFULT_NAME, new TransactionAction() {
      @Override
      public void doInTransaction(TransactionStatus status) {
        x.add(50);
        dao.update(x);

        TransactionTemplate.execute(pigg, AbstractDataSourceFactory.DEFULT_NAME, new TransactionAction() {

          @Override
          public void doInTransaction(TransactionStatus status) {
            TransactionTemplate.execute(pigg, AbstractDataSourceFactory.DEFULT_NAME, new TransactionAction() {

              @Override
              public void doInTransaction(TransactionStatus status) {
                z.sub(30);
                dao.update(z);
                status.setRollbackOnly(true);
              }
            });
            y.sub(20);
            dao.update(y);
          }
        });
      }
    });

    assertThat(dao.getAccount(1).getBalance(), equalTo(1000));
    assertThat(dao.getAccount(2).getBalance(), equalTo(2000));
    assertThat(dao.getAccount(3).getBalance(), equalTo(3000));
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testRollback2() throws Exception {
    thrown.expect(RuntimeException.class);

    final Account x = new Account(1, 1000);
    final Account y = new Account(2, 2000);
    final Account z = new Account(3, 3000);
    dao.insert(x);
    dao.insert(y);
    dao.insert(z);

    TransactionTemplate.execute(pigg, AbstractDataSourceFactory.DEFULT_NAME, new TransactionAction() {
      @Override
      public void doInTransaction(TransactionStatus status) {
        x.add(50);
        dao.update(x);

        TransactionTemplate.execute(pigg, AbstractDataSourceFactory.DEFULT_NAME, new TransactionAction() {

          @Override
          public void doInTransaction(TransactionStatus status) {
            TransactionTemplate.execute(pigg, AbstractDataSourceFactory.DEFULT_NAME, new TransactionAction() {

              @Override
              public void doInTransaction(TransactionStatus status) {
                z.sub(30);
                dao.update(z);
                throw new RuntimeException();
              }
            });
            y.sub(20);
            dao.update(y);
          }
        });
      }
    });

    assertThat(dao.getAccount(1).getBalance(), equalTo(1000));
    assertThat(dao.getAccount(2).getBalance(), equalTo(2000));
    assertThat(dao.getAccount(3).getBalance(), equalTo(3000));
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
