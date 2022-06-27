package de.monticore.sipython._cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.sipython._ast.ASTSIPythonScript;
import de.monticore.sipython._ast.ASTSIUnitParse;
import de.monticore.sipython._visitor.SIPythonVisitor2;
import de.monticore.sipython.types.check.DeriveSymTypeOfSIPython;
import de.monticore.sipython.types.check.SynthesizeSymTypeFromSIPython;
import de.monticore.siunits.utility.UnitFactory;
import de.monticore.siunits.utility.UnitPrettyPrinter;
import de.monticore.types.check.*;
import de.monticore.types.check.cocos.TypeCheckCoCo;
import de.se_rwth.commons.logging.Log;

public class SIPythonTypeCheckCoco extends TypeCheckCoCo implements SIPythonASTSIPythonScriptCoCo, SIPythonVisitor2 {

	public static SIPythonTypeCheckCoco getCoCo() {
		TypeCalculator typeCalculator = new TypeCalculator(new SynthesizeSymTypeFromSIPython(), new DeriveSymTypeOfSIPython());
		return new SIPythonTypeCheckCoco(typeCalculator);
	}

	/**
	 * Creates an instance of TypeCheckCoCo
	 *
	 * @param typeCheck a {@link TypeCheck} object instantiated with the correct
	 *                  {@link ISynthesize} and {@link IDerive} objects of
	 *                  the current language
	 */
	public SIPythonTypeCheckCoco(TypeCalculator typeCheck) {
		super(typeCheck);
	}

	@Override
	public void check(ASTSIPythonScript node) {

	}

	@Override
	public void visit(ASTSIUnitParse node) {
		checkExpression(node.getExpression());
		SymTypeExpression typeOfExpression = this.tc.typeOf(node.getExpression());

		SymTypeExpression siunitType = SIUnitSymTypeExpressionFactory.createSIUnit(
				UnitPrettyPrinter.printUnit(node.getSIUnit()), node.getEnclosingScope());

		if (typeOfExpression instanceof SymTypeOfSIUnit) {
			if (!siunitType.equals(typeOfExpression)) {
				Log.error("type parsing");
			}
		}


	}

}
