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

package jmind.pigg.jdbc;

import org.junit.Before;
import org.junit.Test;

import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.Mapper;
import jmind.pigg.annotation.GeneratedId;
import jmind.pigg.annotation.SQL;
import jmind.pigg.mapper.AbstractRowMapper;
import jmind.pigg.operator.Pigg;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Table;
import jmind.pigg.support.model4table.Msg;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * 自定义RowMapper测试
 *
 * @author xieweibo
 */
public class MapperTest {

  private final static DataSource ds = DataSourceConfig.getDataSource();
  private final static Pigg pigg = Pigg.newInstance(ds);

  @Before
  public void before() throws Exception {
    Connection conn = ds.getConnection();
    Table.MSG.load(conn);
    conn.close();
  }

  @Test
  public void test() {
    MsgDao dao = pigg.create(MsgDao.class);
    int num = 5;
    List<Msg> msgs = Msg.createRandomMsgs(num);
    List<Integer> ids = new ArrayList<Integer>();
    for (Msg msg : msgs) {
      int id = dao.insert(msg.getUid(), msg.getContent());
      assertThat(id, greaterThan(0));
      msg.setId(id);
      ids.add(id);
    }

    List<Msg> dbMsgs = dao.getMsgs(ids);
    assertThat(dbMsgs, hasSize(msgs.size()));
    assertThat(dbMsgs, containsInAnyOrder(msgs.toArray()));
    Msg msg = msgs.get(0);
    assertThat(dao.getMsg(msg.getId()), equalTo(msg));
  }

  @DB(table = "msg")
  interface MsgDao {

    @GeneratedId
    @SQL("insert into #table(uid, content) values(:1, :2)")
    int insert(int uid, String content);

    @SQL("select id, uid, content from #table where id in (:1) order by id")
    @Mapper(MsgMapper.class)
    public List<Msg> getMsgs(List<Integer> ids);

    @SQL("select id, uid, content from #table where id = :1")
    public Msg getMsg(int id);

  }

  public static class MsgMapper extends AbstractRowMapper<Msg> {

    @Override
    public Msg mapRow(ResultSet rs, int rowNum) throws SQLException {
      Msg msg = new Msg();

      msg.setId(rs.getInt("id"));
      msg.setUid(rs.getInt("uid"));
      msg.setContent(rs.getString("content"));
      return msg;
    }
  }

}
