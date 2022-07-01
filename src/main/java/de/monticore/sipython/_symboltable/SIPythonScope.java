package de.monticore.sipython._symboltable;

import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class SIPythonScope extends SIPythonScopeTOP {

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

	@Override
	public  Optional<VariableSymbol> resolveVariable (String name) {
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
/*
	@Override
	public <T extends ISymbol> Optional<T> getResolvedOrThrowException(final Collection<T> resolved) {
		if (resolved.size() == 1) {
			return Optional.of(resolved.iterator().next());
		} else if (resolved.size() > 1) {
			return Optional.of(((List<T>) resolved).get(resolved.size() - 1));
		}

		return Optional.empty();
	}

 */


}
