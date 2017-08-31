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

import org.junit.Test;

import jmind.pigg.binding.BoundSql;
import jmind.pigg.binding.DefaultParameterContext;
import jmind.pigg.binding.InvocationContext;
import jmind.pigg.binding.InvocationContextFactory;
import jmind.pigg.descriptor.ParameterDescriptor;
import jmind.pigg.interceptor.Interceptor;
import jmind.pigg.interceptor.InterceptorChain;
import jmind.pigg.interceptor.InvocationInterceptorChain;
import jmind.pigg.interceptor.Parameter;
import jmind.pigg.support.model4table.User;
import jmind.pigg.util.jdbc.SQLType;
import jmind.base.util.reflect.TypeToken;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author xieweibo
 */
public class InvocationInterceptorChainTest {

  @Test
  public void testIntercept() throws Exception {
    final String sql = "select * from user where id=? and name=?";
    BoundSql boundSql = new BoundSql(sql);
    boundSql.addArg(1);
    boundSql.addArg("ash");
    final User user = new User();
    user.setId(100);
    user.setName("lucy");

    InterceptorChain ic = new InterceptorChain();
    ic.addInterceptor(new Interceptor() {

      @Override
      public void preIntercept(InvocationContext context, SQLType sqlType, DataSource dataSource) {

      }

      @Override
      public void postIntercept(InvocationContext context, SQLType sqlType, Object result) {

      }
    });
    List<Annotation> empty = Collections.emptyList();
    TypeToken<User> t = new TypeToken<User>() {
    };
    ParameterDescriptor p = ParameterDescriptor.create(0, t.getType(), empty, "1");
    List<ParameterDescriptor> pds = Arrays.asList(p);
    InvocationInterceptorChain iic = new InvocationInterceptorChain(ic, pds, SQLType.SELECT);

    InvocationContextFactory f = InvocationContextFactory.create(DefaultParameterContext.create(pds));
    InvocationContext ctx = f.newInvocationContext(new Object[]{user});

  }

}
