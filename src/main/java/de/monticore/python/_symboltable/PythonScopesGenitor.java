package de.monticore.python._symboltable;

import de.monticore.sipython._symboltable.ISIPythonArtifactScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;


public class PythonScopesGenitor extends PythonScopesGenitorTOP {

	public PythonScopesGenitor() {
		super();
	}

	@Override
	public void visit(de.monticore.python._ast.ASTVariableDeclaration node) {
		Optional<IPythonScope> scope = getCurrentScope();
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

}
