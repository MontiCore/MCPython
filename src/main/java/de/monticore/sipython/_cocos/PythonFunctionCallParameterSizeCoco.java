package de.monticore.sipython._cocos;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTCallExpressionCoCo;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

public class PythonFunctionCallParameterSizeCoco implements CommonExpressionsASTCallExpressionCoCo {

	@Override
	public void check(ASTCallExpression node) {
		List<ASTExpression> args = node.getArguments().getExpressionList();
		Optional<ISymbol> symbol = node.getDefiningSymbol();

		if (symbol.isPresent()) {
			FunctionSymbol functionSymbol = ((FunctionSymbol) symbol.get());
			if (args.size() != functionSymbol.getParameterList().size()) {
				Log.error("Invalid argument size for function " + functionSymbol.getName() + " " + node.get_SourcePositionStart());
			}
		}
	}
}
