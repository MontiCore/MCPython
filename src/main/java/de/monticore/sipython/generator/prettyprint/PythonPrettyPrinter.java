/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sipython.generator.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.python._ast.*;
import de.monticore.python._visitor.PythonHandler;
import de.monticore.python._visitor.PythonTraverser;
import de.monticore.python._visitor.PythonVisitor2;

public class PythonPrettyPrinter implements PythonHandler, PythonVisitor2 {

	protected PythonTraverser pythonTraverser;

	@Override
	public PythonTraverser getTraverser() {
		return pythonTraverser;
	}

	@Override
	public void setTraverser(PythonTraverser traverser) {
		this.pythonTraverser = traverser;
	}

	IndentPrinter printer;

	public PythonPrettyPrinter(IndentPrinter printer) {
		this.printer = printer;
	}

	@Override
	public void traverse(ASTEOL node) {
		printer.println();
	}

	@Override
	public void traverse(ASTPythonScript node) {
		printer.println("from pint import UnitRegistry");
		printer.println("ureg = UnitRegistry()");

		CommentPrettyPrinter.printPreComments(node, printer);

		for (ASTStatement astStatement : node.getStatementList()) {
			if (astStatement instanceof ASTExpressionStatement) {
				astStatement.accept(getTraverser());
				printer.println();
			} else {
				astStatement.accept(getTraverser());
			}
		}
		CommentPrettyPrinter.printPostComments(node, printer);
	}

	@Override
	public void traverse(ASTImportStatement node) {
		if (node.isPresentModule()) {
			printer.print("from ");
			printer.print(node.getModule());
			printer.print(" ");
		}
		printer.print("import ");
		printer.print(node.getName());
		node.getEOL().accept(getTraverser());
	}

	@Override
	public void traverse(ASTLocalVariableDeclarationStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.print(node.getVariableDeclaration().getName());
		printer.print(" = ");

		ASTVariableDeclaration astVariableDeclaration = node.getVariableDeclaration();
		ASTVariableInit astVariableInit = astVariableDeclaration.getVariableInit();

		if (astVariableInit instanceof ASTSimpleInit) {
			ASTSimpleInit simpleInit = ((ASTSimpleInit) astVariableInit);

			simpleInit.getExpression().accept(this.getTraverser());

			CommentPrettyPrinter.printPostComments(node, printer);

		} else {
			astVariableInit.accept(getTraverser());
		}

		node.getEOL().accept(getTraverser());

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	@Override
	public void traverse(ASTArrayInit node) {
		printer.print("[");

		boolean first = true;
		for (ASTVariableInit variableInit : node.getVariableInitList()) {
			if (!first) {
				printer.print(", ");
			} else {
				first = false;
			}
			variableInit.accept(getTraverser());
		}

		printer.print("]");
	}

	@Override
	public void traverse(ASTTupleInit node) {
		printer.print("(");

		boolean first = true;
		for (ASTVariableInit variableInit : node.getVariableInitList()) {
			if (!first) {
				printer.print(", ");
			} else {
				first = false;
			}
			variableInit.accept(getTraverser());
		}

		printer.print(")");
	}

	@Override
	public void traverse(ASTIfStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.print("if ");
		node.getCondition().accept(getTraverser());
		printer.print(":");
		printer.println();
		node.getThenStatement().accept(getTraverser());
		if (node.isPresentElseStatement()) {
			printer.print("else:");
			printer.println();
			node.getElseStatement().accept(getTraverser());
		}

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	@Override
	public void traverse(ASTForStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		node.getForControl().accept(getTraverser());
		node.getStatementBlock().accept(getTraverser());

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	@Override
	public void traverse(ASTCommonForControl node) {
		printer.print("for ");
		printer.print(node.getName());
		printer.print(" in ");
		node.getExpression().accept(getTraverser());
		printer.print(":");
		printer.println();
	}

	@Override
	public void traverse(ASTArrayForControl node) {
		printer.print("for ");
		printer.print(node.getName());
		printer.print(" in ");
		node.getArrayInit().accept(getTraverser());
		printer.print(":");
		printer.println();
	}

	@Override
	public void traverse(ASTWhileStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.print("while ");
		node.getCondition().accept(getTraverser());
		printer.print(":");
		printer.println();
		node.getStatementBlock().accept(getTraverser());

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	@Override
	public void traverse(ASTFunctionDeclaration node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.print("def ");
		printer.print(node.getName());
		printer.print("(");
		node.getFunctionParameters().accept(getTraverser());
		printer.print("):");
		node.getEOL().accept(getTraverser());
		node.getStatementBlock().accept(getTraverser());

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	@Override
	public void traverse(ASTFunctionParameters node) {
		boolean first = true;
		for (ASTFunctionParameter argument : node.getFunctionParameterList()) {
			if (!first) {
				printer.print(", ");
			} else {
				first = false;
			}
			printer.print(argument.getName());

		}
	}

	@Override
	public void traverse(ASTReturnStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.print("return ");
		if (node.isPresentExpression()) {
			node.getExpression().accept(getTraverser());
		}
		node.getEOL().accept(getTraverser());

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	@Override
	public void traverse(ASTStatementBlock node) {
		CommentPrettyPrinter.printPreComments(node, printer);


		ASTStatementBlockBody blockBody = node.getStatementBlockBody();
		printer.indent();
		for (ASTStatement statement : blockBody.getStatementList()) {
			statement.accept(getTraverser());
		}
		printer.unindent();

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	@Override
	public void traverse(ASTExpressionStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		node.getExpression().accept(getTraverser());
		node.getEOL().accept(getTraverser());

		CommentPrettyPrinter.printPostComments(node, printer);
	}
}
