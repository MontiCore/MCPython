package de.monticore.sipython._cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.python._ast.ASTFunctionCall;
import de.monticore.python._cocos.PythonASTFunctionCallCoCo;
import de.monticore.sipython.types.check.DeriveSymTypeOfSIPython;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.cocos.TypeCheckCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Iterator;

public class SIPythonFunctionCallTypeCheckCoco extends TypeCheckCoCo implements PythonASTFunctionCallCoCo {

	public static SIPythonFunctionCallTypeCheckCoco getCoCo() {
		TypeCalculator typeCalculator = new TypeCalculator(null, new DeriveSymTypeOfSIPython());
		return new SIPythonFunctionCallTypeCheckCoco(typeCalculator);
	}

	public SIPythonFunctionCallTypeCheckCoco(TypeCalculator typeCheck) {
		super(typeCheck);
	}

	@Override
	public void check(ASTFunctionCall node) {
		FunctionSymbol functionSymbol = node.getNameSymbol();

		Iterator<VariableSymbol> parameterIt = functionSymbol.getParameterList().iterator();
		Iterator<ASTExpression> expressionIt = node.getExpressionList().iterator();

		while (parameterIt.hasNext() && expressionIt.hasNext()) {
			VariableSymbol parameter = parameterIt.next();
			ASTExpression expression = expressionIt.next();

			SymTypeExpression parameterType = parameter.getType();
			SymTypeExpression expressionType = tc.typeOf(expression);

			if (!TypeCheck.compatible(parameterType, expressionType)) {
				Log.error("Incompatible parameter type '" + expressionType.print() + "' for parameter '" + parameter.getName() + "' " + node.get_SourcePositionStart());
			}
		}
	}
}
