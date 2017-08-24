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

package jmind.pigg.plugin.page;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.GeneratedId;
import jmind.pigg.annotation.SQL;
import jmind.pigg.operator.Pigg;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Table;
import jmind.pigg.support.model4table.Msg;

/**
 * @author xieweibo
 */
public class MySQLPageInterceptorTest {

  private final static DataSource ds = DataSourceConfig.getDataSource();
  private static MsgDao dao;
  {
    Pigg pigg = Pigg.newInstance(ds);
    pigg.addInterceptor(new MySQLPageInterceptor());
    dao = pigg.create(MsgDao.class);
  }

  //@Before
  public void before() throws Exception {
    Table.MSG.load(ds);
  }

  @Test
  public void interceptQuery() throws Exception {
    if (DataSourceConfig.isUseMySQL()) {
      int uid = 102;
      String content = "shi";

      List<Msg> expected = new ArrayList<Msg>();
//      for (int i = 0; i < 10; i++) {
//        Msg msg = new Msg();
//        msg.setUid(uid);
//        msg.setContent(content);
//        int id = dao.insert(msg);
//        msg.setId(id);
//        expected.add(msg);
//      }

      List<Msg> actual = new ArrayList<Msg>();

      Page page = Page.create(1, 5);
     page.setOderBy(" id desc");
      List<Msg> msgs = dao.getMsgs(uid, page);
     System.out.println("ss="+msgs);
//      assertThat(page.getTotal(), is(10));
//      assertThat(msgs.size(), is(3));
//
//      page = Page.create(2, 3);
//      msgs = dao.getMsgs(uid, page);
//      actual.addAll(msgs);
//      assertThat(page.getTotal(), is(10));
//      assertThat(msgs.size(), is(3));
//
//      page = Page.create(3, 3);
//      msgs = dao.getMsgs(uid, page);
//      actual.addAll(msgs);
//      assertThat(page.getTotal(), is(10));
//      assertThat(msgs.size(), is(3));
//
//      page = Page.create(4, 3);
//      msgs = dao.getMsgs(uid, page);
//      actual.addAll(msgs);
//      assertThat(page.getTotal(), is(10));
//      assertThat(msgs.size(), is(1));
//
//      assertThat(actual, equalTo(expected));
    }
  }

  @DB
  interface MsgDao {

    @GeneratedId
    @SQL("insert into msg(uid, content) values(:uid, :content)")
    public int insert(Msg msg);

    @SQL("select id, uid, content from msg where uid = :1")
    public List<Msg> getMsgs(int uid, Page page);

  }

}