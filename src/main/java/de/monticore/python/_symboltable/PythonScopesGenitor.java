package de.monticore.python._symboltable;

import de.monticore.python._ast.*;
import de.monticore.sipython.types.check.DeriveSymTypeOfSIPython;
import de.monticore.sipython.types.check.SIPythonSymTypeExpressionFactory;
import de.monticore.types.check.*;

import java.util.List;

public class PythonScopesGenitor extends PythonScopesGenitorTOP {

	private TypeCalculator tc;

	public PythonScopesGenitor() {
		super();
		initTypeCheck();
	}

	private void initTypeCheck() {
		tc = new TypeCalculator(null, new DeriveSymTypeOfSIPython());
	}

	@Override
	public void endVisit(ASTVariableDeclaration node) {
		super.endVisit(node);
		SymTypeExpression symTypeExpression = getSymTypeOfVariableInit(node.getVariableInit());
		node.getSymbol().setType(symTypeExpression);
	}

	private SymTypeExpression getSymTypeOfVariableInit(ASTVariableInit node) {
		SymTypeExpression symTypeExpression = null;
		if (node instanceof ASTSimpleInit) {
			symTypeExpression = tc.typeOf(((ASTSimpleInit) node).getExpression());
		} else if (node instanceof ASTArrayInit) {
			symTypeExpression = getSymTypeOfVariableInit(((ASTArrayInit) node).getVariableInit(0));
		}
		return symTypeExpression;
	}

	@Override
	public void endVisit(ASTFunctionDeclaration node) {
		super.endVisit(node);

		List<ASTStatement> statements = node.getStatementBlock().getStatementBlockBody().getStatementList();

		for (ASTFunctionParameter parameter : node.getFunctionParameters().getFunctionParameterList()) {
			parameter.accept(getTraverser());
		}

		for (ASTStatement statement : statements) {
			statement.accept(getTraverser());
		}

		ASTStatement lastStatement = statements.get(statements.size() - 1);

		if (lastStatement instanceof ASTReturnStatement) {
			SymTypeExpression symTypeExpression = tc.typeOf(((ASTReturnStatement) lastStatement).getExpression());
			node.getSymbol().setType(symTypeExpression);
		} else {
			node.getSymbol().setType(SymTypeExpressionFactory.createTypeVoid());
		}
	}

	@Override
	public void endVisit(ASTFunctionParameter node) {
		node.getSymbol().setType(SIPythonSymTypeExpressionFactory.createPrimitive());
	}

}
