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


import jmind.pigg.binding.BoundSql;
import jmind.pigg.util.Strings;

/**
 * @author xieweibo
 */
public class OraclePageInterceptor extends AbstractPageInterceptor {

  @Override
  void handleTotal(BoundSql boundSql) {
    String sql = boundSql.getSql();
    sql = "SELECT COUNT(1) FROM (" + sql + ") aliasForPage";
    boundSql.setSql(sql);
  }

  @Override
  void handlePage(Page page, BoundSql boundSql) {
    int startRow = (page.getPageNum() - 1) * page.getPageSize();
    int endRow = page.getPageNum() * page.getPageSize();
    String sql = boundSql.getSql();
    if(Strings.isNotEmpty(page.getGroupBy())){
        sql+=" group by "+page.getGroupBy();
    }
    if(Strings.isNotEmpty(page.getOderBy())){
        sql+=" order by "+page.getOderBy() ;
    }
    sql = "SELECT * FROM ( SELECT B.* , ROWNUM RN FROM (" + sql + ") B WHERE ROWNUM <= "
        + endRow + " ) WHERE RN > " + startRow;
    boundSql.setSql(sql);
  }

}
