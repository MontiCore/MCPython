package de.monticore.python._cocos;

import de.monticore.expressions.assignmentexpressions._ast.ASTDecPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecSuffixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncSuffixExpression;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTDecPrefixExpressionCoCo;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTDecSuffixExpressionCoCo;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTIncPrefixExpressionCoCo;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTIncSuffixExpressionCoCo;
import de.se_rwth.commons.logging.Log;

public class NoInvalidExpressionUsedCoCo implements AssignmentExpressionsASTDecPrefixExpressionCoCo, AssignmentExpressionsASTIncPrefixExpressionCoCo, AssignmentExpressionsASTDecSuffixExpressionCoCo, AssignmentExpressionsASTIncSuffixExpressionCoCo {
  @Override
  public void check(ASTDecPrefixExpression node) {
    Log.error("'--i' style expressions are not valid in python", node.get_SourcePositionStart(), node.get_SourcePositionEnd());
  }

  @Override
  public void check(ASTDecSuffixExpression node) {
    Log.error("'i--' style expressions are not valid in python", node.get_SourcePositionStart(), node.get_SourcePositionEnd());
  }

  @Override
  public void check(ASTIncPrefixExpression node) {
    Log.error("'++i' style expressions are not valid in python", node.get_SourcePositionStart(), node.get_SourcePositionEnd());
  }

  @Override
  public void check(ASTIncSuffixExpression node) {
    Log.error("'i++' style expressions are not valid in python", node.get_SourcePositionStart(), node.get_SourcePositionEnd());
  }
}
