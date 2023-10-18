/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sipython.generator.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.python._ast.*;
import de.monticore.python._visitor.PythonHandler;
import de.monticore.python._visitor.PythonTraverser;
import de.monticore.python._visitor.PythonVisitor2;

import java.util.List;

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
		printer.println("from pint import UnitRegistry");
		printer.println("ureg = UnitRegistry()");

		CommentPrettyPrinter.printPreComments(node, printer);

		for (ASTStatement astStatement : node.getStatementList()) {
			astStatement.accept(getTraverser());
		}
		CommentPrettyPrinter.printPostComments(node, printer);
	}

	// statement block
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

	/*===========================Statements======================================*/

	// else statement part
	@Override
	public void traverse(ASTElseStatementPart node) {
		printer.println("else:");
		node.getStatementBlock().accept(getTraverser());
	}

	// import statement
	@Override
	public void traverse(ASTImportStatement node) {
		if (node.isPresentModule()) {
			printer.print("from ");
			printer.print(node.getModule());
			printer.print(" ");
		}
		printer.print("import ");

		boolean first = true;
		for (ASTPyQualifiedName name : node.getNameList()) {
			if (first) {
				first = false;
			} else {
				printer.print(", ");
			}
			printer.print(name.joined());

		}
		printer.println();
	}

	// if-else statement
	@Override
	public void traverse(ASTIfStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.print("if ");
		node.getCondition().accept(getTraverser());
		printer.print(":");
		printer.println();
		node.getThenStatement().accept(getTraverser());

		for (int i = 0; i < node.getElifStatementList().size(); i++) {
			ASTExpression condition = node.getElifCondition(i);
			ASTStatementBlock statementBlock = node.getElifStatement(i);

			printer.print("elif ");
			condition.accept(getTraverser());
			printer.print(":");
			printer.println();
			statementBlock.accept(getTraverser());
		}

		if (node.isPresentElseStatementPart()) {
			node.getElseStatementPart().accept(getTraverser());
		}

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	// assert statement
	@Override
	public void traverse(ASTAssertStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.print("assert ");
		node.getCondition().accept(getTraverser());
		printer.print(", ");
		printer.print("\"" + node.getErrorMessage() + "\"");
		printer.println();

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	// for statement
	@Override
	public void traverse(ASTForStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.print("for ");
		node.getForControl().accept(getTraverser());
		printer.print(":");
		printer.println();
		node.getStatementBlock().accept(getTraverser());

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	@Override
	public void traverse(ASTCommonForControl node) {
		node.getForVariable().accept(getTraverser());
		printer.print(" in ");
		node.getForIterable().accept(getTraverser());
	}

	@Override
	public void traverse(ASTUnpackedForControl node) {
		boolean first = true;
		for (ASTForVariable forVariable : node.getForVariableList()) {
			if (first) {
				first = false;
			} else {
				printer.print(", ");
			}
			forVariable.accept(getTraverser());
		}
		printer.print(" in ");
		node.getForIterable().accept(getTraverser());
	}

	@Override
	public void traverse(ASTForVariable node) {
		printer.print(node.getName());
	}

	//while statement
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

	// break statement
	@Override
	public void traverse(ASTBreakStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.println("break");

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	// continue statement
	@Override
	public void traverse(ASTContinueStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.println("continue");

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	// try-except-finally statement
	@Override
	public void traverse(ASTTryExceptStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.println("try:");
		node.getTryStatement().accept(getTraverser());

		for (ASTExceptStatement statement : node.getExceptStatementList()) {
			printer.print("except ");
			if(statement.isEmptyNames()){
				// Ignore
			}else if(statement.sizeNames() == 1) {
				printer.print(statement.getName(0));
			}else{
				printer.print("(");
				printer.print(String.join(", ", statement.getNameList()));
				printer.print(")");
			}
			printer.println(":");
			statement.getStatementBlock().accept(getTraverser());
		}

		if (node.isPresentElseStatementPart()) {
			node.getElseStatementPart().accept(getTraverser());
		}

		if (node.isPresentFinallyStatement()) {
			printer.println("finally:");
			node.getFinallyStatement().accept(getTraverser());
		}
	}

	// with open file statement
	@Override
	public void traverse(ASTWithOpenFileStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);
		printer.print(" with ");
		boolean first = true;
		for (ASTOpenFileExpression openFileExpression : node.getOpenFileExpressionList()) {
			if (first) {
				first = false;
			} else {
				printer.print(", ");
			}
			openFileExpression.accept(getTraverser());
		}
		printer.println(":");
	}

	// open file expression
	@Override
	public void traverse(ASTOpenFileExpression node) {
		node.getExpression().accept(getTraverser());
		printer.print(" as ");
		printer.print(node.getFileName());
	}

	// variable declaration statement
	@Override
	public void traverse(ASTVariableDeclaration node) {
		printer.print(node.getName());
		printer.print(" = ");
		node.getVariableInit().accept(getTraverser());
	}

	@Override
	public void traverse(ASTLocalVariableDeclarationStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		node.getVariableDeclaration().accept(getTraverser());
		printer.println();

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	@Override
	public void traverse(ASTSimpleInit node) {
		node.getExpression().accept(getTraverser());
	}

	@Override
	public void traverse(ASTArrayLiteralExpression node) {
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
	public void traverse(ASTTupleLiteralExpression node) {
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

	// function declaration statement
	@Override
	public void traverse(ASTSimpleFunctionDeclaration node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.print("def ");
		printer.print(node.getName());
		printer.print("(");
		node.getFunctionParameters().accept(getTraverser());
		printer.print("):");
		printer.println();
		node.getStatementBlock().accept(getTraverser());

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	@Override
	public void traverse(ASTFunctionParameters node) {
		boolean first = true;
		for (ASTFunctionParameter parameter : node.getFunctionParameterList()) {
			if (!first) {
				printer.print(", ");
			} else {
				first = false;
			}
			parameter.accept(getTraverser());

		}
	}

	@Override
	public void traverse(ASTSimpleFunctionParameter node) {
		printer.print(node.getName());
	}

	@Override
	public void traverse(ASTOptionalFunctionParameter node) {
		printer.print(node.getName());
		printer.print("=");
		node.getExpression().accept(getTraverser());
	}

	@Override
	public void traverse(ASTVarArgFunctionParameter node) {
		printer.print("*");
		printer.print(node.getName());
	}

	// return statement
	@Override
	public void traverse(ASTReturnStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.print("return");
		for (int i = 0; i < node.getExpressionList().size(); i++) {
			ASTExpression exp = node.getExpression(i);
			if(i != 0){
				printer.print(", ");
			}
			exp.accept(getTraverser());
		}
		printer.println();

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	@Override
	public void traverse(ASTRaiseStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.print("raise ");
		if (node.isPresentExpression()) {
			node.getExpression().accept(getTraverser());
		}
		printer.println();

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	// expression statement
	@Override
	public void traverse(ASTExpressionStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		node.getExpression().accept(getTraverser());
		printer.println();

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	/*===========================Literals======================================*/

	// string literals for python
	@Override
	public void traverse(ASTStringLiteralPython node) {
		printer.print("\"");
		printer.print(node.getSource());
		printer.print("\"");

	}

	// boolean literals for python
	@Override
	public void traverse(ASTBooleanLiteralPython node) {
		if (node.getSource() == 1) {
			printer.print("True");
		} else {
			printer.print("False");
		}
	}


	/*======================Expressions===========================================*/

	// ternary-operator expression
	@Override
	public void traverse(ASTTernaryOperatorExpression node) {
		CommentPrettyPrinter.printPreComments(node, printer);
		node.getThenExpression().accept(getTraverser());
		for (int i = 0; i < node.getConditionList().size(); i++) {
			printer.print(" if ");
			node.getCondition(i).accept(getTraverser());
			printer.print(" else ");
			node.getElseExpression(i).accept(getTraverser());
		}
		CommentPrettyPrinter.printPostComments(node, printer);
	}


	// mathematical expression
	@Override
	public void traverse(ASTIntegerDivisionExpression node) {
		node.getLeft().accept(getTraverser());
		printer.print(" // ");
		node.getRight().accept(getTraverser());
	}

	@Override
	public void traverse(ASTIntegerPowExpression node) {
		node.getLeft().accept(getTraverser());
		printer.print(" ** ");
		node.getRight().accept(getTraverser());
	}

	//logical expressions
	@Override
	public void traverse(ASTAndExpression node) {
		node.getLeft().accept(getTraverser());
		printer.print(" and ");
		node.getRight().accept(getTraverser());
	}

	@Override
	public void traverse(ASTOrExpression node) {
		node.getLeft().accept(getTraverser());
		printer.print(" or ");
		node.getRight().accept(getTraverser());
	}

	@Override
	public void traverse(ASTIsExpression node) {
		node.getLeft().accept(getTraverser());
		printer.print(" is ");
		node.getRight().accept(getTraverser());
	}

	@Override
	public void traverse(ASTNotExpression node) {
		printer.print(" not ");
		node.getExpression().accept(getTraverser());
	}

	@Override
	public void traverse(ASTInExpression node) {
		node.getLeft().accept(getTraverser());
		printer.print("in");
		node.getRight().accept(getTraverser());
	}

	@Override
	public void traverse(ASTNotInExpression node) {
		node.getLeft().accept(getTraverser());
		printer.print(" not in ");
		node.getRight().accept(getTraverser());
	}

	// lambda expression
	@Override
	public void traverse(ASTLambdaExpression node) {
		printer.print("lambda");
		if (!node.getFunctionParameters().isEmptyFunctionParameters()) {
			printer.print(" ");
		}
		node.getFunctionParameters().accept(getTraverser());
		printer.print(": ");
		node.getExpression().accept(getTraverser());
	}

	@Override
	public void traverse(ASTAppliedLambdaExpression node) {
		printer.print("(");
		node.getLambdaExpression().accept(getTraverser());
		printer.print(")(");
		node.getExpression().accept(getTraverser());
		printer.print(")");
	}

	/*===========================Classes======================================*/

	// class declaration statement
	@Override
	public void traverse(ASTClassDeclaration node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.print("class ");
		printer.print(node.getName());
		if (node.isPresentSuperClass()) {
			printer.print("(");
			printer.print(node.getSuperClass());
			printer.print(")");
		}
		printer.print(":");
		printer.println();
		node.getClassStatementBlock().accept(getTraverser());
		CommentPrettyPrinter.printPostComments(node, printer);
	}

	// class statement block
	@Override
	public void traverse(ASTClassStatementBlock node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		ASTClassStatementBlockBody blockBody = node.getClassStatementBlockBody();
		printer.indent();
		for (ASTClassStatement statement : blockBody.getClassStatementList()) {
			statement.accept(getTraverser());
		}
		printer.unindent();

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	// class function declaration
	@Override
	public void traverse(ASTClassFunctionDeclaration node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.print("def ");
		printer.print(node.getName());
		printer.print("(");
		node.getClassFunctionParameters().accept(getTraverser());
		printer.print("):");
		printer.println();
		node.getStatementBlock().accept(getTraverser());

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	// class attributes
	@Override
	public void traverse(ASTClassAttributes node) {

		CommentPrettyPrinter.printPreComments(node, printer);

		node.getVariableDeclaration().accept(getTraverser());

		printer.println();

		CommentPrettyPrinter.printPostComments(node, printer);

	}

	// class function parameters
	@Override
	public void traverse(ASTClassFunctionParameters node) {
		List<ASTFunctionParameter> functionParameterList = node.getFunctionParameterList();
		for (int i = 0; i < functionParameterList.size(); i++) {
			ASTFunctionParameter argument = functionParameterList.get(i);
			if(i != 0) {
				printer.print(", ");
			}
			argument.accept(getTraverser());
		}
	}
}
