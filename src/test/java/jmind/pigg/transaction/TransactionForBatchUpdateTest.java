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

import com.google.common.collect.Lists;

import jmind.pigg.annotation.*;
import jmind.pigg.datasource.SimpleDataSourceFactory;
import jmind.pigg.operator.Pigg;
import jmind.pigg.sharding.DatabaseShardingStrategy;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Table;
import jmind.pigg.support.model4table.Msg;

import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author xieweibo
 */
public class TransactionForBatchUpdateTest {

  private final static DataSource ds = DataSourceConfig.getDataSource();
  private static Pigg pigg;

  static {
    pigg = Pigg.newInstance(ds);
  }

  @Before
  public void before() throws Exception {
    Table.MSG.load(ds);
  }

  @Test
  public void test() throws Exception {
    List<Msg> msgs = Lists.newArrayList();
    List<Msg> msg2s = Lists.newArrayList();
    int uid1 = 100;
    int uid2 = 200;
    for (int i = 0; i < 2; i++) {
      Msg msg = new Msg();
      msg.setUid(uid1);
      msg.setContent(String.valueOf(i + 1));
      msgs.add(msg);
      Msg msg2 = new Msg();
      msg2.setUid(uid2);
      msg2.setContent(String.valueOf(i + 1));
      msg2s.add(msg2);
    }
    Msg msg = new Msg();
    msg.setUid(uid1);
    String content = "";
    for (int i = 0; i < 1000; i++) {
      content = content + i;
    }
    msg.setContent(content);
    msgs.add(msg);
    Msg msg2 = new Msg();
    msg2.setUid(uid2);
    msg2.setContent(content);
    msg2s.add(msg);

    MsgDao dao = pigg.create(MsgDao.class);
    boolean ok = false;
    try {
      dao.batchInsert(msgs);
    } catch (Exception e) {
      ok = true;
    }
    assertThat(ok, equalTo(true));
    assertThat(dao.getMsgByUid(uid1).size(), equalTo(2));

    ok = false;
    try {
      dao.useTransactionForBatchInsert(msg2s);
    } catch (Exception e) {
      ok = true;
    }
    assertThat(ok, equalTo(true));
    assertThat(dao.getMsgByUid(uid2).size(), equalTo(0));
  }

  @DB(table = "msg")
  interface MsgDao {

    @SQL("insert into #table(uid, content) values(:uid, :content)")
    int batchInsert(List<Msg> msgs);

    @UseTransactionForBatchUpdate
    @SQL("insert into #table(uid, content) values(:uid, :content)")
    int useTransactionForBatchInsert(List<Msg> msgs);

    @SQL("select id, uid, content from #table where uid = :1")
    List<Msg> getMsgByUid(int uid);

  }

}
