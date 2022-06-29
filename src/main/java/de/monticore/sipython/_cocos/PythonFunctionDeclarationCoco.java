package de.monticore.sipython._cocos;

import de.monticore.python._ast.ASTFunctionDeclaration;
import de.monticore.python._cocos.PythonASTFunctionDeclarationCoCo;

public class PythonFunctionDeclarationCoco implements PythonASTFunctionDeclarationCoCo {

	public PythonFunctionDeclarationCoco() {
		super();
	}

	@Override
	public void check(ASTFunctionDeclaration node) {
		// TODO function not in loop, if, function ...
	}
}
