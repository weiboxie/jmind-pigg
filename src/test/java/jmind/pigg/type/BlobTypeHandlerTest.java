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
import org.mockito.Mock;
import org.mockito.Mockito;

import jmind.pigg.type.BlobTypeHandler;
import jmind.pigg.type.TypeHandler;

import java.io.InputStream;
import java.sql.Blob;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BlobTypeHandlerTest extends BaseTypeHandlerTest {

  private static final TypeHandler<byte[]> TYPE_HANDLER = new BlobTypeHandler();

  @Mock
  protected Blob blob;

  @Override
  @Test
  public void shouldSetParameter() throws Exception {
    TYPE_HANDLER.setParameter(ps, 1, new byte[] { 1, 2, 3 });
    verify(ps).setBinaryStream(Mockito.eq(1), Mockito.any(InputStream.class), Mockito.eq(3));
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByPosition() throws Exception {
    when(rs.getBlob(1)).thenReturn(blob);
    when(rs.wasNull()).thenReturn(false);
    when(blob.length()).thenReturn(3l);
    when(blob.getBytes(1, 3)).thenReturn(new byte[] { 1, 2, 3 });
    assertArrayEquals(new byte[] { 1, 2, 3 }, TYPE_HANDLER.getResult(rs, 1));
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByPosition() throws Exception {
    when(rs.getBlob(1)).thenReturn(null);
    when(rs.wasNull()).thenReturn(true);
    assertNull(TYPE_HANDLER.getResult(rs, 1));
  }

}
