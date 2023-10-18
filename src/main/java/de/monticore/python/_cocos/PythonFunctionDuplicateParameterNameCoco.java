package de.monticore.python._cocos;

import de.monticore.python._ast.ASTClassFunctionDeclaration;
import de.monticore.python._ast.ASTFunctionDeclaration;
import de.monticore.python._ast.ASTFunctionParameter;
import de.monticore.python._ast.ASTSimpleFunctionDeclaration;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PythonFunctionDuplicateParameterNameCoco implements PythonASTFunctionDeclarationCoCo {

	@Override
	public void check(ASTFunctionDeclaration node) {
		Set<String> names = new HashSet<>();

		List<ASTFunctionParameter> parameters = new ArrayList<>();

		if (node instanceof ASTSimpleFunctionDeclaration) {
			parameters.addAll(((ASTSimpleFunctionDeclaration) node).getFunctionParameters().getFunctionParameterList());
		} else if (node instanceof ASTClassFunctionDeclaration) {
			parameters.addAll(((ASTClassFunctionDeclaration) node).getClassFunctionParameters().getFunctionParameterList());
		}

		for (ASTFunctionParameter parameter : parameters) {
			if (names.contains(parameter.getName())) {
				Log.error("Duplicate parameter name '" + parameter.getName() + "' in function '" + node.getName() +
						"' " + node.get_SourcePositionStart());
			} else {
				names.add(parameter.getName());
			}
		}
	}
}
