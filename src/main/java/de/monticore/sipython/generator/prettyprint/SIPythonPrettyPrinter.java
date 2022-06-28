package de.monticore.sipython.generator.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.sipython._ast.ASTSIUnitConversion;
import de.monticore.sipython._visitor.SIPythonHandler;
import de.monticore.sipython._visitor.SIPythonTraverser;
import de.monticore.sipython._visitor.SIPythonVisitor2;

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

	@Override
	public void traverse(ASTSIUnitConversion node) {
		CommentPrettyPrinter.printPreComments(node, printer);
		node.getSIUnit().accept(getTraverser());

		printer.print("(");

		printer.print(")");

		CommentPrettyPrinter.printPostComments(node, printer);
	}
}
