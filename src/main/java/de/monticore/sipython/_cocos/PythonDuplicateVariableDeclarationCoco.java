package de.monticore.sipython._cocos;

import de.monticore.python._ast.ASTVariableDeclaration;
import de.monticore.python._cocos.PythonASTVariableDeclarationCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Set;

public class PythonDuplicateVariableDeclarationCoco implements PythonASTVariableDeclarationCoCo {

	private final Set<String> variableNames = new HashSet<>();

	@Override
	public void check(ASTVariableDeclaration node) {
		if (variableNames.contains(node.getName())) {
			Log.error("Duplicate variable name " + node.get_SourcePositionStart());
		} else {
			variableNames.add(node.getName());
		}
	}
}
