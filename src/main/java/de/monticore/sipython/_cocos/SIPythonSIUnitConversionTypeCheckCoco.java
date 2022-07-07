package de.monticore.sipython._cocos;

import de.monticore.sipython.types.check.DeriveSymTypeOfSIPython;
import de.monticore.siunits.utility.UnitPrettyPrinter;
import de.monticore.types.check.*;
import de.monticore.types.check.cocos.TypeCheckCoCo;
import de.se_rwth.commons.logging.Log;

public class SIPythonSIUnitConversionTypeCheckCoco extends TypeCheckCoCo implements SIPythonASTSIUnitConversionCoCo {

	public static SIPythonSIUnitConversionTypeCheckCoco getCoCo() {
		TypeCalculator typeCalculator = new TypeCalculator(null, new DeriveSymTypeOfSIPython());
		return new SIPythonSIUnitConversionTypeCheckCoco(typeCalculator);
	}

	public SIPythonSIUnitConversionTypeCheckCoco(TypeCalculator typeCheck) {
		super(typeCheck);
	}


	@Override
	public void check(de.monticore.sipython._ast.ASTSIUnitConversion node) {
		checkExpression(node.getExpression());

		SymTypeExpression typeOfExpression = this.tc.typeOf(node.getExpression());

		SymTypeExpression siunitType =
				SIUnitSymTypeExpressionFactory.createSIUnit(UnitPrettyPrinter.printUnit(node.getSIUnitType().getSIUnit()), node.getEnclosingScope());

		if (!TypeCheck.compatible(siunitType, ((SymTypeOfNumericWithSIUnit) typeOfExpression).getSIUnit())) {
			Log.error(node.get_SourcePositionStart() + " Incompatible SI type conversion from '" + ((SymTypeOfNumericWithSIUnit) typeOfExpression).getSIUnit().print() + "' to '" + siunitType.print() + "'");
		}
	}

}
