package de.monticore.sipython;

import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTMinusExpressionCoCo;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTModuloExpressionCoCo;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTPlusExpressionCoCo;
import de.monticore.python._ast.ASTPythonScript;
import de.monticore.sipython._cocos.*;
import de.monticore.siunits.SIUnitsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class SIPythonCocoCheckerTest extends AbstractTest {

	@Before
	public void init() {
		Log.init();
		Log.enableFailQuick(false);
		SIPythonMill.reset();
		SIPythonMill.init();
		SIUnitsMill.initializeSIUnits();
	}

	private void typeCheckCoCo(String input, boolean expectedError) {
		Log.getFindings().clear();
		ASTPythonScript model = parseModel(input);
		SIPythonMill.scopesGenitorDelegator().createFromAST(model);
		SIPythonCoCoChecker checker = new SIPythonCoCoChecker();
		checker.addCoCo(SIPythonSIUnitConversionTypeCheckCoco.getCoCo());
		checker.addCoCo(new PythonFunctionDeclarationInFunctionCoco());
		checker.addCoCo(new PythonFunctionDeclarationInForStatementCoco());
		checker.addCoCo(new PythonFunctionDeclarationInWhileStatementCoco());
		checker.addCoCo(new PythonFunctionDeclarationInIfStatementCoco());
	//	checker.addCoCo(new PythonFunctionCallArgumentsSizeCoco());
		checker.addCoCo(new PythonFunctionParameterDuplicateNameCoco());
		checker.addCoCo((CommonExpressionsASTPlusExpressionCoCo) PythonCommonExpressionsTypeCheckCoco.getCoco());
		checker.addCoCo((CommonExpressionsASTMinusExpressionCoCo) PythonCommonExpressionsTypeCheckCoco.getCoco());
		checker.addCoCo((CommonExpressionsASTModuloExpressionCoCo) PythonCommonExpressionsTypeCheckCoco.getCoco());
		checker.addCoCo(new PythonFunctionParameterDuplicateNameCoco());


		try {
			checker.checkAll(model);
		} catch (Exception e) {
			fail(e.getMessage());
		}

		assertEquals(expectedError, Log.getErrorCount() > 0);
	}

	@Test
	public void parseSimplePython() {
		String model = "python/simple_python.sipy";
		typeCheckCoCo(model, false);
	}

	@Test
	public void parseUnitScript() {
		String model = "unit_script.sipy";
		typeCheckCoCo(model, false);
	}

	@Test
	public void parseSimpleSkriptWithTypeCoCoError() {
		String model = "sipythonWithTypeCoCoError.sipy";
		typeCheckCoCo(model, true);
	}

	@Test
	public void parseSimpleSkriptWithFunctionInsideFunctionCoCoError() {
		String model = "python/pythonFunctionInsideStatementBlockCocoError.sipy";
		typeCheckCoCo(model, true);
	}

	/*
	@Test
	public void parseSIPythonCommonExpressionsTypeCocoError() {
		String model = "sipythonExpressionsTypeCocoError.sipy";
		typeCheckCoCo(model, true);
	}

	 */
}
