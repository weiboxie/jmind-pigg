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

package jmind.pigg.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import jmind.pigg.type.TypeHandler;
import jmind.pigg.type.TypeHandlerRegistry;
import jmind.pigg.util.jdbc.ResultSetWrapper;

/**
 * 单列RowMapper
 *
 * @author xieweibo
 */
public class SingleColumnRowMapper<T> implements RowMapper<T> {

  private Class<T> mappedClass;

  public SingleColumnRowMapper(Class<T> mappedClass) {
    this.mappedClass = mappedClass;
  }

  @SuppressWarnings("unchecked")
  public T mapRow(ResultSet rs, int rowNum) throws SQLException {
    ResultSetWrapper rsw = new ResultSetWrapper(rs);

    /**
     * 在对oralce进行单列分页查询时会引入行号变量，导致单列变成双列，所以去掉下面的检测
     */
    //if (rsw.getColumnCount() != 1) {
    //  throw new MappingException("incorrect column count, expected 1 but " + rsw.getColumnCount());
    //}

    int index = 1;
    TypeHandler<?> typeHandler = TypeHandlerRegistry.getTypeHandler(mappedClass, rsw.getJdbcType(index));
    Object value = typeHandler.getResult(rsw.getResultSet(), index);
    return (T) value;
  }

  @Override
  public Class<T> getMappedClass() {
    return mappedClass;
  }

}
