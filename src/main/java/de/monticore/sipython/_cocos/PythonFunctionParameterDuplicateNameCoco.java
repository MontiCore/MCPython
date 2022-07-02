package de.monticore.sipython._cocos;

import de.monticore.python._ast.ASTFunctionDeclaration;
import de.monticore.python._ast.ASTFunctionParameter;
import de.monticore.python._cocos.PythonASTFunctionDeclarationCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Set;

public class PythonFunctionParameterDuplicateNameCoco implements PythonASTFunctionDeclarationCoCo {

	@Override
	public void check(ASTFunctionDeclaration node) {
		Set<String> names = new HashSet<>();
		for (ASTFunctionParameter parameter : node.getFunctionParameters().getFunctionParameterList()) {
			if (names.contains(parameter.getName())) {
				Log.error("Duplicate parameter name '" + parameter.getName() + "' in function '" + node.getName() +
						"' " + node.get_SourcePositionStart());
			} else {
				names.add(parameter.getName());
			}
		}
	}
}
