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

package jmind.pigg.interceptor;

import javax.sql.DataSource;

import jmind.pigg.binding.BoundSql;
import jmind.pigg.binding.InvocationContext;
import jmind.pigg.util.jdbc.SQLType;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xieweibo
 */
public class InterceptorChain {

  private final List<Interceptor> interceptors=new LinkedList<>();

  public void addInterceptor(Interceptor interceptor) {
    interceptors.add(interceptor);
  }

  public void preIntercept(InvocationContext context, SQLType sqlType, DataSource dataSource) {
      for (Interceptor interceptor : interceptors) {
        interceptor.preIntercept(context, sqlType, dataSource);
    }
  }

// 后置拦截器应该导过来循环
  public void postIntercept(InvocationContext context, SQLType sqlType, Object result) {
    for (int i=interceptors.size()-1;i>=0;i--) {
      interceptors.get(i).postIntercept(context, sqlType, result);
    }
  }






}
