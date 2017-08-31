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

import jmind.pigg.annotation.*;
import jmind.pigg.datasource.SimpleDataSourceFactory;
import jmind.pigg.operator.Pigg;
import jmind.pigg.sharding.DatabaseShardingStrategy;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Table;
import jmind.pigg.support.model4table.Msg;
import jmind.pigg.transaction.Transaction;
import jmind.pigg.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * 测试事务
 *
 * @author xieweibo
 */
public class ParallelTransactionTest {

  private final static DataSource ds1 = DataSourceConfig.getDataSource(1, false, 4);
  private final static DataSource ds2 = DataSourceConfig.getDataSource(2, false, 4);
  private static Pigg pigg;

  static {
    pigg = Pigg.newInstance(Arrays.asList(new SimpleDataSourceFactory("db1", ds1), new SimpleDataSourceFactory("db2", ds2)));
  }

  private final static MsgDao dao = pigg.create(MsgDao.class);

  @Before
  public void before() throws Exception {
    Table.MSG.load(ds1);
    Table.MSG.load(ds2);
  }

  @Test
  public void testCommit() throws Exception {
    int threadNum = 4;
    final int taskPerThread = 10;
    final AtomicInteger uid = new AtomicInteger(1);
    Thread[] threads = new Thread[threadNum];
    for (int i = 0; i < threadNum; i++) {
      threads[i] = new Thread(new Runnable() {
        @Override
        public void run() {
          for (int i = 0; i < taskPerThread; i++) {
            Msg msg = new Msg();
            int ruid = uid.getAndIncrement();
            msg.setUid(ruid);
            msg.setContent("content");
            String database = "db" + (ruid % 2 + 1);
            Transaction tx = TransactionFactory.newTransaction(pigg, database);
            int id = dao.insert(msg);
            tx.commit();
            msg.setId(id);
            assertThat(dao.getMsgById(ruid, id), equalTo(msg));
          }
        }
      });
    }
    for (int i = 0; i < threadNum; i++) {
      threads[i].start();
    }
    for (int i = 0; i < threadNum; i++) {
      threads[i].join();
    }

  }

  @DB(table = "msg")
  @Sharding(databaseShardingStrategy = MsgDatabaseShardingStrategy.class)
  interface MsgDao {

    @GeneratedId
    @SQL("insert into #table(uid, content) values(:1.uid, :1.content)")
    int insert(@ShardingBy("uid") Msg msg);

    @SQL("select id, uid, content from #table where id = :2")
    public Msg getMsgById(@ShardingBy int uid, int id);

  }

  static class MsgDatabaseShardingStrategy implements DatabaseShardingStrategy<Integer> {

    @Override
    public String getDataSourceFactoryName(Integer uid) {
      return "db" + (uid % 2 + 1);
    }

  }

}
