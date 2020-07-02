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



import jmind.base.util.DataUtil;
import jmind.pigg.binding.BoundSql;
import jmind.pigg.binding.InvocationContext;
import jmind.pigg.interceptor.Parameter;
import jmind.pigg.util.jdbc.SQLType;

import java.util.List;


/**
 * @author xieweibo
 */
public class MySQLPageInterceptor extends AbstractPageInterceptor {

  @Override
  public void handleTotal(Page page,BoundSql boundSql) {
    String sql = boundSql.getSql();
    sql = "SELECT COUNT(*) FROM (" + sql + ") aliasForPage";
    boundSql.setSql(sql);
  }

  @Override
  public void handlePage(Page page, InvocationContext context) {
    int startRow = (page.getPage() - 1) * page.getPageSize();

    if(DataUtil.isNotEmpty(page.getOrderBy())){
      context.writeToSqlBuffer(" order by "+page.getOrderBy()) ;
    }

    context.writeToSqlBuffer(" limit ?, ?");
    context.appendToArgs(startRow);
    context.appendToArgs(page.getPageSize());
  }



}
