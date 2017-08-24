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

package jmind.pigg.crud.custom.parser.op;

/**
 * @author xieweibo
 */
public abstract class Param1Op extends AbstractOp {

  @Override
  public int paramCount() {
    return 1;
  }

  @Override
  public String render(String column, String[] params) {
    if (params == null) {
      throw new NullPointerException("params can't be null");
    }
    if (params.length != 1) {
      throw new IllegalArgumentException("length of params expected 1, but " + params.length);
    }
    return column + " " + operator() + " " + params[0];
  }

  public abstract String operator();

}
