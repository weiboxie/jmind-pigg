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

import org.junit.Before;
import org.junit.Test;

import jmind.pigg.annotation.DB;
import jmind.pigg.crud.CrudRepository;
import jmind.pigg.operator.Pigg;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Table;

import javax.sql.DataSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author xieweibo
 */
public class CrudRepositoryTest {

  private final static DataSource ds = DataSourceConfig.getDataSource();
  private final static Pigg pigg = Pigg.newInstance(ds);

  @Before
  public void before() throws Exception {
    Table.ORDER.load(ds);
  }

  @Test
  public void test() throws Exception {
    int userId = 1;
    CrudOrderDao dao = pigg.create(CrudOrderDao.class);
    CrudOrder co = CrudOrder.createRandomCrudOrder(userId);
    dao.save(co);
    assertThat(dao.getById(co.getId()), equalTo(co));
    assertThat(dao.findOne(co.getId()), equalTo(co));
    assertThat(dao.delete(co.getId()), equalTo(1));
    assertThat(dao.findAll().size(), equalTo(0));
    assertThat(dao.count(), equalTo(0L));
  }



  @DB(table = "t_order")
  interface CrudOrderDao extends CrudRepository<CrudOrder, String> {

    CrudOrder getById(String id);

  }

}
