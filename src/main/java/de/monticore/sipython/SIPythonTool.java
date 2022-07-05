package de.monticore.sipython;

import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTMinusExpressionCoCo;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTModuloExpressionCoCo;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTPlusExpressionCoCo;
import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._cocos.PythonASTIfStatementCoCo;
import de.monticore.sipython._cocos.*;

public class SIPythonTool extends SIPythonToolTOP {

	@Override
	public void runDefaultCoCos(ASTPythonScript ast) {
		SIPythonCoCoChecker checker = new SIPythonCoCoChecker();

		//checker.addCoCo(SIPythonSIUnitConversionTypeCheckCoco.getCoCo());
		checker.addCoCo((PythonASTIfStatementCoCo) new PythonFunctionDeclarationInStatementBlockCheck());
		checker.addCoCo(new PythonFunctionParameterDuplicateNameCoco());
		// checker.addCoCo((CommonExpressionsASTPlusExpressionCoCo) SIPythonCommonExpressionsTypeCheckCoco.getCoco());

		checker.checkAll(ast);
	}
}
