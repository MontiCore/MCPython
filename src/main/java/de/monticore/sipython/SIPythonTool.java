package de.monticore.sipython;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._cocos.*;
import de.monticore.sipython._cocos.*;

public class SIPythonTool extends SIPythonToolTOP {

	@Override
	public void runDefaultCoCos(ASTPythonScript ast) {
		SIPythonCoCoChecker checker = new SIPythonCoCoChecker();
		//checker.addCoCo(SIPythonSIUnitConversionTypeCheckCoco.getCoCo());
		checker.addCoCo((PythonASTIfStatementCoCo) new PythonFunctionDeclarationInStatementBlockCoco());
		checker.addCoCo(new PythonFunctionDuplicateParameterNameCoco());
		checker.addCoCo(new PythonFunctionArgumentSizeCoco());
		checker.addCoCo(new PythonVariableOrFunctionOrClassExistsCoco());
		// checker.addCoCo((CommonExpressionsASTPlusExpressionCoCo) SIPythonCommonExpressionsTypeCheckCoco.getCoco());

		checker.checkAll(ast);
	}
}
