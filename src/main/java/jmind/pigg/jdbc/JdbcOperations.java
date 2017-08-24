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

package jmind.pigg.jdbc;

import javax.sql.DataSource;

import jmind.pigg.binding.BoundSql;
import jmind.pigg.jdbc.exception.DataAccessException;
import jmind.pigg.mapper.RowMapper;

import java.util.List;
import java.util.Set;

/**
 * @author xieweibo
 */
public interface JdbcOperations {

  public <T> T queryForObject(DataSource ds, BoundSql boundSql, RowMapper<T> rowMapper)
      throws DataAccessException;

  public <T> List<T> queryForList(DataSource ds, BoundSql boundSql,
                                  ListSupplier listSupplier, RowMapper<T> rowMapper)
      throws DataAccessException;

  public <T> Set<T> queryForSet(DataSource ds, BoundSql boundSql,
                                SetSupplier setSupplier, RowMapper<T> rowMapper)
      throws DataAccessException;

  public <T> Object queryForArray(DataSource ds, BoundSql boundSql, RowMapper<T> rowMapper)
      throws DataAccessException;

  public int update(DataSource ds, BoundSql boundSql)
      throws DataAccessException;

  public int update(DataSource ds, BoundSql boundSql, GeneratedKeyHolder holder)
      throws DataAccessException;

  public int[] batchUpdate(DataSource ds, List<BoundSql> boundSql)
      throws DataAccessException;

}
