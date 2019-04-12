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
import jmind.pigg.binding.InvocationContext;
import jmind.pigg.interceptor.QueryInterceptor;
import jmind.pigg.jdbc.JdbcOperationsFactory;
import jmind.pigg.mapper.SingleColumnRowMapper;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author xieweibo
 */
public abstract class AbstractPageInterceptor extends QueryInterceptor {

    @Override
    public void interceptQuery(InvocationContext context, DataSource dataSource) {
        List<Object> values = context.getParameterValues();
        for(int i=values.size()-1;i>=0;i--){ // 一般把最后一个参数放Page，倒叙循环，提高性能
            if(values.get(i) instanceof Page){
                Page page = (Page) values.get(i);
                // 参数检测
                int pageNum = page.getPage();
                int pageSize = page.getPageSize();
                if (pageNum <= 0) {
                    throw new PageException("pageNum need > 0, but pageNum is " + pageNum);
                }
                if (pageSize <= 0) {
                    throw new PageException("pageSize need > 0, but pageSize is " + pageSize);
                }

                // 获取总数
                if (page.isFetchTotal()) {
                    BoundSql totalBoundSql = context.getBoundSql();
                    handleTotal(totalBoundSql);
                    SingleColumnRowMapper<Long> mapper = new SingleColumnRowMapper<Long>(long.class);
                    long total = JdbcOperationsFactory.getJdbcOperations().queryForObject(dataSource, totalBoundSql, mapper);
                    page.setTotalNum(total);
                }

                // 分页处理
                handlePage(page, context);
                return;
            }
        }

    }

    @Override
    public  void interceptResult(InvocationContext context, Object result){
        List<Object> values = context.getParameterValues();
        for(int i=values.size()-1;i>=0;i--){
             if(values.get(i) instanceof Page){
                 Page page = (Page) values.get(i);
                 page.setResult(result);
                    return;
             }
         }
    }

    abstract void handleTotal(BoundSql boundSql);

    abstract void handlePage(Page page, InvocationContext context);

}
