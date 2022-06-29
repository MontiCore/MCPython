package de.monticore.sipython._cocos;

import de.monticore.python._ast.ASTForStatement;
import de.monticore.python._ast.ASTIfStatement;
import de.monticore.python._cocos.PythonASTForStatementCoCo;
import de.monticore.python._cocos.PythonASTIfStatementCoCo;

public class PythonFunctionDeclarationInForStatementCoco extends PythonFunctionDeclarationInStatementBlockCheck implements PythonASTForStatementCoCo {

	public PythonFunctionDeclarationInForStatementCoco() {
		super();
	}

	@Override
	public void check(ASTForStatement node) {
		this.checkStatementBlock(node.getStatementBlock());
	}

}
