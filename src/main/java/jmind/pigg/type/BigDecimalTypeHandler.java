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

package jmind.pigg.type;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jmind.pigg.util.jdbc.JdbcType;

/**
 * @author Clinton Begin
 * @author xieweibo
 */
public class BigDecimalTypeHandler extends BaseTypeHandler<BigDecimal> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int index, BigDecimal parameter, JdbcType jdbcType)
      throws SQLException {
    ps.setBigDecimal(index, parameter);
  }

  @Override
  public BigDecimal getNullableResult(ResultSet rs, int index)
      throws SQLException {
    return rs.getBigDecimal(index);
  }

  @Override
  public JdbcType getJdbcType() {
    return JdbcType.DECIMAL;
  }

}
