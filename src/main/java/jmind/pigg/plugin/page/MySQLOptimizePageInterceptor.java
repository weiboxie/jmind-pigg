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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 优化分页count
 * @author xieweibo
 */
public class MySQLOptimizePageInterceptor extends MySQLPageInterceptor {


  private final Logger logger= LoggerFactory.getLogger(getClass());


  @Override
  public void handleTotal(Page page,BoundSql boundSql) {
    String sql = boundSql.getSql();
    boundSql.setSql(getCountSql(page,sql.trim()));
  }



  final Pattern p = Pattern.compile("(^select )(.*?)( from .*)", Pattern.CASE_INSENSITIVE);

  private   String getCountSql(Page page,String sql) {
    //   String regex="(^select)([\\S\\s]+)(from[\\s\\S]+)" ;
    if(page.isOptimizedCount()){
      String lowerSql=sql.toLowerCase();
      //   不包含 distinct  和 group by
      if(!lowerSql.contains("distinct") && !lowerSql.contains("group by")){
        Matcher m = p.matcher(sql);
        boolean rs = m.find();
        if (rs&&m.groupCount()==3){
          String s=  "select count(*) " + m.group(3);
          logger.info("sql count="+sql);
          return s;
        }
      }
    }
    return "SELECT COUNT(*) FROM (" + sql + ") aliasForPage";

  }


}
