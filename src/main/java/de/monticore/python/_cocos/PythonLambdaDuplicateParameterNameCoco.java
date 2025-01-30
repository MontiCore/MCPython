package de.monticore.python._cocos;

import de.monticore.python._ast.ASTFunctionParameter;
import de.monticore.python._ast.ASTLambdaExpression;
import de.monticore.python._util.PythonTypeDispatcher;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Set;

public class PythonLambdaDuplicateParameterNameCoco implements PythonASTLambdaExpressionCoCo {

	@Override
	public void check(ASTLambdaExpression node) {
		Set<String> names = new HashSet<>();

		PythonTypeDispatcher td = new PythonTypeDispatcher();

		for (ASTFunctionParameter parameter : node.getFunctionParameters().getFunctionParameterList()) {
			if(td.isBasicSymbolsASTVariable(parameter)) {
				String name = td.asBasicSymbolsASTVariable(parameter).getName();
				if (names.contains(name)) {
					Log.error("Duplicate parameter name '" + name + "' in lambda function " + node.get_SourcePositionStart());
				} else {
					names.add(name);
				}
			}
		}

	}
}
