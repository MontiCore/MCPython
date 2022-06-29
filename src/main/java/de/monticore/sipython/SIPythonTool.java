package de.monticore.sipython;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.sipython._cocos.*;

public class SIPythonTool extends SIPythonToolTOP {

	@Override
	public void runDefaultCoCos(ASTPythonScript ast) {
		SIPythonCoCoChecker checker = new SIPythonCoCoChecker();

		checker.addCoCo(SIPythonSIUnitConversionTypeCheckCoco.getCoCo());
		checker.addCoCo(new PythonFunctionDeclarationInFunctionCoco());
		checker.addCoCo(new PythonFunctionDeclarationInForStatementCoco());
		checker.addCoCo(new PythonFunctionDeclarationInWhileStatementCoco());
		checker.addCoCo(new PythonFunctionDeclarationInIfStatementCoco());

		checker.checkAll(ast);
	}
}
