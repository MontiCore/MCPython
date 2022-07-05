package de.monticore.sipython._cocos;

import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._cocos.*;
import de.monticore.sipython.types.check.DeriveSymTypeOfSIPython;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.cocos.TypeCheckCoCo;
import de.se_rwth.commons.logging.Log;

public class SIPythonCommonExpressionsTypeCheckCoco extends TypeCheckCoCo implements
		CommonExpressionsASTPlusExpressionCoCo, CommonExpressionsASTMinusExpressionCoCo, CommonExpressionsASTModuloExpressionCoCo {

	public static SIPythonCommonExpressionsTypeCheckCoco getCoco() {
		return new SIPythonCommonExpressionsTypeCheckCoco(new TypeCalculator(null, new DeriveSymTypeOfSIPython()));
	}

	public SIPythonCommonExpressionsTypeCheckCoco(TypeCalculator typeCheck) {
		super(typeCheck);
	}

	@Override
	public void check(ASTPlusExpression node) {
		checkInfixExpression(node);
	}

	@Override
	public void check(ASTMinusExpression node) {
		checkInfixExpression(node);
	}

	@Override
	public void check(ASTModuloExpression node) {
		checkInfixExpression(node);
	}

	private void checkInfixExpression(ASTInfixExpression node) {
		checkExpression(node.getLeft());
		checkExpression(node.getRight());

		SymTypeExpression leftType = tc.typeOf(node.getLeft());
		SymTypeExpression rightType = tc.typeOf(node.getRight());

		if (!TypeCheck.compatible(leftType, rightType)) {
			Log.error(node.get_SourcePositionStart() + " Incompatible SI type '" +
					((SymTypeOfNumericWithSIUnit) leftType).getSIUnit().print() + "' and '" +
					((SymTypeOfNumericWithSIUnit) rightType).getSIUnit().print() + "' " + node.get_SourcePositionStart());
		}
	}
}
