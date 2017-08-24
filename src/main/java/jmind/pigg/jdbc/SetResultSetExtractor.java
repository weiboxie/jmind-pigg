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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import jmind.pigg.mapper.RowMapper;

/**
 * @author xieweibo
 */
public class SetResultSetExtractor<T> implements ResultSetExtractor<Set<T>> {

  private final SetSupplier setSupplier;
  private final RowMapper<T> rowMapper;

  public SetResultSetExtractor(SetSupplier setSupplier, RowMapper<T> rowMapper) {
    this.setSupplier = setSupplier;
    this.rowMapper = rowMapper;
  }

  @Override
  public Set<T> extractData(ResultSet rs) throws SQLException {
    Set<T> results = setSupplier.get(rowMapper.getMappedClass());
    int rowNum = 0;
    while (rs.next()) {
      results.add(rowMapper.mapRow(rs, rowNum++));
    }
    return results;
  }

}
