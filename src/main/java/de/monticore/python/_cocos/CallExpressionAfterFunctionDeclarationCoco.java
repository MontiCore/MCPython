package de.monticore.python._cocos;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTCallExpressionCoCo;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.python._ast.ASTOptionalFunctionParameter;
import de.monticore.python._symboltable.IPythonScope;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

public class CallExpressionAfterFunctionDeclarationCoco implements CommonExpressionsASTCallExpressionCoCo {
	@Override
	public void check(ASTCallExpression node) {
		Optional<FunctionSymbol> optionalFunctionSymbol = getFunctionSymbol(node);
		if (optionalFunctionSymbol.isPresent()) {
			FunctionSymbol symbol = optionalFunctionSymbol.get();

			SourcePosition symbolPosition = symbol.getAstNode().get_SourcePositionStart();
			SourcePosition nodePosition = node.get_SourcePositionStart();

			if (symbolPosition.getFileName().isPresent() && nodePosition.getFileName().isPresent()) {
				// different files, nothing to check
				if (!symbolPosition.getFileName().get().equals(nodePosition.getFileName().get())) {
					return;
				}
			}

			if (symbolPosition.compareTo(nodePosition) >= 0) {
				Log.error("Call of function '" + symbol.getName() + "' before declaration " + nodePosition);
			}
		}
	}

	private Optional<FunctionSymbol> getFunctionSymbol(ASTCallExpression node) {
		if (node.getExpression() instanceof ASTNameExpression) {
			return ((IPythonScope) node.getEnclosingScope()).resolveFunction(((ASTNameExpression) node.getExpression()).getName());
		}
		return Optional.empty();
	}
}
