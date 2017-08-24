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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.SQL;
import jmind.pigg.annotation.Sharding;
import jmind.pigg.annotation.ShardingBy;
import jmind.pigg.operator.Pigg;
import jmind.pigg.sharding.DatabaseShardingStrategy;
import jmind.pigg.sharding.TableShardingStrategy;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Table;
import jmind.pigg.support.model4table.User;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author xieweibo
 */
public class ShardingExceptionTest {

  private final static DataSource ds = DataSourceConfig.getDataSource();
  private final static Pigg pigg = Pigg.newInstance(ds);
  static {
    pigg.setLazyInit(true);
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void before() throws Exception {
    Connection conn = ds.getConnection();
    Table.USER.load(conn);
    conn.close();
  }

  @Test
  public void testGenericException() throws Exception {
    thrown.expect(ClassCastException.class);
    thrown.expectMessage("DatabaseShardingStrategy[class jmind.pigg.sharding.ShardingExceptionTest$UserDatabaseShardingStrategy]'s generic type[class java.lang.String] must be assignable from the type of parameter Modified @DatabaseShardingBy [long], please note that @ShardingBy = @TableShardingBy + @DatabaseShardingBy");
    UserDao dao = pigg.create(UserDao.class);
    dao.getUser(1);
  }

  @Test
  public void testGenericException2() throws Exception {
    thrown.expect(ClassCastException.class);
    thrown.expectMessage("TableShardingStrategy[class jmind.pigg.sharding.ShardingExceptionTest$UserTableShardingStrategy]'s generic type[class java.lang.String] must be assignable from the type of parameter Modified @TableShardingBy [long], please note that @ShardingBy = @TableShardingBy + @DatabaseShardingBy");
    UserDao2 dao = pigg.create(UserDao2.class);
    dao.getUser(1);
  }

  @DB(table = "user")
  @Sharding(databaseShardingStrategy = UserDatabaseShardingStrategy.class)
  static interface UserDao {

    @SQL("select id, name, age, gender, money, update_time from #table where id = :1")
    public User getUser(@ShardingBy long id);

  }

  static class UserDatabaseShardingStrategy implements DatabaseShardingStrategy<String> {

    @Override
    public String getDataSourceFactoryName(String uid) {
      return "xx";

    }
  }

  @DB(table = "user")
  @Sharding(tableShardingStrategy = UserTableShardingStrategy.class)
  static interface UserDao2 {

    @SQL("select id, name, age, gender, money, update_time from #table where id = :1")
    public User getUser(@ShardingBy long id);

  }

  static class UserTableShardingStrategy implements TableShardingStrategy<String> {

    @Override
    public String getTargetTable(String table, String shardParam) {
      return table;
    }

  }

}
