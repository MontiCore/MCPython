package de.monticore.sipython._cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisASTExpressionCoCo;
import de.monticore.sipython.types.check.DeriveSymTypeOfSIPython;
import de.monticore.types.check.*;
import de.monticore.types.check.cocos.TypeCheckCoCo;

public class SIPythonCommonExpressionTypeCheckCoco extends TypeCheckCoCo implements ExpressionsBasisASTExpressionCoCo {

	public static SIPythonCommonExpressionTypeCheckCoco getCoCo() {
		TypeCalculator typeCheck = new TypeCalculator(null, new DeriveSymTypeOfSIPython());
		return new SIPythonCommonExpressionTypeCheckCoco(typeCheck);
	}

	public SIPythonCommonExpressionTypeCheckCoco(TypeCalculator typeCheck) {
		super(typeCheck);
	}

	@Override
	public void check(ASTExpression node) {
		checkExpression(node);
	}
}
