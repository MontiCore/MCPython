package de.monticore.python._cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisASTNameExpressionCoCo;
import de.monticore.python._symboltable.PythonScope;
import de.se_rwth.commons.logging.Log;

public class PythonVariableOrFunctionOrClassExistsCoco implements ExpressionsBasisASTNameExpressionCoCo {

	@Override
	public void check(ASTNameExpression node) {
		if (((PythonScope) node.getEnclosingScope()).resolveVariable(node.getName()).isEmpty()
				&& ((PythonScope) node.getEnclosingScope()).resolveFunction(node.getName()).isEmpty()
				&& ((PythonScope) node.getEnclosingScope()).resolvePythonClass(node.getName()).isEmpty()) {
			Log.error("Variable, Function or Class " + node.getName() + " does not exist " + node.get_SourcePositionStart());
		}
	}

}
