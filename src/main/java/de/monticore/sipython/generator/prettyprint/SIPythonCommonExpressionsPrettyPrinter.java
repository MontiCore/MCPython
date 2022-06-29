package de.monticore.sipython.generator.prettyprint;

import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.sipython.types.check.DeriveSymTypeOfSIPython;
import de.monticore.sipython.types.check.SynthesizeSymTypeFromSIPython;
import de.monticore.siunits.utility.Converter;
import de.monticore.types.check.*;

import javax.measure.converter.UnitConverter;
import javax.measure.unit.Unit;
import java.util.HashSet;
import java.util.Set;

public class SIPythonCommonExpressionsPrettyPrinter extends CommonExpressionsPrettyPrinter {

	private Set<String> preDefined;
	private final String print = "print";
	private final String value = "value";
	private final String basevalue = "basevalue";

	TypeCalculator tc = new TypeCalculator(new SynthesizeSymTypeFromSIPython(), new DeriveSymTypeOfSIPython());

	public SIPythonCommonExpressionsPrettyPrinter(IndentPrinter printer) {
		super(printer);
		this.preDefined = new HashSet<>();
		this.preDefined.add(print);
		this.preDefined.add(value);
		this.preDefined.add(basevalue);
	}

	/*
	@Override
	public void handle(ASTCallExpression node) {
		CommentPrettyPrinter.printPreComments(node, this.getPrinter());



		CommentPrettyPrinter.printPostComments(node, this.getPrinter());
	}

	@Override
	public void handle(ASTPlusExpression node) {
		SymTypeExpression symType = tc.typeOf(node);
		if (symType instanceof SymTypeOfNumericWithSIUnit) {
			handlePlusMinusModulo(node, "+", (SymTypeOfNumericWithSIUnit) symType);
		} else
			super.handle(node);
	}

	@Override
	public void handle(ASTMinusExpression node) {
		SymTypeExpression symType = tc.typeOf(node);
		if (symType instanceof SymTypeOfNumericWithSIUnit) {
			handlePlusMinusModulo(node, "-", (SymTypeOfNumericWithSIUnit) symType);
		} else
			super.handle(node);
	}

	@Override
	public void handle(ASTModuloExpression node) {
		SymTypeExpression symType = tc.typeOf(node);
		if (symType instanceof SymTypeOfNumericWithSIUnit) {
			handlePlusMinusModulo(node, "%", (SymTypeOfNumericWithSIUnit) symType);
		} else
			super.handle(node);
	}

 */

	private void handlePlusMinusModulo(ASTInfixExpression node, String operator, SymTypeOfNumericWithSIUnit symType) {
		/*CommentPrettyPrinter.printPreComments(node, this.getPrinter());

		UnitConverter leftConverter = UnitConverter.IDENTITY;
		UnitConverter rightConverter = UnitConverter.IDENTITY;
		Unit unit = symType.getUnit();
		Unit leftUnit = ((SymTypeOfNumericWithSIUnit) tc.typeOf(node.getLeft())).getUnit();
		Unit rightUnit = ((SymTypeOfNumericWithSIUnit) tc.typeOf(node.getRight())).getUnit();

		if (!leftUnit.equals(unit)) {
			leftConverter = Converter.getConverter(leftUnit, unit);
		}
		if (!rightUnit.equals(unit)) {
			rightConverter = Converter.getConverter(rightUnit, unit);
		}

		getPrinter().print(factorStart(leftConverter));
		node.getLeft().accept(this.getTraverser());
		getPrinter().print(factorEnd(leftConverter));
		this.getPrinter().print(" " + operator + " ");
		getPrinter().print(factorStart(rightConverter));
		node.getRight().accept(this.getTraverser());
		getPrinter().print(factorEnd(rightConverter));

		CommentPrettyPrinter.printPostComments(node, this.getPrinter());

		 */
	}
}
