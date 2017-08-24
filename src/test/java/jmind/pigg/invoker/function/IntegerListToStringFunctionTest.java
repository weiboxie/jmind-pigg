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

import com.google.common.collect.Lists;

import jmind.pigg.annotation.Getter;
import jmind.pigg.invoker.FunctionalGetterInvoker;
import jmind.pigg.invoker.GetterInvoker;
import jmind.pigg.invoker.function.IntegerListToStringFunction;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author xieweibo
 */
public class IntegerListToStringFunctionTest {

  @Test
  public void testApply() throws Exception {
    A a = new A();
    Method m = A.class.getDeclaredMethod("getX");
    GetterInvoker invoker = FunctionalGetterInvoker.create("x", m);

    a.setX(Lists.newArrayList(1, 2, 3));
    assertThat((String) invoker.invoke(a), is("1,2,3"));

    a.setX(null);
    assertThat(invoker.invoke(a), nullValue());

    a.setX(new ArrayList<Integer>());
    assertThat((String) invoker.invoke(a), is(""));
  }

  static class A {
    private List<Integer> x;

    @Getter(IntegerListToStringFunction.class)
    List<Integer> getX() {
      return x;
    }

    void setX(List<Integer> x) {
      this.x = x;
    }
  }

}
