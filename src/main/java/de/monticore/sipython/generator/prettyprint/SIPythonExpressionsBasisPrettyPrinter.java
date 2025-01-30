package de.monticore.sipython.generator.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class SIPythonExpressionsBasisPrettyPrinter extends ExpressionsBasisPrettyPrinter {

	public SIPythonExpressionsBasisPrettyPrinter(IndentPrinter printer) {
		super(printer, true);
	}

	@Override
	public void handle(ASTNameExpression node) {
		CommentPrettyPrinter.printPreComments(node, getPrinter());
		getPrinter().print(node.getName());
		CommentPrettyPrinter.printPostComments(node, getPrinter());
	}

	@Override
	public void handle(ASTLiteralExpression node) {
		node.getLiteral().accept(getTraverser());
	}

}
