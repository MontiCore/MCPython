package de.monticore.sipython;

import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTBooleanNotExpressionCoCo;
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
		checker.addCoCo(new PythonExpressionCoco());
		checker.addCoCo(new PythonLambdaDuplicateParameterNameCoco());
		checker.addCoCo(((PythonASTPythonScriptCoCo) new PythonDuplicateFunctionCoco()));
		checker.addCoCo(new CallExpressionAfterFunctionDeclarationCoco());
		checker.addCoCo(((CommonExpressionsASTBooleanNotExpressionCoCo) new JavaBooleanExpressionCoco()));
		// checker.addCoCo((CommonExpressionsASTPlusExpressionCoCo) SIPythonCommonExpressionsTypeCheckCoco.getCoco());

		checker.checkAll(ast);
	}
}
