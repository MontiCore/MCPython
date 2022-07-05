package de.monticore.sipython.generator.prettyprint;

import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.sipython.types.check.DeriveSymTypeOfSIPython;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteral;
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

	TypeCalculator tc = new TypeCalculator(null, new DeriveSymTypeOfSIPython());

	public SIPythonCommonExpressionsPrettyPrinter(IndentPrinter printer) {
		super(printer);
		this.preDefined = new HashSet<>();
		this.preDefined.add(print);
		this.preDefined.add(value);
		this.preDefined.add(basevalue);
	}

	@Override
	public void handle(ASTCallExpression node) {
		CommentPrettyPrinter.printPreComments(node, this.getPrinter());

		CommentPrettyPrinter.printPreComments(node, printer);

		node.getExpression().accept(getTraverser());

		printer.print("(");

		boolean first = true;
		for (ASTExpression expression : node.getArguments().getExpressionList()) {
			if (!first) {
				printer.print(", ");
			} else {
				first = false;
			}
			expression.accept(getTraverser());
		}

		printer.print(")");

		CommentPrettyPrinter.printPostComments(node, this.getPrinter());
	}

	@Override
	public void handle(ASTPlusExpression node) {
		getPrinter().print("(");
		super.handle(node);
		getPrinter().print(")");
	}

	@Override
	public void handle(ASTMinusExpression node) {
		getPrinter().print("(");
		super.handle(node);
		getPrinter().print(")");
	}

	@Override
	public void handle(ASTModuloExpression node) {
		getPrinter().print("(");
		super.handle(node);
		getPrinter().print(")");
	}

}
