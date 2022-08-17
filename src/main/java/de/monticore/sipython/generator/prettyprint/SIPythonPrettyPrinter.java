package de.monticore.sipython.generator.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.sipython._ast.ASTSIUnitConversion;
import de.monticore.sipython._visitor.SIPythonHandler;
import de.monticore.sipython._visitor.SIPythonTraverser;
import de.monticore.sipython._visitor.SIPythonVisitor2;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteral;

public class SIPythonPrettyPrinter implements SIPythonHandler, SIPythonVisitor2 {

	protected SIPythonTraverser traverser;

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
		node.getSIUnit().accept(getTraverser());
		printer.print("')");

		CommentPrettyPrinter.printPostComments(node, printer);
	}

}
