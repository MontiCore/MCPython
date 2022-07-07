package de.monticore.sipython.generator.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.sipython._ast.ASTSIUnitConversion;
import de.monticore.sipython._visitor.SIPythonHandler;
import de.monticore.sipython._visitor.SIPythonTraverser;
import de.monticore.sipython._visitor.SIPythonVisitor2;
import de.monticore.sipython.types.check.DeriveSymTypeOfSIPython;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunits.utility.Converter;
import de.monticore.siunits.utility.UnitPrettyPrinter;
import de.monticore.types.check.*;

import javax.measure.converter.UnitConverter;
import javax.measure.unit.Unit;

public class SIPythonPrettyPrinter implements SIPythonHandler, SIPythonVisitor2 {

	protected SIPythonTraverser traverser;
	protected TypeCalculator tc;

	@Override
	public SIPythonTraverser getTraverser() {
		return traverser;
	}

	@Override
	public void setTraverser(SIPythonTraverser traverser) {
		this.traverser = traverser;
	}

	IndentPrinter printer;

	public SIPythonPrettyPrinter(IndentPrinter printer) {
		this.printer = printer;
		this.tc = new TypeCalculator(null, new DeriveSymTypeOfSIPython());
	}

	public IndentPrinter getPrinter() {
		return printer;
	}

	@Override
	public void traverse(ASTSIUnitConversion node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		if (node.getExpression() instanceof ASTLiteralExpression) {
			ASTLiteralExpression literalExpression = ((ASTLiteralExpression) node.getExpression());
			if (literalExpression.getLiteral() instanceof ASTSIUnitLiteral) {
				ASTSIUnitLiteral astsiUnitLiteral = ((ASTSIUnitLiteral) literalExpression.getLiteral());
				astsiUnitLiteral.getNumericLiteral().accept(getTraverser());
			}
		} else {
			node.getExpression().accept(this.getTraverser());
		}

		printer.print(" * ureg('");
		node.getSIUnitType().accept(getTraverser());
		printer.print("')");

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	private String printNumericType(SymTypeExpression symTypeExpression) {
		if (symTypeExpression instanceof SymTypeOfNumericWithSIUnit)
			return ((SymTypeOfNumericWithSIUnit) symTypeExpression)
					.getNumericType().print();
		else return symTypeExpression.print();
	}

	public static String factorStart(UnitConverter converter) {
		if (converter != UnitConverter.IDENTITY && converter.convert(1) != 1.0)
			return "((";
		else return "";
	}

	public static String factorStartSimple(UnitConverter converter) {
		if (converter != UnitConverter.IDENTITY && converter.convert(1) != 1.0)
			return "(";
		else return "";
	}

	public static String factorEnd(UnitConverter converter) {
		if (converter != UnitConverter.IDENTITY && converter.convert(1) != 1.0) {
			String factor;
			if (converter.convert(1) > 1)
				factor = " * " + converter.convert(1);
			else
				factor = " / " + converter.inverse().convert(1);
			return ")" + factor + ")";
		} else
			return "";
	}

	public static String factorEndSimple(UnitConverter converter) {
		if (converter != UnitConverter.IDENTITY && converter.convert(1) != 1.0) {
			String factor;
			if (converter.convert(1) > 1)
				factor = " * " + converter.convert(1);
			else
				factor = " / " + converter.inverse().convert(1);
			return ")" + factor;
		} else
			return "";
	}
}
