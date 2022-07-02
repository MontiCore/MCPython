package de.monticore.sipython._symboltable;

import de.monticore.sipython.types.check.DeriveSymTypeOfSIPython;
import de.monticore.types.check.TypeCalculator;

public class SIPythonScopesGenitor extends SIPythonScopesGenitorTOP {

	private TypeCalculator tc;

	public SIPythonScopesGenitor() {
		super();
		initTypeCheck();
	}

	private void initTypeCheck() {
		tc = new TypeCalculator(null, new DeriveSymTypeOfSIPython());
	}


}
