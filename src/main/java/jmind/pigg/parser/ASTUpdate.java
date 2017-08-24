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

package jmind.pigg.parser;

import jmind.pigg.parser.generate.Parser;
import jmind.pigg.parser.generate.ParserVisitor;
import jmind.pigg.util.jdbc.SQLType;

public class ASTUpdate extends AbstractDMLNode {

  public ASTUpdate(int id) {
    super(id);
  }

  public ASTUpdate(Parser p, int id) {
    super(p, id);
  }

  @Override
  public SQLType getSQLType() {
    return SQLType.UPDATE;
  }

  @Override
  public Object jjtAccept(ParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

}