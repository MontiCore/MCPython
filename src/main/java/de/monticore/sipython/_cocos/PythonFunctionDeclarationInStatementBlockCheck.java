package de.monticore.sipython._cocos;

import de.monticore.python._ast.*;
import de.monticore.python._cocos.PythonASTForStatementCoCo;
import de.monticore.python._cocos.PythonASTFunctionDeclarationCoCo;
import de.monticore.python._cocos.PythonASTIfStatementCoCo;
import de.monticore.python._cocos.PythonASTWhileStatementCoCo;
import de.se_rwth.commons.logging.Log;

public class PythonFunctionDeclarationInStatementBlockCheck implements PythonASTIfStatementCoCo,
		PythonASTWhileStatementCoCo, PythonASTForStatementCoCo {

	protected void checkStatementBlock(ASTStatementBlock node) {
		for (ASTStatement statement : node.getStatementBlockBody().getStatementList()) {
			if (statement instanceof ASTFunctionDeclaration) {
				Log.error("Function declarations are not allowed in statement block at " + statement.get_SourcePositionStart());
			}
		}
	}

	@Override
	public void check(ASTIfStatement node) {
		this.checkStatementBlock(node.getThenStatement());

		for(ASTStatementBlock statementBlock: node.getElifStatementList()){
			this.checkStatementBlock(statementBlock);
		}

		if (node.isPresentElseStatement()) {
			this.checkStatementBlock(node.getElseStatement());
		}
	}

	@Override
	public void check(ASTWhileStatement node) {
		this.checkStatementBlock(node.getStatementBlock());
	}

	@Override
	public void check(ASTForStatement node) {
		this.checkStatementBlock(node.getStatementBlock());
	}
}
