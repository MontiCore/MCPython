package de.monticore.sipython.types.check;

import de.monticore.sipython.SIPythonMill;
import de.monticore.sipython._ast.ASTSIUnitConversion;
import de.monticore.sipython._visitor.SIPythonHandler;
import de.monticore.sipython._visitor.SIPythonTraverser;
import de.monticore.siunits.utility.UnitPrettyPrinter;
import de.monticore.types.check.AbstractSynthesize;
import de.monticore.types.check.SIUnitSymTypeExpressionFactory;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;

public class SynthesizeSymTypeFromSIPython extends AbstractSynthesize implements SIPythonHandler {

	protected SIPythonTraverser traverser;

	public SynthesizeSymTypeFromSIPython() {
		super(SIPythonMill.traverser());
	}

	@Override
	public SIPythonTraverser getTraverser() {
		return traverser;
	}

	@Override
	public void setTraverser(SIPythonTraverser traverser) {
		this.traverser = traverser;
	}

	@Override
	public void traverse(ASTSIUnitConversion node) {
		SymTypeExpression siunitType;

		siunitType = SIUnitSymTypeExpressionFactory.createSIUnit(
				UnitPrettyPrinter.printUnit(node.getSIUnit()), node.getEnclosingScope());

		typeCheckResult.setResult(siunitType);
	}

}
