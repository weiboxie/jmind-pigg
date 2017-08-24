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

import org.junit.Before;
import org.junit.Test;

import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.GeneratedId;
import jmind.pigg.annotation.SQL;
import jmind.pigg.operator.Pigg;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Table;
import jmind.pigg.support.model4table.BT;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * 测试字段中含有boolean类型
 *
 * @author xieweibo
 */
public class BTTest {

  private final static DataSource ds = DataSourceConfig.getDataSource();
  private final static Pigg pigg = Pigg.newInstance(ds);
  private final static BTDao dao = pigg.create(BTDao.class);

  @Before
  public void before() throws Exception {
    Connection conn = ds.getConnection();
    Table.BT.load(conn);
    conn.close();
  }

  @Test
  public void test() {
    BT bt = new BT();
    bt.setOk(true);
    int id = dao.insert(bt);
    assertThat(dao.getBT(id).isOk(), equalTo(true));
    bt.setOk(false);
    id = dao.insert(bt);
    assertThat(dao.getBT(id).isOk(), equalTo(false));
  }


  @DB(table = "bt")
  interface BTDao {

    @GeneratedId
    @SQL("insert into #table(is_ok) values(:isOk)")
    public int insert(BT bt);

    @SQL("select id, is_ok from #table where id=:1")
    public BT getBT(int id);

  }

}
