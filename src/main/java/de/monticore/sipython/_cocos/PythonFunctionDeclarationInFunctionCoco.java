package de.monticore.sipython._cocos;

import de.monticore.python._ast.*;
import de.monticore.python._cocos.PythonASTFunctionDeclarationCoCo;

public class PythonFunctionDeclarationInFunctionCoco extends PythonFunctionDeclarationInStatementBlockCheck implements PythonASTFunctionDeclarationCoCo {

	public PythonFunctionDeclarationInFunctionCoco() {
		super();
	}

	@Override
	public void check(ASTFunctionDeclaration node) {
		this.checkStatementBlock(node.getStatementBlock());
	}

}
