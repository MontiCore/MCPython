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
/*
	public  Optional<VariableSymbol> resolveVariable(VariableSymbol currentSymbol, String name) {
		List<VariableSymbol> symbols = resolveVariableMany(name);
		if (symbols.size() == 1) {
			return Optional.of(symbols.iterator().next());
		}	else if (symbols.size() > 1) {
			Iterator<VariableSymbol> iterator = symbols.iterator();
			VariableSymbol current = null;
			VariableSymbol previous;
			do {
				previous = current;
				current = iterator.next();
			} while (iterator.hasNext() && !current.equals(currentSymbol));

			return Optional.of(previous);
		}

		return Optional.empty();
	}


	@Override
	public  List<VariableSymbol> resolveVariableMany(boolean foundSymbols, String name, AccessModifier modifier,
																									 Predicate<VariableSymbol> predicate) {
		if (isVariableSymbolsAlreadyResolved()) {
			return new ArrayList<>();
		}

		// (1) resolve symbol locally. During this, the 'already resolved' flag is set to true,
		// to prevent resolving cycles caused by cyclic symbol adapters
		setVariableSymbolsAlreadyResolved(true);
		final List<VariableSymbol> resolvedSymbols = this.resolveVariableLocallyMany(foundSymbols, name, modifier, predicate);

		foundSymbols = foundSymbols | resolvedSymbols.size() > 0;
		setVariableSymbolsAlreadyResolved(false);

		final String resolveCall = "resolveMany(\"" + name + "\", \"" + "VariableSymbol"
				+ "\") in scope \"" + (isPresentName() ? getName() : "") + "\"";
		Log.trace("START " + resolveCall + ". Found #" + resolvedSymbols.size() + " (local)", "");

		// (2) continue with enclosingScope, if either no symbol has been found yet or this scope is non-shadowing
		final List<VariableSymbol> resolvedFromEnclosing = continueVariableWithEnclosingScope(foundSymbols, name, modifier, predicate);

		// (3) unify results
		resolvedSymbols.addAll(resolvedFromEnclosing);
		Log.trace("END " + resolveCall + ". Found #" + resolvedSymbols.size(), "");

		return resolvedSymbols;
	}

	@Override
	public Optional<VariableSymbol> filterVariable(String name, LinkedListMultimap<String, VariableSymbol> symbols)

	{

		final LinkedList<VariableSymbol> resolvedSymbols = new LinkedList<>();

		final String simpleName = de.se_rwth.commons.Names.getSimpleName(name);

		if (symbols.containsKey(simpleName)) {
			for (VariableSymbol symbol : symbols.get(simpleName)) {
				if (symbol.getName().equals(name) || symbol.getFullName().equals(name)) {
					resolvedSymbols.add(symbol);
				}
			}
		}

		return getResolvedOrThrowException(resolvedSymbols);
	}

	@Override
	public List<VariableSymbol> resolveVariableLocallyMany(boolean foundSymbols, String name, AccessModifier modifier,
																													Predicate<VariableSymbol> predicate) {

		final LinkedList<VariableSymbol> resolvedSymbols = new LinkedList<>();

		try {
			Optional<VariableSymbol> resolvedSymbol = filterVariable(name, getVariableSymbols());

			resolvedSymbol.ifPresent(resolvedSymbols::addLast);
		} catch (de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException e) {
			for (VariableSymbol symbol : e.<VariableSymbol>getSymbols()) {
				resolvedSymbols.addLast(symbol);
			}
		}

		// add all symbols of sub kinds of the current kind
		resolvedSymbols.addAll(resolveVariableSubKinds(foundSymbols, name, modifier, predicate));

		// filter out symbols that are not included within the access modifier
		List<de.monticore.symbols.basicsymbols._symboltable.VariableSymbol> filteredSymbols = filterSymbolsByAccessModifier(modifier, resolvedSymbols);
		filteredSymbols = filteredSymbols.stream().filter(predicate).collect(java.util.stream.Collectors.toList());

		// if no symbols found try to find adapted one
		if (filteredSymbols.isEmpty()) {
			filteredSymbols.addAll(resolveAdaptedVariableLocallyMany(foundSymbols, name, modifier, predicate));
			filteredSymbols = filterSymbolsByAccessModifier(modifier, filteredSymbols);
			filteredSymbols = filteredSymbols.stream().filter(predicate).collect(java.util.stream.Collectors.toList());
		}
		return filteredSymbols;
	}

	@Override
	public <T extends ISymbol> List<T> filterSymbolsByAccessModifier(AccessModifier modifier, Collection<T> resolvedUnfiltered) {
		return resolvedUnfiltered.stream().filter(new IncludesAccessModifierSymbolPredicate(modifier)).collect(Collectors.toList());
	}
	*/


}
