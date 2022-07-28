package de.monticore.python._cocos;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.python._ast.ASTExpressionStatement;
import de.monticore.python._cocos.PythonASTExpressionStatementCoCo;
import de.se_rwth.commons.logging.Log;

public class PythonExpressionCoco implements PythonASTExpressionStatementCoCo {

    @Override
    public void check(ASTExpressionStatement node) {


        if(!(node.getExpression() instanceof ASTCallExpression) && !(node.getExpression() instanceof ASTAssignmentExpression)){
            Log.warn("Statement seems to have no effect " + node.get_SourcePositionStart());
        }

        if (node.getExpression() instanceof ASTAssignmentExpression && ((ASTAssignmentExpression) node.getExpression()).getLeft() instanceof ASTLiteralExpression) {
            if (((ASTAssignmentExpression) node.getExpression()).getOperator() == 2) {
                Log.error("Invalid variable declaration " + node.get_SourcePositionStart());
            }
        }
    }
}
