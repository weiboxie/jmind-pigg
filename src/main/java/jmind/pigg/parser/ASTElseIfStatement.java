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

import jmind.pigg.binding.InvocationContext;
import jmind.pigg.parser.generate.Parser;
import jmind.pigg.parser.generate.ParserVisitor;

public class ASTElseIfStatement extends AbstractRenderableNode {

  public ASTElseIfStatement(int id) {
    super(id);
  }

  public ASTElseIfStatement(Parser p, int id) {
    super(p, id);
  }

  @Override
  public boolean render(InvocationContext context) {

    /**
     * 检测#elseif(expression)是否返回true
     */
    AbstractExpression expr = (AbstractExpression) jjtGetChild(0);
    if (expr.evaluate(context)) {
      ((AbstractRenderableNode) jjtGetChild(1)).render(context);
      return true;
    }
    return false;
  }

  @Override
  public Object jjtAccept(ParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

}