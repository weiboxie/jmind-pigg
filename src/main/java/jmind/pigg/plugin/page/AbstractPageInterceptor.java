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

package jmind.pigg.plugin.page;

import javax.sql.DataSource;

import jmind.pigg.binding.BoundSql;
import jmind.pigg.interceptor.Parameter;
import jmind.pigg.interceptor.QueryInterceptor;
import jmind.pigg.mapper.SingleColumnRowMapper;

import java.util.List;

/**
 * @author xieweibo
 */
public abstract class AbstractPageInterceptor extends QueryInterceptor {

  @Override
  public void interceptQuery(BoundSql boundSql, List<Parameter> parameters, DataSource dataSource) {
    for (Parameter parameter : parameters) {
      Object val = parameter.getValue();
      if (val instanceof Page) {
        Page page = (Page) val;

        // 参数检测
        int pageNum = page.getPageNum();
        int pageSize = page.getPageSize();
        if (pageNum <= 0) {
          throw new PageException("pageNum need > 0, but pageNum is " + pageNum);
        }
        if (pageSize <= 0) {
          throw new PageException("pageSize need > 0, but pageSize is " + pageSize);
        }

        // 获取总数
        if (page.isFetchTotal()) {
          BoundSql totalBoundSql = boundSql.copy();
          handleTotal(totalBoundSql);
          SingleColumnRowMapper<Integer> mapper = new SingleColumnRowMapper<Integer>(int.class);
          int total = getJdbcOperations().queryForObject(dataSource, totalBoundSql, mapper);
          page.setTotal(total);
        }

        // 分页处理
        handlePage(page, boundSql);
      }
    }
  }

  abstract void handleTotal(BoundSql boundSql);

  abstract void handlePage(Page page, BoundSql boundSql);

}
