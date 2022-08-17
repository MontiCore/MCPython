package de.monticore.python._cocos;

import de.monticore.python._ast.*;
import de.se_rwth.commons.logging.Log;

public class PythonFunctionOrClassDeclarationInStatementBlockCoco implements PythonASTIfStatementCoCo,
		PythonASTWhileStatementCoCo, PythonASTForStatementCoCo {

	protected void checkStatementBlock(ASTStatementBlock node) {
		for (ASTStatement statement : node.getStatementBlockBody().getStatementList()) {
			if (statement instanceof ASTFunctionDeclaration || statement instanceof ASTClassDeclaration) {
				Log.error("Function/Class declarations are not allowed in statement block at " +
						statement.get_SourcePositionStart());
			}
		}
	}

	@Override
	public void check(ASTIfStatement node) {
		this.checkStatementBlock(node.getThenStatement());

		for(ASTStatementBlock statementBlock: node.getElifStatementList()){
			this.checkStatementBlock(statementBlock);
		}

		if (node.isPresentElseStatementPart()) {
			this.checkStatementBlock(node.getElseStatementPart().getStatementBlock());
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
