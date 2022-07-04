package de.monticore.sipython._cocos;

import de.monticore.python._ast.ASTFunctionCall;
import de.monticore.python._cocos.PythonASTFunctionCallCoCo;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.se_rwth.commons.logging.Log;

public class PythonFunctionCallArgumentsSizeCoco implements PythonASTFunctionCallCoCo {

	@Override
	public void check(ASTFunctionCall node) {

		if (node.isPresentNameSymbol()) {
			FunctionSymbol functionSymbol = node.getNameSymbol();

			if (functionSymbol.getParameterList().size() > node.getExpressionList().size()) {
				Log.error( "Too few arguments for function " + functionSymbol.getName() + node.get_SourcePositionStart());
			} else if (functionSymbol.getParameterList().size() < node.getExpressionList().size()) {
				Log.error( "Too many arguments for function " + functionSymbol.getName() + node.get_SourcePositionStart());
			}
		}
	}
}
