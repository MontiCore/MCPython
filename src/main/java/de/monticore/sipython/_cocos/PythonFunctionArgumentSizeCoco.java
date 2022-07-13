package de.monticore.sipython._cocos;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTCallExpressionCoCo;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.python._symboltable.IPythonScope;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

public class PythonFunctionArgumentSizeCoco implements CommonExpressionsASTCallExpressionCoCo {

	@Override
	public void check(ASTCallExpression node) {
		List<ASTExpression> args = node.getArguments().getExpressionList();
		Optional<FunctionSymbol> optionalFunctionSymbol = getFunctionSymbol(node.getExpression());
		if (optionalFunctionSymbol.isPresent()) {
			FunctionSymbol symbol = optionalFunctionSymbol.get();

			if (args.size() != symbol.getParameterList().size()) {
				Log.error("Invalid argument size for function " + symbol.getName() + " " + node.get_SourcePositionStart());
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
