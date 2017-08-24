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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.Connection;
import java.util.Random;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import jmind.pigg.annotation.Cache;
import jmind.pigg.annotation.CacheBy;
import jmind.pigg.annotation.CacheIgnored;
import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.SQL;
import jmind.pigg.operator.Pigg;
import jmind.pigg.operator.cache.LocalCacheHandler;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Table;
import jmind.pigg.support.model4table.Position;

/**
 * @author xieweibo
 */
public class MultiCacheByTest {

  private final static DataSource ds = DataSourceConfig.getDataSource();

  @Before
  public void before() throws Exception {
    Connection conn = ds.getConnection();
    Table.POSITION.load(conn);
    conn.close();
  }


  @Test
  public void test() throws Exception {
    LocalCacheHandler cacheHandler = new LocalCacheHandler();
    Pigg pigg = Pigg.newInstance(ds);
    pigg.setCacheHandler(cacheHandler);
    PositionDao dao = pigg.create(PositionDao.class);
    Position p = createRandomPosition();
    dao.insert(p);
    String key = getKey(p);
    assertThat(cacheHandler.get(key), nullValue());
    assertThat(dao.get(p.getX(), p.getY()), equalTo(p));
    assertThat((Position) cacheHandler.get(key), equalTo(p));
    assertThat(dao.get(p.getX(), p.getY()), equalTo(p));

    p.setV(9527);
    assertThat(dao.update(p), is(1));
    assertThat(cacheHandler.get(key), nullValue());
    assertThat(dao.get(p.getX(), p.getY()), equalTo(p));
    assertThat((Position) cacheHandler.get(key), equalTo(p));

    dao.delete(p.getX(), p.getY());
    assertThat(cacheHandler.get(key), nullValue());
    assertThat(dao.get(p.getX(), p.getY()), nullValue());
  }

  @DB(table = "pos")
  @Cache(prefix = "pos", expire = 50000)
  interface PositionDao {

    @CacheIgnored
    @SQL("insert into #table(x, y, v) values(:x, :y, :v)")
    void insert(@CacheBy("x, y") Position p);

    @SQL("delete from #table where x = :1 and y = :2")
    boolean delete(@CacheBy int x, @CacheBy int y);

    @SQL("update #table set v = :v where x = :x and y = :y")
    int update(@CacheBy("x,y") Position p);

    @SQL("select x, y, v from #table where x = :1 and y = :2")
    Position get(@CacheBy int x, @CacheBy int y);

  }

  private Position createRandomPosition() {
    Random r = new Random();
    int x = r.nextInt(100000);
    int y = r.nextInt(100000);
    int v = r.nextInt(100000);
    return new Position(x, y, v);
  }

  private String getKey(Position p) {
    return "pos_" + p.getX() + "_" + p.getY();
  }

}













