package de.monticore.python._cocos;

import de.monticore.python._ast.ASTFunctionParameter;
import de.monticore.python._ast.ASTLambdaExpression;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Set;

public class PythonLambdaDuplicateParameterNameCoco implements PythonASTLambdaExpressionCoCo {

	@Override
	public void check(ASTLambdaExpression node) {
		Set<String> names = new HashSet<>();

		for (ASTFunctionParameter parameter : node.getFunctionParameters().getFunctionParameterList()) {
			if (names.contains(parameter.getName())) {
				Log.error("Duplicate parameter name '" + parameter.getName() + "' in lambda function " + node.get_SourcePositionStart());
			} else {
				names.add(parameter.getName());
			}
		}

	}
}
