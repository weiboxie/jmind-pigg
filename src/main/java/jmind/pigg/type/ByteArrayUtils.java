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

/**
 * @author Clinton Begin
 * @author xieweibo
 */
class ByteArrayUtils {

  private ByteArrayUtils() {
    // Prevent Instantiation
  }

  static byte[] convertToPrimitiveArray(Byte[] objects) {
    final byte[] bytes = new byte[objects.length];
    for (int i = 0; i < objects.length; i++) {
      bytes[i] = objects[i];
    }
    return bytes;
  }

  static Byte[] convertToObjectArray(byte[] bytes) {
    final Byte[] objects = new Byte[bytes.length];
    for (int i = 0; i < bytes.length; i++) {
      objects[i] = bytes[i];
    }
    return objects;
  }
}
