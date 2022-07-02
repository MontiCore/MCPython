package de.monticore.sipython.types.check;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;

public class SIPythonSymTypeExpressionFactory extends SymTypeExpressionFactory {

	public static SymTypeExpression createPrimitive() {
		return SymTypeExpressionFactory.createPrimitive("int");
	}
}
