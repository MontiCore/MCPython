package de.monticore.sipython._symboltable;

import de.monticore.python._symboltable.PythonClassSymbol;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SIPythonScope extends SIPythonScopeTOP {

	private List<String> pythonKeywords = new LinkedList<>();

	public SIPythonScope() {
		super();
	}

	public SIPythonScope(boolean isShadowingScope) {
		super(isShadowingScope);
	}

	public SIPythonScope(ISIPythonScope enclosingScope) {
		this(enclosingScope, false);
	}

	public SIPythonScope(ISIPythonScope enclosingScope, boolean isShadowingScope) {
		super(enclosingScope,isShadowingScope);
	}

	public  Optional<VariableSymbol> resolveVariable(String name) {
		List<VariableSymbol> symbols = resolveVariableMany(name);
		if (symbols.size() == 1) {
			return Optional.of(symbols.iterator().next());
		}	else if (symbols.size() > 1) {
			Iterator<VariableSymbol> iterator = symbols.iterator();
			VariableSymbol current;
			do {
				current = iterator.next();
			} while (current.getType() == null && iterator.hasNext());

			return Optional.of(current);
		}

		return Optional.empty();
	}

	private void checkNameIsKeyword(ISymbol symbol) {
		if (this.pythonKeywords.contains(symbol.getName())) {
			Log.error("Symbol '" + symbol.getName() + "' must not be a keyword ");
			Log.error(String.valueOf(symbol.getAstNode().get_SourcePositionStart()));
		}
	}

	@Override
	public void add(PythonClassSymbol symbol) {
		super.add(symbol);
		this.checkNameIsKeyword(symbol);
	}

	@Override
	public void add(OOTypeSymbol symbol) {
		super.add(symbol);
		this.checkNameIsKeyword(symbol);
	}

	@Override
	public void add(FieldSymbol symbol) {
		super.add(symbol);
		this.checkNameIsKeyword(symbol);
	}

	@Override
	public void add(MethodSymbol symbol) {
		super.add(symbol);
		this.checkNameIsKeyword(symbol);
	}

	@Override
	public void add(VariableSymbol symbol) {
		super.add(symbol);
		this.checkNameIsKeyword(symbol);
	}

	@Override
	public void add(FunctionSymbol symbol) {
		super.add(symbol);
		this.checkNameIsKeyword(symbol);
	}
}
