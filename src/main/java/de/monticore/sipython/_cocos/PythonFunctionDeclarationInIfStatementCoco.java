package de.monticore.sipython._cocos;

import de.monticore.python._ast.ASTFunctionDeclaration;
import de.monticore.python._ast.ASTIfStatement;
import de.monticore.python._cocos.PythonASTFunctionDeclarationCoCo;
import de.monticore.python._cocos.PythonASTIfStatementCoCo;

public class PythonFunctionDeclarationInIfStatementCoco extends PythonFunctionDeclarationInStatementBlockCheck implements PythonASTIfStatementCoCo {

	public PythonFunctionDeclarationInIfStatementCoco() {
		super();
	}

	@Override
	public void check(ASTIfStatement node) {
		this.checkStatementBlock(node.getThenStatement());
		if (node.isPresentElseStatement()) {
			this.checkStatementBlock(node.getElseStatement());
		}
	}

}
