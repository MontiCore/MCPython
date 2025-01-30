package de.monticore.python._cocos;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTCallExpressionCoCo;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.python._ast.ASTArgument;
import de.monticore.python._ast.ASTArguments;
import de.monticore.python._ast.ASTOptionalFunctionParameter;
import de.monticore.python._symboltable.IPythonScope;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

public class PythonFunctionArgumentSizeCoco implements CommonExpressionsASTCallExpressionCoCo {

	@Override
	public void check(ASTCallExpression node) {
		List<ASTArgument> args = ((ASTArguments)node.getArguments()).getArgumentList();
		Optional<FunctionSymbol> optionalFunctionSymbol = getFunctionSymbol(node.getExpression());
		if (optionalFunctionSymbol.isPresent()) {
			FunctionSymbol symbol = optionalFunctionSymbol.get();

			// can not check parameter size for builtin functions
			if (symbol.getEnclosingScope().getName().equals("__builtin__")) {
				return;
			}

			if (args.size() < symbol.getParameterList().stream().filter(p -> !(p.getAstNode() instanceof ASTOptionalFunctionParameter)).count()) {
				Log.error("Too few arguments for function " + symbol.getName() + " " + node.get_SourcePositionStart());
			}

			if (args.size() > symbol.getParameterList().size()) {
				Log.error("Too many arguments for function " + symbol.getName() + " " + node.get_SourcePositionStart());
			}
		}
	}

	private Optional<FunctionSymbol> getFunctionSymbol(ASTExpression node) {
		if (node instanceof ASTNameExpression) {
			return ((IPythonScope) node.getEnclosingScope()).resolveFunction(((ASTNameExpression) node).getName());
		} else if (node instanceof ASTFieldAccessExpression) {
			return ((IPythonScope) node.getEnclosingScope()).resolveFunction(((ASTFieldAccessExpression) node).getName());
		}
		return Optional.empty();
	}
}
