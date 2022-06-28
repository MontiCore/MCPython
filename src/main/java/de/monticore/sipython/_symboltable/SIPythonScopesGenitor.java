package de.monticore.sipython._symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.sipython.types.check.DeriveSymTypeOfSIPython;
import de.monticore.sipython.types.check.SynthesizeSymTypeFromSIPython;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunitliterals.utility.SIUnitLiteralDecoder;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.AbstractDerive;
import de.monticore.types.check.AbstractSynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types.check.cocos.TypeCheckCoCo;

public class SIPythonScopesGenitor extends SIPythonScopesGenitorTOP {

	private TypeCalculator tc;

	public SIPythonScopesGenitor() {
		super();
		initTypeCheck();
	}

	private void initTypeCheck() {
		AbstractSynthesize synthesize = new SynthesizeSymTypeFromSIPython();
		AbstractDerive der = new DeriveSymTypeOfSIPython();
		tc = new TypeCalculator(synthesize, der);
	}

	/*
	@Override
	public void endVisit(ASTSIVariableDeclaration node) {
		super.endVisit(node);
		SymTypeExpression symTypeExpression = tc.typeOf(node.getExpression());
		node.getSymbol().setType(symTypeExpression);
	}

	 */

}
