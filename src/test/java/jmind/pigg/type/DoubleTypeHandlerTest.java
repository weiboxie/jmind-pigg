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

package jmind.pigg.type;

import org.junit.Test;

import jmind.pigg.type.DoubleTypeHandler;
import jmind.pigg.type.TypeHandler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DoubleTypeHandlerTest extends BaseTypeHandlerTest {

  private static final TypeHandler<Double> TYPE_HANDLER = new DoubleTypeHandler();

  @Override
  @Test
  public void shouldSetParameter() throws Exception {
    TYPE_HANDLER.setParameter(ps, 1, 100d);
    verify(ps).setDouble(1, 100d);
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByPosition() throws Exception {
    when(rs.getDouble(1)).thenReturn(100d);
    when(rs.wasNull()).thenReturn(false);
    assertEquals(new Double(100d), TYPE_HANDLER.getResult(rs, 1));
  }

  @Override
  public void shouldGetResultNullFromResultSetByPosition() throws Exception {
    // Unnecessary
  }

}