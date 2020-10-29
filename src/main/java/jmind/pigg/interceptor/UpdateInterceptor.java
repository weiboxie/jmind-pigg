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
import jmind.pigg.descriptor.MethodDescriptor;
import jmind.pigg.util.jdbc.SQLType;

import java.util.List;

/**
 * @author xieweibo
 */
public abstract class UpdateInterceptor  implements Interceptor {



  public abstract void interceptUpdate(InvocationContext context,MethodDescriptor md, DataSource dataSource);

  public abstract void interceptResult(InvocationContext context,MethodDescriptor md, DataSource dataSource,Object result);

  @Override
  public void preIntercept(InvocationContext context, SQLType sqlType, MethodDescriptor md, DataSource dataSource) {
        if(sqlType.needChangeData()) {
            interceptUpdate(context,md, dataSource);
        }
  }

  @Override
  public void postIntercept(InvocationContext context, SQLType sqlType,MethodDescriptor md, DataSource dataSource, Object result) {
       if(sqlType.needChangeData()){
           interceptResult(context, md, dataSource,result);
       }
  }
}
