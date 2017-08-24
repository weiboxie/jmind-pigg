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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import jmind.pigg.annotation.DB;
import jmind.pigg.crud.CrudRepository;
import jmind.pigg.crud.CrudException;
import jmind.pigg.operator.Pigg;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Table;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author xieweibo
 */
public class CustomCrudDaoTest {

  private final static DataSource ds = DataSourceConfig.getDataSource();
  private final static Pigg pigg = Pigg.newInstance(ds);

  @Before
  public void before() throws Exception {
    Table.ORDER.load(ds);
  }

  @Test
  public void test() throws Exception {
    CrudOrderDao dao = pigg.create(CrudOrderDao.class);
    int userId = 1;
    HashSet<CrudOrder> cos = Sets.newHashSet();
    List<String> ids = Lists.newArrayList();
    CrudOrder co = CrudOrder.createRandomCrudOrder(userId);
    dao.save(co);
    ids.add(co.getId());
    cos.add(co);
    co = CrudOrder.createRandomCrudOrder(userId);
    ids.add(co.getId());
    dao.save(co);
    cos.add(co);

    assertThat(dao.getById(co.getId()), equalTo(co));
    assertThat(dao.getByUserIdAndId(co.getUserId(), co.getId()), equalTo(co));
    HashSet<CrudOrder> actualCos = Sets.newHashSet(dao.getByIdIn(ids));
    assertThat(actualCos, equalTo(cos));
    assertThat(dao.countByUserId(userId), equalTo(2));
    assertThat(dao.deleteByUserId(userId), equalTo(2));
    assertThat(dao.countByUserId(userId), equalTo(0));
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void test2() throws Throwable {
    thrown.expect(CrudException.class);
    thrown.expectMessage("the type of 1th parameters of method [getById] expected 'class java.lang.String', but 'int'");
    try {
      pigg.create(CrudOrder2Dao.class);
    } catch (Exception e) {
      throw e.getCause();
    }
  }

  @Test
  public void test3() throws Throwable {
    thrown.expect(CrudException.class);
    thrown.expectMessage("can't convert method [abc] to SQL");
    try {
      pigg.create(CrudOrder3Dao.class);
    } catch (Exception e) {
      throw e.getCause();
    }

  }

  @Test
  public void test4() throws Throwable {
    thrown.expect(CrudException.class);
    thrown.expectMessage("the type of 1th parameters of method [getByIdIn] expected iterable, but 'int'");
    try {
      pigg.create(CrudOrder4Dao.class);
    } catch (Exception e) {
      throw e.getCause();
    }

  }

  @Test
  public void test5() throws Throwable {
    thrown.expect(CrudException.class);
    thrown.expectMessage("the type of 1th parameters of method [getByIdIn] error");
    try {
      pigg.create(CrudOrder5Dao.class);
    } catch (Exception e) {
      throw e.getCause();
    }

  }

  @DB(table = "t_order")
  interface CrudOrderDao extends CrudRepository<CrudOrder, String> {

    CrudOrder getById(String id);

    List<CrudOrder> getByIdIn(List<String> ids);

    CrudOrder getByUserIdAndId(int userId, String id);

    int countByUserId(int userId);

    int deleteByUserId(int userId);

  }

  @DB(table = "t_order")
  interface CrudOrder2Dao extends CrudRepository<CrudOrder, String> {

    CrudOrder getById(int id);

  }

  @DB(table = "t_order")
  interface CrudOrder3Dao extends CrudRepository<CrudOrder, String> {

    CrudOrder abc(int id);

  }

  @DB(table = "t_order")
  interface CrudOrder4Dao extends CrudRepository<CrudOrder, String> {

    CrudOrder getByIdIn(int id);

  }

  @DB(table = "t_order")
  interface CrudOrder5Dao extends CrudRepository<CrudOrder, String> {

    CrudOrder getByIdIn(List<Integer> ids);

  }

}
