package de.monticore.sipython._cocos;

import de.monticore.python._ast.ASTForStatement;
import de.monticore.python._ast.ASTWhileStatement;
import de.monticore.python._cocos.PythonASTForStatementCoCo;
import de.monticore.python._cocos.PythonASTWhileStatementCoCo;

public class PythonFunctionDeclarationInWhileStatementCoco extends PythonFunctionDeclarationInStatementBlockCheck implements PythonASTWhileStatementCoCo {

	public PythonFunctionDeclarationInWhileStatementCoco() {
		super();
	}

	@Override
	public void check(ASTWhileStatement node) {
		this.checkStatementBlock(node.getStatementBlock());
	}

}
