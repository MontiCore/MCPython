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

		// Note: Python overrides the arguments production
		node.getArguments().accept(getTraverser());

		CommentPrettyPrinter.printPostComments(node, this.getPrinter());
	}

}
