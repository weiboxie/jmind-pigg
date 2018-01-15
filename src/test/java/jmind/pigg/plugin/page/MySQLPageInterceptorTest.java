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

import com.alibaba.fastjson.JSON;
import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.GeneratedId;
import jmind.pigg.annotation.SQL;
import jmind.pigg.crud.CrudRepository;
import jmind.pigg.operator.Pigg;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.model4table.Msg;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xieweibo
 */
public class MySQLPageInterceptorTest {

  private final static DataSource ds = DataSourceConfig.getDataSource();
  private static MsgDao dao;

  public static void main(String[] args) {

    Pigg pigg = Pigg.newInstance(ds);
    pigg.addInterceptor(new MySQLPageInterceptor());
    dao = pigg.create(MsgDao.class);
    Msg msg=new Msg();
    msg.setId(12);
    msg.setPid(10);
    msg.setContent("wavsss");
     int insert=dao.save(msg);
  System.err.println("ii="+insert);

  }
  //@Before



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
     page.setOrderBy(" id desc");
      List<Msg> msgs = dao.getMsgs(1,page);
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

  @DB(table="msg2")
  interface MsgDao extends CrudRepository<Msg,Integer> {

    @GeneratedId
    @SQL("insert into msg(uid, content) values(:uid, :content)")
    public int insert(Msg msg);

    @SQL("select id, uid, content from msg where uid = :1")
    public List<Msg> getMsgs( int uid,Page page);

    @SQL("select id, uid, content from msg where uid = :1 #if(:2>0) and pid>0 #end")
    public List<Msg> gets(int uid, int pid);


    public List<Msg>  getByUid(int uid,Page page);

    public List<Msg>  getByUid(int uid);

    @SQL("select * from #table")
    public List<Msg> selectAll();

  }

}