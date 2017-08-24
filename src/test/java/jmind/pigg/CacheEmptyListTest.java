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

package jmind.pigg;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import jmind.pigg.annotation.Cache;
import jmind.pigg.annotation.CacheBy;
import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.Param;
import jmind.pigg.annotation.GeneratedId;
import jmind.pigg.annotation.SQL;
import jmind.pigg.operator.Pigg;
import jmind.pigg.operator.cache.LocalCacheHandler;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Randoms;
import jmind.pigg.support.Table;
import jmind.pigg.support.model4table.Msg;

/**
 * @author xieweibo
 */
public class CacheEmptyListTest {

  private final static DataSource ds = DataSourceConfig.getDataSource();

  @Before
  public void before() throws Exception {
    Connection conn = ds.getConnection();
    Table.MSG.load(conn);
    conn.close();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSingleKeyReturnList() throws Exception {
    LocalCacheHandler cacheHandler = new LocalCacheHandler();
    List<Msg> msgs = new ArrayList<Msg>();
    Pigg pigg = Pigg.newInstance(ds);
    pigg.setCacheHandler(cacheHandler);
    MsgDao dao = pigg.create(MsgDao.class);
    int uid = 100;
    String key = getMsgKey(uid);

    List<Msg> actual = dao.getMsgs(uid);
    assertThat(actual, hasSize(0));
    assertThat(cacheHandler.get(key), nullValue());

    Msg msg = createRandomMsg(uid);
    msgs.add(msg);
    msg.setId(dao.insert(msg));

    actual = dao.getMsgs(uid);
    assertThat(actual, hasSize(msgs.size()));
    assertThat(actual, contains(msgs.toArray()));
    List<Msg> cacheActual = (List<Msg>) cacheHandler.get(key);
    assertThat(cacheActual, hasSize(msgs.size()));
    assertThat(cacheActual, contains(msgs.toArray()));


    msg = msgs.remove(0);
    dao.delete(msg.getUid(), msg.getId());
    actual = dao.getMsgs(uid);
    assertThat(actual, hasSize(0));
    assertThat(cacheHandler.get(key), nullValue());
  }

  private String getMsgKey(int uid) {
    return "msg_" + uid;
  }

  private Msg createRandomMsg(int uid) {
    String content = Randoms.randomString(20);
    Msg msg = new Msg();
    msg.setUid(uid);
    msg.setContent(content);
    return msg;
  }

  @DB
  @Cache(prefix = "msg", expire = 50000, cacheEmptyList = false)
  interface MsgDao {

    @GeneratedId
    @SQL("insert into msg(uid, content) values(:m.uid, :m.content)")
    public int insert(@CacheBy("uid") @Param("m") Msg msg);

    @SQL("delete from msg where uid=:1 and id=:2")
    public int delete(@CacheBy int uid, int id);

    @SQL("select id, uid, content from msg where uid=:1 order by id")
    public List<Msg> getMsgs(@CacheBy int uid);

  }

}
