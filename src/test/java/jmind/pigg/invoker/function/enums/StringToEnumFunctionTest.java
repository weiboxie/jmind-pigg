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

package jmind.pigg.invoker.function.enums;

import org.junit.Test;

import jmind.pigg.annotation.Setter;
import jmind.pigg.invoker.FunctionalSetterInvoker;
import jmind.pigg.invoker.SetterInvoker;
import jmind.pigg.invoker.function.enums.StringToEnumFunction;

import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author xieweibo
 */
public class StringToEnumFunctionTest {

  @Test
  public void testApply() throws Exception {
    A a = new A();
    Method m = A.class.getDeclaredMethod("setE", E.class);
    SetterInvoker invoker = FunctionalSetterInvoker.create("e", m);
    invoker.invoke(a, "Y");
    assertThat(a.getE(), is(E.Y));
  }

  static class A {
    private E e;

    E getE() {
      return e;
    }

    @Setter(StringToEnumFunction.class)
    void setE(E e) {
      this.e = e;
    }
  }

  enum E {
    X, Y, Z;
  }

}
