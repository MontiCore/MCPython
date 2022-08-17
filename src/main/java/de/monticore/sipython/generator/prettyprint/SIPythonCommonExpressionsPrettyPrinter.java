package de.monticore.sipython.generator.prettyprint;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

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
