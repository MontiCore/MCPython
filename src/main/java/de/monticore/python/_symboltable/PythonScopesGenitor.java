package de.monticore.python._symboltable;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.python._ast.*;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


public class PythonScopesGenitor extends PythonScopesGenitorTOP {

	private final List<String> pythonKeywords = new LinkedList<>();


	public PythonScopesGenitor() {
		super();
		this.initPythonKeywords();
	}

	private void initPythonKeywords(){
		pythonKeywords.addAll(List.of(
				"False",
				"None",
				"True",
				"and",
				"as",
				"assert",
				"async",
				"await",
				"break",
				"class",
				"continue",
				"def",
				"del",
				"elif",
				"else",
				"except",
				"finally",
				"for",
				"from",
				"global",
				"if",
				"import",
				"in",
				"is",
				"lambda",
				"nonlocal",
				"not",
				"or",
				"pass",
				"raise",
				"return",
				"try",
				"while",
				"with",
				"yield"
		));
	}

	private void checkNameIsKeyword(ISymbol symbol) {
		this.checkNameIsKeyword(symbol, symbol.getSourcePosition());
	}

	private void checkNameIsKeyword(ISymbol symbol, SourcePosition position) {
		if (this.pythonKeywords.contains(symbol.getName())) {
			Log.error("Symbol '" + symbol.getName() + "' must not be a keyword " + position);
		}
	}

	@Override
	public void visit(de.monticore.python._ast.ASTVariableDeclaration node) {
		if (getCurrentScope().isPresent()) {
			Optional<VariableSymbol> variableSymbolOptional = getCurrentScope().get().resolveVariable(node.getName());
			if (variableSymbolOptional.isPresent()) {
				node.setSymbol(variableSymbolOptional.get());
				node.setEnclosingScope(variableSymbolOptional.get().getEnclosingScope());
				initVariableHP1(node.getSymbol());
			} else {
				super.visit(node);
			}
		} else {
			Log.warn("0xA5021x71517 Symbol cannot be added to current scope, since no scope exists.");
		}
	}

	@Override
	public void endVisit(ASTVariableDeclaration node) {
		super.endVisit(node);
		this.checkNameIsKeyword(node.getSymbol());
	}

	@Override
	public void endVisit(ASTSimpleFunctionDeclaration node) {
		super.endVisit(node);
		this.checkNameIsKeyword(node.getSymbol());
	}

	@Override
	public void endVisit(ASTClassFunctionDeclaration node) {
		super.endVisit(node);
		this.checkNameIsKeyword(node.getSymbol());
	}

	// create class field symbols via the __init__ method
	@Override
	public void visit(ASTClassDeclaration node) {
		super.visit(node);

		for (ASTClassStatement classStatement : node.getClassStatementBlock().getClassStatementBlockBody().getClassStatementList()) {
			if (classStatement instanceof ASTClassFunctionDeclaration) {
				ASTClassFunctionDeclaration functionDeclaration = ((ASTClassFunctionDeclaration) classStatement);

				if (functionDeclaration.getName().equals("__init__")) {
					for (ASTStatement functionStatement : functionDeclaration.getStatementBlock().getStatementBlockBody().getStatementList()) {
						if (functionStatement instanceof ASTExpressionStatement) {
							ASTExpressionStatement expressionStatement = ((ASTExpressionStatement) functionStatement);
              for (ASTExpression expr : expressionStatement.getExpressionList()) {
                if (expr instanceof ASTAssignmentExpression) {
                  ASTAssignmentExpression assignmentExpression = (ASTAssignmentExpression) expr;

                  if (assignmentExpression.getLeft() instanceof ASTFieldAccessExpression) {
                    String name = ((ASTFieldAccessExpression) assignmentExpression.getLeft()).getName();
                    FieldSymbol symbol = new FieldSymbol(name);
                    this.checkNameIsKeyword(symbol, assignmentExpression.get_SourcePositionStart());
                    node.getSpannedScope().add(symbol);
                  }
                }
              }
						}
					}
				}
			}
		}
	}

	@Override
	public void endVisit(ASTClassDeclaration node) {
		super.endVisit(node);
		this.checkNameIsKeyword(node.getSymbol());
	}

}
