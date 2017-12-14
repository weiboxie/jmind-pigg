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

package jmind.pigg.crud;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jmind.pigg.annotation.DB;
import jmind.pigg.operator.Pigg;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Table;
import jmind.pigg.support.model4table.Msg;

/**
 * @author xieweibo
 */
public class CommonCrudDaoTest {

    private final static DataSource ds = DataSourceConfig.getDataSource();
    private final static Pigg pigg = Pigg.newInstance(ds);

    // @Before
    public void before() throws Exception {
        Table.MSG.load(ds);
    }

    public static void main(String[] args) {
        MsgDao dao = pigg.create(MsgDao.class);
        //      System.out.println(GeneratedId.class.getSimpleName());
           Msg msg = new Msg();
           int id=13;
        msg.setId(id);
msg.setUid(101);
        msg.setUserName("bb");
        dao.updateAllField(msg);
        //      List<Msg> list=new ArrayList<>();
        //      for(int i=10;i<15;i++){
        //          msg=new Msg();
        //          msg.setUserName("wa"+i);
        //          msg.setPid(i);
        //       msg.setId(i);
        //       list.add(msg);
        //      }
        //     
        //      int[] a = dao.set(list);
        //     System.err.println(list);
        //     
        //     msg.setUid(101);
        //     msg.setContent(null);
        //     msg.setUserName("wave");

//        List<Msg> all = dao.findAll(Arrays.asList(13, 14, 15));
//        System.out.println(all);


              System.out.println(dao.findOne(id));
              System.out.println("---------------"+dao.count());
        //     System.out.println(dao.findAll(Arrays.asList(2,3)));

    }

    // @Test
    public void test() throws Exception {
        MsgDao dao = pigg.create(MsgDao.class);
        Msg msg = Msg.createRandomMsg();
        int id = dao.saveAndGeneratedId(msg);
        msg.setId(id);
        assertThat(dao.findOne(id), equalTo(msg));
        Msg msg2 = Msg.createRandomMsg();
        dao.save(msg2);
        Msg msg3 = Msg.createRandomMsg();
        int id3 = dao.saveAndGeneratedId(msg3);
        assertThat(dao.count(), equalTo(3L));
        msg3.setId(id3);
        assertThat(dao.findOne(id3), equalTo(msg3));
        assertThat(dao.findAll().size(), equalTo(3));
        List<Integer> ids = Lists.newArrayList(id, id3);
        List<Msg> msgs = dao.findAll(ids);
        assertThat(msgs.size(), equalTo(2));
        Map<Integer, Msg> mapping = Maps.newHashMap();
        mapping.put(id, msg);
        mapping.put(id3, msg3);
        for (Msg actualMsg : msgs) {
            assertThat(actualMsg, equalTo(mapping.get(actualMsg.getId())));
        }
        msg.setContent("ash");
        int r = dao.update(msg);
        assertThat(r, equalTo(1));
        assertThat(dao.findOne(id), equalTo(msg));
        msgs = Lists.newArrayList(msg, msg3);
        int[] rr = dao.update(msgs);
        assertThat(rr, equalTo(new int[] { 1, 1 }));

        msg.setId(-1);
        msg3.setId(-3);
        r = dao.update(msg);
        assertThat(r, equalTo(0));
        rr = dao.update(msgs);
        assertThat(rr, equalTo(new int[] { 0, 0 }));

        dao.delete(id);
        assertThat(dao.findOne(id), nullValue());
        msgs = Msg.createRandomMsgs(5);
        dao.save(msgs);
    }

    @Test
    public void test2() throws Exception {
        MsgDao dao = pigg.create(MsgDao.class);
        Msg msg = Msg.createRandomMsg();
        int id = dao.saveAndGeneratedId(msg);
        msg.setId(id);
        assertThat(dao.findOne(id), equalTo(msg));
        Msg msg2 = Msg.createRandomMsg();
        dao.save(msg2);
        Msg msg3 = Msg.createRandomMsg();
        int id3 = dao.saveAndGeneratedId(msg3);
        assertThat(dao.findAll().size(), equalTo(3));
        assertThat(dao.count(), equalTo(3L));
        msg3.setId(id3);
        assertThat(dao.findOne(id3), equalTo(msg3));
        List<Integer> ids = Lists.newArrayList(id, id3);
        List<Msg> msgs = dao.findAll(ids);
        assertThat(msgs.size(), equalTo(2));
        Map<Integer, Msg> mapping = Maps.newHashMap();
        mapping.put(id, msg);
        mapping.put(id3, msg3);
        for (Msg actualMsg : msgs) {
            assertThat(actualMsg, equalTo(mapping.get(actualMsg.getId())));
        }

        msg.setContent("ash");
        int r = dao.update(msg);
        assertThat(r, equalTo(1));
        assertThat(dao.findOne(id), equalTo(msg));
        msgs = Lists.newArrayList(msg, msg3);
        int[] rr = dao.update(msgs);
        assertThat(rr, equalTo(new int[] { 1, 1 }));

        msg.setId(-1);
        msg3.setId(-3);
        r = dao.update(msg);
        assertThat(r, equalTo(0));
        rr = dao.update(msgs);
        assertThat(rr, equalTo(new int[] { 0, 0 }));

        dao.delete(id);
        assertThat(dao.findOne(id), nullValue());
        msgs = Msg.createRandomMsgs(5);
        dao.save(msgs);
    }

    @DB(table = "msg")
    interface MsgDao extends CrudRepository<Msg, Integer> {

        Msg findByIdLessThanOrderByIdDesc(int id);

        List<Msg> getByIdLessThanOrderByIdDesc(int id);

    }

    //  @DB(table = "msg")
    //  interface MsgDao2  {
    //      @SQL("insert into #table values(null,:uid,:content)")
    //      @GeneratedId
    //     int  add(Msg entity);
    //      @SQL("select * from msg where id=:1")
    //      Msg get(int id);
    //   
    //  }

}