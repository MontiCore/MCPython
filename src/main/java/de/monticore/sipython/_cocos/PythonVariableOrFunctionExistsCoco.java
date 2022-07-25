package de.monticore.sipython._cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisASTNameExpressionCoCo;
import de.monticore.sipython._symboltable.SIPythonScope;
import de.se_rwth.commons.logging.Log;

public class PythonVariableOrFunctionExistsCoco implements ExpressionsBasisASTNameExpressionCoCo {

	@Override
	public void check(ASTNameExpression node) {
		if (((SIPythonScope) node.getEnclosingScope()).resolveVariable(node.getName()).isEmpty()
				&& ((SIPythonScope) node.getEnclosingScope()).resolveFunction(node.getName()).isEmpty()
				&& ((SIPythonScope) node.getEnclosingScope()).resolveClass(node.getName()).isEmpty()) {
			Log.error("Variable, Function or Class" + node.getName() + " does not exist " + node.get_SourcePositionStart());
		}
	}

}
