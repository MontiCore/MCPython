package de.monticore.sipython._cocos;

import de.monticore.python._ast.*;
import de.monticore.python._cocos.PythonASTForStatementCoCo;
import de.monticore.python._cocos.PythonASTFunctionDeclarationCoCo;
import de.monticore.python._cocos.PythonASTIfStatementCoCo;
import de.monticore.python._cocos.PythonASTWhileStatementCoCo;
import de.se_rwth.commons.logging.Log;

public class PythonFunctionDeclarationInStatementBlockCheck implements PythonASTIfStatementCoCo,
		PythonASTFunctionDeclarationCoCo, PythonASTWhileStatementCoCo, PythonASTForStatementCoCo {

	protected void checkStatementBlock(ASTStatementBlock node) {
		for (ASTStatement statement : node.getStatementBlockBody().getStatementList()) {
			if (statement instanceof ASTFunctionDeclaration) {
				Log.error("Function declarations are not allowed in statement block at " + statement.get_SourcePositionStart());
			} else if (statement instanceof ASTForStatement) {
				checkStatementBlock(((ASTForStatement) statement).getStatementBlock());
			} else if (statement instanceof ASTWhileStatement) {
				checkStatementBlock(((ASTWhileStatement) statement).getStatementBlock());
			} else if (statement instanceof ASTIfStatement) {
				checkStatementBlock(((ASTIfStatement) statement).getElseStatement());
				if (((ASTIfStatement) statement).isPresentElseStatement()) {
					checkStatementBlock(((ASTIfStatement) statement).getElseStatement());
				}
			}
		}
	}

	@Override
	public void check(ASTIfStatement node) {
		this.checkStatementBlock(node.getThenStatement());
		if (node.isPresentElseStatement()) {
			this.checkStatementBlock(node.getElseStatement());
		}
	}

	@Override
	public void check(ASTFunctionDeclaration node) {
		this.checkStatementBlock(node.getStatementBlock());
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
