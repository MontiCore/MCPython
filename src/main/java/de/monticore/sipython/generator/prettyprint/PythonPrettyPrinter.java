/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sipython.generator.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
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
	public void traverse(ASTPythonScript node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		for (ASTStatement astStatement : node.getStatementList()) {
			if (astStatement instanceof ASTEmptyStatement) {
				printer.println();
			} else if (astStatement instanceof ASTExpressionStatement) {
				astStatement.accept(getTraverser());
				printer.println();
			} else {
				astStatement.accept(getTraverser());
			}
		}
		CommentPrettyPrinter.printPostComments(node, printer);
	}

	@Override
	public void traverse(ASTLocalVariableDeclarationStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.print(node.getVariableDeclaration().getDeclarator().getName());
		printer.print(" = ");
		ASTVariableInit astVariableInit = node.getVariableDeclaration().getVariableInit();
		astVariableInit.accept(getTraverser());
		printer.println();

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	@Override
	public void traverse(ASTSimpleInit node) {
		node.getExpression().accept(getTraverser());
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
		printer.print(node.getForVariable());
		printer.print(" in ");
		node.getExpression().accept(getTraverser());
		printer.print(":");
		printer.println();
	}

	@Override
	public void traverse(ASTArrayForControl node) {
		printer.print("for ");
		printer.print(node.getForVariable());
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
	public void traverse(ASTFunctionCall node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.print(node.getName());

		printer.print("(");

		boolean first = true;
		for (ASTExpression expression : node.getFunctionArguments().getExpressionList()) {
			if (!first) {
				printer.print(", ");
			} else {
				first = false;
			}
			expression.accept(getTraverser());
		}

		printer.print(")");
		printer.println();

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	@Override
	public void traverse(ASTFunctionDeclaration node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.print("def ");
		printer.print(node.getName());
		printer.print("(");
		node.getFunctionDeclarationArguments().accept(getTraverser());
		printer.print("):");
		printer.println();
		node.getStatementBlock().accept(getTraverser());

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	@Override
	public void traverse(ASTSimpleFunctionDeclarationArguments node) {
		boolean first = true;
		for (ASTExpression expressionStatement : node.getExpressionList()) {
			if (!first) {
				printer.print(", ");
			} else {
				first = false;
			}
			expressionStatement.accept(getTraverser());

		}
	}

	@Override
	public void traverse(ASTReturnStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.print("return ");
		if (node.isPresentExpression()) {
			node.getExpression().accept(getTraverser());
		}
		printer.println();

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	@Override
	public void traverse(ASTStatementBlock node) {
		ASTStatementBlockBody blockBody = node.getStatementBlockBody();
		printer.indent();
		for (ASTStatement statement : blockBody.getStatementList()) {
			statement.accept(getTraverser());
		}
		printer.unindent();
	}
}
