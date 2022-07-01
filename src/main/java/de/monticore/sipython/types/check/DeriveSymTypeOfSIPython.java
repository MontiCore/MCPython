package de.monticore.sipython.types.check;

import de.monticore.sipython.SIPythonMill;
import de.monticore.sipython._visitor.SIPythonTraverser;
import de.monticore.types.check.*;

public class DeriveSymTypeOfSIPython extends AbstractDerive {

	private DeriveSymTypeOfAssignmentExpressionsWithSIUnitTypes deriveSymTypeOfAssignmentExpressions;

	private DeriveSymTypeOfCommonExpressionsWithSIUnitTypes deriveSymTypeOfCommonExpressions;

	private DeriveSymTypeOfSIPythonExpression deriveSymTypeOfSIPythonExpression;

	private DeriveSymTypeOfLiterals deriveSymTypeOfLiterals;

	private DeriveSymTypeOfSIUnitLiterals deriveSymTypeOfSIUnitLiterals;

	private DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals;

	public DeriveSymTypeOfSIPython(SIPythonTraverser traverser) {
		super(traverser);
		init(traverser);
	}

	public DeriveSymTypeOfSIPython(){
		this(SIPythonMill.traverser());
	}


	/**
	 * set the last typeCheckResult of all calculators to the same object
	 */
	public void setTypeCheckResult(TypeCheckResult typeCheckResult) {
		deriveSymTypeOfAssignmentExpressions.setTypeCheckResult(typeCheckResult);
		deriveSymTypeOfMCCommonLiterals.setTypeCheckResult(typeCheckResult);
		deriveSymTypeOfCommonExpressions.setTypeCheckResult(typeCheckResult);
		deriveSymTypeOfSIPythonExpression.setTypeCheckResult(typeCheckResult);
		deriveSymTypeOfLiterals.setTypeCheckResult(typeCheckResult);
		deriveSymTypeOfSIUnitLiterals.setTypeCheckResult(typeCheckResult);
	}

	/**
	 * initialize the typescalculator
	 */
	public void init(SIPythonTraverser traverser) {
		deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressionsWithSIUnitTypes();
		traverser.add4CommonExpressions(deriveSymTypeOfCommonExpressions);
		traverser.setCommonExpressionsHandler(deriveSymTypeOfCommonExpressions);

		deriveSymTypeOfAssignmentExpressions = new DeriveSymTypeOfAssignmentExpressionsWithSIUnitTypes();
		traverser.add4AssignmentExpressions(deriveSymTypeOfAssignmentExpressions);
		traverser.setAssignmentExpressionsHandler(deriveSymTypeOfAssignmentExpressions);

		deriveSymTypeOfSIPythonExpression = new DeriveSymTypeOfSIPythonExpression();
		traverser.add4ExpressionsBasis(deriveSymTypeOfSIPythonExpression);
		traverser.setExpressionsBasisHandler(deriveSymTypeOfSIPythonExpression);

		deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
		traverser.add4MCLiteralsBasis(deriveSymTypeOfLiterals);

		deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
		traverser.add4MCCommonLiterals(deriveSymTypeOfMCCommonLiterals);

		deriveSymTypeOfSIUnitLiterals = new DeriveSymTypeOfSIUnitLiterals();
		traverser.setSIUnitLiteralsHandler(deriveSymTypeOfSIUnitLiterals);

		setTypeCheckResult(typeCheckResult);
	}

}
