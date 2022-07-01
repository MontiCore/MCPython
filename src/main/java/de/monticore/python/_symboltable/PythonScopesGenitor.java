package de.monticore.python._symboltable;

import de.monticore.python._ast.*;
import de.monticore.sipython.types.check.DeriveSymTypeOfSIPython;
import de.monticore.types.check.*;

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

}
