package de.monticore.python._cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisASTNameExpressionCoCo;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.python._symboltable.IPythonScope;
import de.se_rwth.commons.logging.Log;

public class PythonVariableOrFunctionOrClassExistsCoco implements ExpressionsBasisASTNameExpressionCoCo {

	@Override
	public void check(ASTNameExpression node) {
    IExpressionsBasisScope es = node.getEnclosingScope();
    if(!(es instanceof IPythonScope)){
      return;
    }

    IPythonScope s = (IPythonScope) es;

    if (s.resolveVariable(node.getName()).isEmpty()
				&& s.resolveFunction(node.getName()).isEmpty()
				&& s.resolvePythonClass(node.getName()).isEmpty()) {
			Log.error("Variable, Function or Class " + node.getName() + " does not exist " + node.get_SourcePositionStart());
		}
	}

}
