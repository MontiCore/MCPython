package de.monticore.sipython._cocos;

import de.monticore.expressions.commonexpressions._ast.ASTInfixExpression;
import de.monticore.expressions.commonexpressions._ast.ASTMultExpression;
import de.monticore.expressions.commonexpressions._ast.ASTPlusExpression;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTInfixExpressionCoCo;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisASTExpressionCoCo;
import de.monticore.sipython.SIPythonMill;
import de.monticore.sipython.types.check.DeriveSymTypeOfSIPython;
import de.monticore.sipython.types.check.SynthesizeSymTypeFromSIPython;
import de.monticore.types.check.*;
import de.monticore.types.check.cocos.TypeCheckCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class SIPythonCommonExpressionTypeCheckCoco extends TypeCheckCoCo implements ExpressionsBasisASTExpressionCoCo {

	public static SIPythonCommonExpressionTypeCheckCoco getCoCo() {
		TypeCalculator typeCheck = new TypeCalculator(new SynthesizeSymTypeFromSIPython(), new DeriveSymTypeOfSIPython());
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
