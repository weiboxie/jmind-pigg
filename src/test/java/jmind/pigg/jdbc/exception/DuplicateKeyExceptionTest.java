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

package jmind.pigg.jdbc.exception;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import jmind.pigg.jdbc.JdbcTemplate;
import jmind.pigg.jdbc.exception.DuplicateKeyException;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Table;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author xieweibo
 */
public class DuplicateKeyExceptionTest {

  private final static DataSource ds = DataSourceConfig.getDataSource();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void before() throws Exception {
    Connection conn = ds.getConnection();
    Table.PERSON.load(conn);
    conn.close();
  }


  // TODO
//  @Test
//  public void test() {
//    thrown.expect(DuplicateKeyException.class);
//    JdbcTemplate t = new JdbcTemplate();
//    t.update(ds, "insert into person(id, name) values(?, ?)", new Object[]{1, "ash"});
//    t.update(ds, "insert into person(id, name) values(?, ?)", new Object[]{1, "ash"});
//  }

}
