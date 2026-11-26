package de.monticore.python._cocos;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.python._ast.ASTExpressionStatement;
import de.monticore.pythonbasis._ast.ASTConstantsPythonBasis;
import de.se_rwth.commons.logging.Log;

public class PythonExpressionCoco implements PythonASTExpressionStatementCoCo {

    @Override
    public void check(ASTExpressionStatement node) {
      for(ASTExpression expression: node.getExpressionList()) {
        if (!(expression instanceof ASTCallExpression)
            && !(expression instanceof ASTAssignmentExpression)) {
          Log.warn("Statement seems to have no effect " + node.get_SourcePositionStart());
        }

        if (expression instanceof ASTAssignmentExpression
            && ((ASTAssignmentExpression) expression).getLeft() instanceof ASTLiteralExpression) {
          // PythonBasis overrides AssignmentExpression, so the operator is a PythonBasis constant
          if (((ASTAssignmentExpression) expression).getOperator() == ASTConstantsPythonBasis.EQUALS) {
            Log.error("Invalid variable declaration " + node.get_SourcePositionStart());
          }
        }
      }
    }
}
