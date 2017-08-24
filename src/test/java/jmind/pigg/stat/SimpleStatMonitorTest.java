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

package jmind.pigg.stat;

import org.junit.Before;
import org.junit.Test;

import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.GeneratedId;
import jmind.pigg.annotation.SQL;
import jmind.pigg.operator.Pigg;
import jmind.pigg.stat.SimpleStatMonitor;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Randoms;
import jmind.pigg.support.Table;
import jmind.pigg.support.model4table.User;
import jmind.pigg.util.logging.PiggLogger;

import javax.sql.DataSource;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author xieweibo
 */
public class SimpleStatMonitorTest {

  @Test
  public void test() throws Exception {
    DataSource ds = DataSourceConfig.getDataSource();
    Table.USER.load(ds);
    Pigg pigg = Pigg.newInstance(ds);
    pigg.setStatMonitor(new SimpleStatMonitor(1));
    UserDao dao = pigg.create(UserDao.class);
    int id = dao.insertUser(createRandomUser());
    long end = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);
    while (System.currentTimeMillis() < end) {
      dao.getName(id);
    }
    pigg.shutDownStatMonitor();
  }

  @DB(table = "user")
  static interface UserDao {

    @SQL("select name from #table where id = :1")
    public String getName(int id);

    @GeneratedId
    @SQL("insert into user(name, age, gender, money, update_time) " +
        "values(:1.name, :1.age, :1.gender, :1.money, :1.updateTime)")
    public int insertUser(User user);

  }

  private static User createRandomUser() {
    Random r = new Random();
    String name = Randoms.randomString(20);
    int age = r.nextInt(200);
    boolean gender = r.nextBoolean();
    long money = r.nextInt(1000000);
    Date date = new Date();
    User user = new User(name, age, gender, money, date);
    return user;
  }

}
