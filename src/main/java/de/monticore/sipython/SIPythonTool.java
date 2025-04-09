package de.monticore.sipython;

import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTDecPrefixExpressionCoCo;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTBooleanNotExpressionCoCo;
import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._cocos.*;
import de.monticore.sipython._cocos.SIPythonCoCoChecker;

public class SIPythonTool extends SIPythonToolTOP {

	@Override
	public void runDefaultCoCos(ASTPythonScript ast) {
		SIPythonCoCoChecker checker = new SIPythonCoCoChecker();
		checker.addCoCo(new UseClassAfterClassDeclarationCoco());
		checker.addCoCo((PythonASTIfStatementCoCo) new PythonFunctionOrClassDeclarationInStatementBlockCoco());
		checker.addCoCo(new PythonFunctionDuplicateParameterNameCoco());
		checker.addCoCo(new PythonFunctionArgumentSizeCoco());
		checker.addCoCo(new PythonVariableOrFunctionOrClassExistsCoco());
		checker.addCoCo(new PythonExpressionCoco());
		checker.addCoCo(new PythonLambdaDuplicateParameterNameCoco());
		checker.addCoCo(((PythonASTPythonScriptCoCo) new PythonDuplicateFunctionAndClassCoco()));
		checker.addCoCo(new CallExpressionAfterFunctionDeclarationCoco());
		checker.addCoCo(((CommonExpressionsASTBooleanNotExpressionCoCo) new JavaBooleanExpressionCoco()));
		checker.addCoCo(new SelfParameterIsFirstCoCo());

		checker.addCoCo((AssignmentExpressionsASTDecPrefixExpressionCoCo) new NoInvalidExpressionUsedCoCo());

		checker.checkAll(ast);
	}
}
