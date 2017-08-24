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

package jmind.pigg.parser.visitor;


import jmind.pigg.parser.*;
import jmind.pigg.parser.generate.ParserVisitor;
import jmind.pigg.parser.generate.SimpleNode;

/**
 * in属性收集
 *
 * @author xieweibo
 */
public enum InterablePropertyCollectVisitor implements ParserVisitor {

  INSTANCE;

  @Override
  public Object visit(SimpleNode node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTRootNode node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTInsert node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTDelete node, Object data) {
    throw new UnsupportedOperationException();

  }

  @Override
  public Object visit(ASTUpdate node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTSelect node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTReplace node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTMerge node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTTruncate node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTBlock father, Object data) {
    int num = father.jjtGetNumChildren();
    for (int i = 0; i < num - 2; i++) {
      if (father.jjtGetChild(i) instanceof ASTText
          && father.jjtGetChild(i + 1) instanceof ASTBlank
          && father.jjtGetChild(i + 2) instanceof ASTJDBCIterableParameter) {
        String p = ((ASTText) father.jjtGetChild(i)).getValue();
        ((ASTJDBCIterableParameter) father.jjtGetChild(i + 2)).setPropertyOfMapper(p);
      }
    }
    return father.childrenAccept(this, data);
  }

  @Override
  public Object visit(ASTJDBCParameter node, Object data) {
    return data;
  }

  @Override
  public Object visit(ASTJDBCIterableParameter node, Object data) {
    return data;
  }

  @Override
  public Object visit(ASTGlobalTable node, Object data) {
    return data;
  }

  @Override
  public Object visit(ASTJoinParameter node, Object data) {
    return data;
  }

  @Override
  public Object visit(ASTQuoteText node, Object data) {
    return data;
  }

  @Override
  public Object visit(ASTText node, Object data) {
    return data;
  }

  @Override
  public Object visit(ASTBlank node, Object data) {
    return data;
  }

  @Override
  public Object visit(ASTIfStatement node, Object data) {
    return node.childrenAccept(this, data);
  }

  @Override
  public Object visit(ASTElseStatement node, Object data) {
    return node.childrenAccept(this, data);
  }

  @Override
  public Object visit(ASTElseIfStatement node, Object data) {
    return node.childrenAccept(this, data);
  }

  @Override
  public Object visit(ASTExpression node, Object data) {
    return data;
  }

  @Override
  public Object visit(ASTOrNode node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTAndNode node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTLTNode node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTGTNode node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTLENode node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTGENode node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTEQNode node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTNENode node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTNotNode node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTExpressionParameter node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTIntegerLiteral node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTStringLiteral node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTTrue node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTFalse node, Object data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ASTNull node, Object data) {
    throw new UnsupportedOperationException();
  }

}
