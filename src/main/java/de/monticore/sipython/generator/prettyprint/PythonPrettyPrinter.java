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
		for (String name : node.getNameList()) {
			if (first) {
				first = false;
			} else {
				printer.print(", ");
			}
			printer.print(name);

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

		for(int i = 0; i < node.getElifStatementList().size(); i++) {
			ASTExpression condition = node.getElifCondition(i);
			ASTStatementBlock statementBlock = node.getElifStatement(i);

			printer.print("elif ");
			condition.accept(getTraverser());
			printer.print(":");
			printer.println();
			statementBlock.accept(getTraverser());
		}

		if (node.isPresentElseStatement()) {
			printer.print("else:");
			printer.println();
			node.getElseStatement().accept(getTraverser());
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
			if (statement.isPresentName()) printer.print(statement.getName());
			printer.println(":");
			statement.getStatementBlock().accept(getTraverser());
		}

		if (node.isPresentElseStatement()) {
			printer.println("else:");
			node.getElseStatement().accept(getTraverser());
		}

		if (node.isPresentFinallyStatement()) {
			printer.println("finally:");
			node.getFinallyStatement().accept(getTraverser());
		}
	}

	// with open file statement
	@Override
	public void traverse (ASTWithOpenFileStatement node){
		CommentPrettyPrinter.printPreComments(node, printer);
		printer.print(" with ");
		node.getOpenExpression().accept(getTraverser());
		printer.print(" as ");
		printer.print(node.getNameFile());
		for (int i = 0; i < node.getOpenExpressionRecursionList().size(); i++){
			printer.print(" , ");
			node.getOpenExpressionRecursion(i).accept(getTraverser());
			printer.print(" as ");
			printer.print(node.getNameFileRecursion(i));
		}
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
		if (node.isPresentExpression()) {
			printer.print(" ");
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
	public void traverse (ASTStringLiteralPython node){
		printer.print("\"");
		printer.print(node.getSource());
		printer.print("\"");

	}

	// boolean literals for python
	@Override
	public void traverse (ASTBooleanLiteralPython node){
		if(node.getSource() == 1){
			printer.print("True");
		}else {
			printer.print("False");
		}
	}


	/*======================Expressions===========================================*/

	// ternary-operator expression
	@Override
	public void traverse(ASTTernaryOperatorExpression node){
		CommentPrettyPrinter.printPreComments(node, printer);
		node.getThenExpression().accept(getTraverser());
		for(int i = 0; i < node.getConditionList().size(); i++){
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
	public void traverse(ASTANDExpression node) {
		node.getLeft().accept(getTraverser());
		printer.print("and");
		node.getRight().accept(getTraverser());
	}
	@Override
	public void traverse(ASTORExpression node) {
		node.getLeft().accept(getTraverser());
		printer.print("or");
		node.getRight().accept(getTraverser());
	}
	@Override
	public void traverse(ASTISExpression node) {
		node.getLeft().accept(getTraverser());
		printer.print("is");
		node.getRight().accept(getTraverser());
	}
	@Override
	public void traverse(ASTNOTExpression node) {
		printer.print("not");
		node.getExpression().accept(getTraverser());
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
	public void traverse(ASTClassDeclaration node){
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
	public void traverse(ASTClassAttributes node){

		CommentPrettyPrinter.printPreComments(node, printer);

		node.getVariableDeclaration().accept(getTraverser());

		printer.println();

		CommentPrettyPrinter.printPostComments(node, printer);

	}

	//class self parameter
	@Override
	public void traverse(ASTSelfParameter node) {
		printer.print(node.getName());
	}
	// class function parameters

	@Override
	public void traverse(ASTClassFunctionParameters node) {
		node.getSelfParameter().accept(getTraverser());
		for (ASTFunctionParameter argument : node.getFunctionParameterList()) {
			printer.print(", ");
			argument.accept(getTraverser());
		}
	}
}
