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

	public SIPythonCommonExpressionsPrettyPrinter(IndentPrinter printer) {
		super(printer);
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

}
