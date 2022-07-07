package de.monticore.sipython;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._cocos.PythonASTFunctionDeclarationCoCo;
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
		ASTPythonScript model = parseModelAndReturnASTPythonScript(input);
		SIPythonMill.scopesGenitorDelegator().createFromAST(model);
		SIPythonCoCoChecker checker = new SIPythonCoCoChecker();
		// checker.addCoCo(SIPythonSIUnitConversionTypeCheckCoco.getCoCo());
		checker.addCoCo((PythonASTFunctionDeclarationCoCo) new PythonFunctionDeclarationInStatementBlockCheck());
		checker.addCoCo(new PythonFunctionParameterDuplicateNameCoco());
		// checker.addCoCo((CommonExpressionsASTPlusExpressionCoCo) SIPythonCommonExpressionsTypeCheckCoco.getCoco());


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

	/*
	@Test
	public void parseSimpleSkriptWithTypeCoCoError() {
		String model = "cocos/sipythonWithTypeCoCoError.sipy";
		typeCheckCoCo(model, true);
	}

	 */

	@Test
	public void parseSimpleSkriptWithFunctionInsideFunctionCoCoError() {
		String model = "cocos/pythonFunctionInsideStatementBlockCocoError.sipy";
		typeCheckCoCo(model, true);
	}

	@Test
	public void parsePythonDuplicateFunctionParameterCocoError() {
		String model = "cocos/pythonDuplicateFunctionParameterCocoError.sipy";
		typeCheckCoCo(model, true);
	}

	/*
	@Test
	public void parseSIPythonCommonExpressionsTypeCocoError() {
		String model = "cocos/sipythonExpressionsTypeCocoError.sipy";
		typeCheckCoCo(model, true);
	}

	 */
}
