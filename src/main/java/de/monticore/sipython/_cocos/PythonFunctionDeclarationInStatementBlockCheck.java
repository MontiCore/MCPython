package de.monticore.sipython._cocos;

import de.monticore.python._ast.*;
import de.se_rwth.commons.logging.Log;

public class PythonFunctionDeclarationInStatementBlockCheck {

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
}
