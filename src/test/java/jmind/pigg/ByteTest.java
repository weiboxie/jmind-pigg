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

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * 测试byte[]
 *
 * @author xieweibo
 */
public class ByteTest {

  private final static DataSource ds = DataSourceConfig.getDataSource();
  private final static Pigg pigg = Pigg.newInstance(ds);
  private final static ByteInfoDao dao = pigg.create(ByteInfoDao.class);

  @Before
  public void before() throws Exception {
    Connection conn = ds.getConnection();
    Table.BYTE_INFO.load(conn);
    conn.close();
  }

  @Test
  public void testByteInfo() {
    byte[] arrayByte = new byte[]{1, 2, 3};
    byte singleByte = 10;
    dao.insert(arrayByte, singleByte);
    int id = dao.insert(arrayByte, singleByte);
    assertThat(Arrays.toString(dao.getArrayByte(id)), equalTo(Arrays.toString(arrayByte)));
  }

  @DB
  interface ByteInfoDao {

    @GeneratedId
    @SQL("insert into byte_info(array_byte, single_byte) values(:1, :2)")
    public int insert(byte[] arrayByte, byte singleByte);

    @SQL("select array_byte from byte_info where id=:1")
    public byte[] getArrayByte(int id);

  }

}
