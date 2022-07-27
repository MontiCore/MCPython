package de.monticore.python._cocos;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.python._ast.ASTExpressionStatement;
import de.monticore.python._cocos.PythonASTExpressionStatementCoCo;
import de.se_rwth.commons.logging.Log;

public class PythonExpressionCoco implements PythonASTExpressionStatementCoCo {

    @Override
    public void check(ASTExpressionStatement node){


        if(!(node.getExpression() instanceof ASTCallExpression) && !(node.getExpression() instanceof ASTAssignmentExpression)){
            Log.error("Statement can not be used like that " + node.get_SourcePositionStart());
        }
    }
}
