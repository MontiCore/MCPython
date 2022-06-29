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
			if (astStatement instanceof ASTLocalVariableDeclarationStatement) {
				astStatement.accept(getTraverser());
			} else if (astStatement instanceof ASTIfStatement) {
				astStatement.accept(getTraverser());
			} else if (astStatement instanceof ASTForStatement) {
				astStatement.accept(getTraverser());
			} else if (astStatement instanceof ASTWhileStatement) {
				astStatement.accept(getTraverser());
			} else if (astStatement instanceof ASTFunctionDeclaration) {
				astStatement.accept(getTraverser());
			} else if (astStatement instanceof ASTReturnStatement) {
				astStatement.accept(getTraverser());
			} else if (astStatement instanceof ASTFunctionCall) {
				astStatement.accept(getTraverser());
			} else if (astStatement instanceof ASTExpressionStatement) {
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
	public void traverse(ASTFunctionDeclaration node) {
		printer.print(node.getFunctionName().getName());
		printer.print("(");
		node.getFunctionDeclarationArguments().accept(getTraverser());
		printer.print("):");
		printer.println();
		node.getStatementBlock().accept(getTraverser());
	}

	@Override
	public void traverse(ASTSimpleFunctionDeclarationArguments node) {
		for (ASTExpression expressionStatement : node.getExpressionList()) {
			expressionStatement.accept(getTraverser());
			printer.print(", ");
		}
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
