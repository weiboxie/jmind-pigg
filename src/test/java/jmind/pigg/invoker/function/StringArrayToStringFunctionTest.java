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

package jmind.pigg.invoker.function;

import org.junit.Test;

import jmind.pigg.annotation.Getter;
import jmind.pigg.invoker.FunctionalGetterInvoker;
import jmind.pigg.invoker.GetterInvoker;
import jmind.pigg.invoker.function.StringArrayToStringFunction;

import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author xieweibo
 */
public class StringArrayToStringFunctionTest {

  @Test
  public void testApply() throws Exception {
    A a = new A();
    Method m = A.class.getDeclaredMethod("getX");
    GetterInvoker invoker = FunctionalGetterInvoker.create("x", m);

    a.setX(new String[]{"1", "2", "3"});
    assertThat((String) invoker.invoke(a), is("1,2,3"));

    a.setX(null);
    assertThat(invoker.invoke(a), nullValue());

    a.setX(new String[]{});
    assertThat((String) invoker.invoke(a), is(""));
  }

  static class A {

    private String[] x;

    @Getter(StringArrayToStringFunction.class)
    String[] getX() {
      return x;
    }

    void setX(String[] x) {
      this.x = x;
    }
  }

}
