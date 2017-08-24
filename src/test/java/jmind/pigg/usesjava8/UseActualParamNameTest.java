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

package jmind.pigg.usesjava8;

import org.junit.Before;
import org.junit.Test;

import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.SQL;
import jmind.pigg.operator.Pigg;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Table;
import jmind.pigg.support.model4table.User;

import javax.sql.DataSource;

/**
 * @author xieweibo
 */
public class UseActualParamNameTest {

  private final static DataSource ds = DataSourceConfig.getDataSource();
  private final static Pigg pigg = Pigg.newInstance(ds);
  {
    pigg.setUseActualParamName(true);
  }

  @Before
  public void before() throws Exception {
    Table.USER.load(ds);
  }

  @Test
  public void test() throws Exception {
    UserDao dao = pigg.create(UserDao.class);
    dao.getUser(1);
  }

  @DB()
  static interface UserDao {

    @SQL("select id, name, age, gender, money, update_time from user where id = :id")
    public User getUser(int id);

  }

}
