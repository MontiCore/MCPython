package de.monticore.python._cocos;

import de.monticore.python._ast.*;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PythonDuplicateFunctionCoco implements PythonASTPythonScriptCoCo, PythonASTStatementBlockCoCo, PythonASTClassStatementBlockCoCo {

	@Override
	public void check(ASTPythonScript node) {
		this.check(node.getStatementList());
	}

	@Override
	public void check(ASTStatementBlock node) {
		this.check(node.getStatementBlockBody().getStatementList());
	}

	@Override
	public void check(ASTClassStatementBlock node) {
		this.check(node.getClassStatementBlockBody().getClassStatementList());
	}

	private void check(List<? extends ASTPythonNode> statements) {
		Set<String> functionNames = new HashSet<>();

		for (ASTPythonNode statement : statements) {
			if (statement instanceof ASTFunctionDeclaration) {
				if (!functionNames.add(((ASTFunctionDeclaration) statement).getName())) {
					Log.error("Duplicate function '" + ((ASTFunctionDeclaration) statement).getName() + "' " + statement.get_SourcePositionStart());
				}
			}
		}
	}
}
