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

public class ASTEQNode extends AbstractExpression {

  public ASTEQNode(int id) {
    super(id);
  }

  public ASTEQNode(Parser p, int id) {
    super(p, id);
  }

  @Override
  public boolean evaluate(InvocationContext context) {
    Object left = ((AbstractExpression) jjtGetChild(0)).value(context);
    Object right = ((AbstractExpression) jjtGetChild(1)).value(context);
    if (left instanceof Number && right instanceof Number) {
      return MathUtils.compare((Number) left, (Number) right) == 0;
    }

    // 都不为null，一个是另一个的子类，则使用equals操作
    if (left != null && right != null &&
        (left.getClass().isAssignableFrom(right.getClass()) ||
            right.getClass().isAssignableFrom(left.getClass()))) {
      return left.equals(right);
    }

    if (left == null && right == null) { // 都为null
      return true;
    } else if (left == null || right == null) { // 一个为null，一个不为null
      return false;
    } else { // 都不为null
      left = left.toString();
      right = right.toString();
      return left.equals(right);
    }
  }

  @Override
  public Object value(InvocationContext context) {
    return evaluate(context) ? Boolean.TRUE : Boolean.FALSE;
  }

  @Override
  public Object jjtAccept(ParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

}