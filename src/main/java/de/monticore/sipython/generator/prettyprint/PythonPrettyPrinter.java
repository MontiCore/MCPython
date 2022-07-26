/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sipython.generator.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.python._ast.*;
import de.monticore.python._visitor.PythonHandler;
import de.monticore.python._visitor.PythonTraverser;
import de.monticore.python._visitor.PythonVisitor2;
import de.monticore.sipython.types.check.DeriveSymTypeOfSIPython;
import de.monticore.siunits.utility.Converter;
import de.monticore.siunits.utility.UnitPrettyPrinter;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsArtifactScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.tf.odrulegeneration._ast.ASTCondition;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types.check.TypeCalculator;

import javax.measure.converter.UnitConverter;
import javax.measure.unit.Unit;
import java.util.Optional;

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

		boolean first = true;
		for (String name : node.getNameList()) {
			if (first) {
				first = false;
			} else {
				printer.print(", ");
			}
			printer.print(name);

		}
		node.getEOL().accept(getTraverser());
	}

	@Override
	public void traverse(ASTLocalVariableDeclarationStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		node.getVariableDeclaration().accept(getTraverser());
		node.getEOL().accept(getTraverser());

		CommentPrettyPrinter.printPostComments(node, printer);
	}

	@Override
	public void traverse(ASTVariableDeclaration node) {
		printer.print(node.getName());
		printer.print(" = ");
		node.getVariableInit().accept(getTraverser());
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

	@Override
	public void traverse(ASTTernaryOperatorInit node){
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

	@Override
	public void traverse(ASTClassAttributes node){

		CommentPrettyPrinter.printPreComments(node, printer);

		node.getVariableDeclaration().accept(getTraverser());

		printer.println();

		CommentPrettyPrinter.printPostComments(node, printer);

	}

	@Override
	public void traverse(ASTClassFunctionParameters node) {
		node.getSelfParameter().accept(getTraverser());
		for (ASTFunctionParameter argument : node.getFunctionParameterList()) {
			printer.print(", ");
			argument.accept(getTraverser());
		}
	}

	@Override
	public void traverse(ASTSelfParameter node) {
		printer.print(node.getName());
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

	@Override
	public void traverse(ASTAssertStatement node) {
		CommentPrettyPrinter.printPreComments(node, printer);

		printer.print("assert ");
		node.getCondition().accept(getTraverser());
		printer.print(", ");
		printer.print("\"" + node.getErrorMessage() + "\"");
		node.getEOL().accept(getTraverser());

		CommentPrettyPrinter.printPostComments(node, printer);
	}
}
