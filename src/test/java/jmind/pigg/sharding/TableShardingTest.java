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

package jmind.pigg.sharding;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jmind.pigg.annotation.*;
import jmind.pigg.operator.Pigg;
import jmind.pigg.sharding.ModTenTableShardingStrategy;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Randoms;
import jmind.pigg.support.Table;
import jmind.pigg.support.model4table.Msg;

import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * 测试分表
 *
 * @author xieweibo
 */
public class TableShardingTest {

  private final static DataSource ds = DataSourceConfig.getDataSource();
  private final static Pigg pigg = Pigg.newInstance(ds);
  private final static MsgDao dao = pigg.create(MsgDao.class);

  @Before
  public void before() throws Exception {
    Connection conn = ds.getConnection();
    Table.MSG_PARTITION.load(conn);
    conn.close();
  }

  @Test
  public void testRandomPartition() {
    int num = 10;
    List<Msg> msgs = Msg.createRandomMsgs(num);
    for (Msg msg : msgs) {
      int id = dao.insert(msg);
      assertThat(id, greaterThan(0));
      msg.setId(id);
    }
    check(msgs);
    for (Msg msg : msgs) {
      msg.setContent(Randoms.randomString(20));
    }
    dao.batchUpdate(msgs);
    check(msgs);
  }

  @Test
  public void testOnePartition() {
    int num = 10;
    int uid = 100;
    List<Msg> msgs = new ArrayList<Msg>();
    for (int i = 0; i < num; i++) {
      Msg msg = new Msg();
      msg.setUid(uid);
      msg.setContent(Randoms.randomString(20));
      msgs.add(msg);
      int id = dao.insert(msg);
      msg.setId(id);
    }
    check(msgs);
    for (Msg msg : msgs) {
      msg.setContent(Randoms.randomString(20));
    }
    dao.batchUpdate(msgs);
    check(msgs);
  }

  private void check(List<Msg> msgs) {
    List<Msg> dbMsgs = new ArrayList<Msg>();
    Multiset<Integer> ms = HashMultiset.create();
    for (Msg msg : msgs) {
      ms.add(msg.getUid());
    }
    for (Multiset.Entry<Integer> entry : ms.entrySet()) {
      dbMsgs.addAll(dao.getMsgs(entry.getElement()));
    }
    assertThat(dbMsgs, hasSize(msgs.size()));
    assertThat(dbMsgs, containsInAnyOrder(msgs.toArray()));
  }

  @DB(table = "msg")
  @Sharding(tableShardingStrategy = ModTenTableShardingStrategy.class)
  interface MsgDao {

    @GeneratedId
    @SQL("insert into #table(uid, content) values(:1.uid, :1.content)")
    int insert(@ShardingBy("uid") Msg msg);

    @SQL("update #table set content=:1.content where id=:1.id and uid=:1.uid")
    public int[] batchUpdate(@ShardingBy("uid") List<Msg> msgs);

    @SQL("select id, uid, content from #table where uid=:1")
    public List<Msg> getMsgs(@ShardingBy int uid);

  }

}
