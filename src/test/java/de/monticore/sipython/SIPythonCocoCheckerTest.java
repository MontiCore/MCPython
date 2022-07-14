package de.monticore.sipython;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._cocos.PythonASTFunctionDeclarationCoCo;
import de.monticore.sipython._cocos.*;
import de.monticore.siunits.SIUnitsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class SIPythonCocoCheckerTest extends AbstractTest {

	private SIPythonCoCoChecker checker;

	@Before
	public void init() {
		Log.init();
		Log.enableFailQuick(false);
		SIPythonMill.reset();
		SIPythonMill.init();
		SIUnitsMill.initializeSIUnits();
		checker = new SIPythonCoCoChecker();
//		 checker.addCoCo(SIPythonSIUnitConversionTypeCheckCoco.getCoCo());
		checker.addCoCo((PythonASTFunctionDeclarationCoCo) new PythonFunctionDeclarationInStatementBlockCheck());
		checker.addCoCo(new PythonFunctionParameterDuplicateNameCoco());
		checker.addCoCo(new PythonFunctionArgumentSizeCoco());
		checker.addCoCo(new PythonVariableOrFunctionExistsCoco());
//		 checker.addCoCo((CommonExpressionsASTPlusExpressionCoCo) SIPythonCommonExpressionsTypeCheckCoco.getCoco());
	}

//	---------------------------------------------------------------
//	Tests for single code snippets from strings.
//	---------------------------------------------------------------

	@Test
	public void checkPythonFunctionArgumentSizeCoco() {
		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"def calcVelocity(x):\n" +
						"    x++\n" +
						"calcVelocity(4)"
		);

		parseCodeStringAndCheckCoCosAndExpectError(
				"def calcVelocity(x):\n" +
						"    x++\n" +
						"calcVelocity(4,5)"
		);
	}

	@Test
	public void checkPythonFunctionDeclarationInStatementBlockCheck() {
		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"x = 1\n" +
				"if(x > 5):\n" +
				"    x++\n" +
				"def calcVelocity(x):\n" +
				"    x++\n"
		);

		parseCodeStringAndCheckCoCosAndExpectError(
				"x = 1\n" +
				"if(x > 5):\n" +
				"    def calcVelocity(x):\n" +
				"        x++\n"
		);
	}

	@Test
	public void checkPythonFunctionParameterDuplicateNameCoco() {
		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"def calcVelocity(x,y):\n" +
				"    x++"
		);

		parseCodeStringAndCheckCoCosAndExpectError(
				"def calcVelocity(x,x):\n" +
				"    x++"
		);
	}

	@Test
	public void checkPythonVariableOrFunctionExistsCoco() {
		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"x = 1\n" +
				"x++"
		);

		parseCodeStringAndCheckCoCosAndExpectError(
				"if(x > 5):" +
				"    x++"
		);
	}

//	---------------------------------------------------------------
//	Tests for scripts from files.
//	---------------------------------------------------------------

	@Test
	public void parseSimplePython() {
		String model = "python/simple_python.sipy";
		parseFromModelFileAndCheckCoCosAndExpectSuccess(model);
	}

	@Test
	public void parseUnitScript() {
		String model = "unit_script.sipy";
		parseFromModelFileAndCheckCoCosAndExpectSuccess(model);
	}

	@Test
	public void parseSimpleSkriptWithFunctionInsideFunctionCoCoError() {
		String model = "cocos/pythonFunctionInsideStatementBlockCocoError.sipy";
		parseFromModelFileAndCheckCoCosAndExpectFail(model);
	}

	@Test
	public void parsePythonDuplicateFunctionParameterCocoError() {
		String model = "cocos/pythonDuplicateFunctionParameterCocoError.sipy";
		parseFromModelFileAndCheckCoCosAndExpectFail(model);
	}

	@Test
	public void parsePythonVariableOrFunctionNotExistsCocoError() {
		String model = "cocos/pythonVariableOrFunctionExistsCocoError.sipy";
		parseFromModelFileAndCheckCoCosAndExpectFail(model);
	}

	@Test
	public void parsePythonFunctionArgumentSizeCocoError() {
		String model = "cocos/pythonFunctionArgumentSizeCocoError.sipy";
		parseFromModelFileAndCheckCoCosAndExpectFail(model);
	}

	private void parseCodeStringAndCheckCoCosAndExpectSuccess(String codeString) {
		Optional<ASTPythonScript> astPythonScriptOptional = parseModelFromStringAndReturnASTPythonScript(codeString);
		checkCoCos(astPythonScriptOptional);
		assertEquals(0, Log.getErrorCount());
	}

	private void parseCodeStringAndCheckCoCosAndExpectError(String codeString) {
		Optional<ASTPythonScript> astPythonScriptOptional = parseModelFromStringAndReturnASTPythonScript(codeString);
		checkCoCos(astPythonScriptOptional);
		assertTrue(Log.getErrorCount() > 0);
	}

	private void parseFromModelFileAndCheckCoCosAndExpectSuccess(String modelFileName) {
		Optional<ASTPythonScript> astPythonScriptOptional = parseModelFromFileAndReturnASTPythonScript(modelFileName);
		checkCoCos(astPythonScriptOptional);
		assertEquals(0, Log.getErrorCount());
	}

	private void parseFromModelFileAndCheckCoCosAndExpectFail(String modelFileName) {
		Optional<ASTPythonScript> astPythonScriptOptional = parseModelFromFileAndReturnASTPythonScript(modelFileName);
		checkCoCos(astPythonScriptOptional);
		assertTrue(Log.getErrorCount() > 0);
	}

	private void checkCoCos(Optional<ASTPythonScript> astPythonScriptOptional) {
		Log.getFindings().clear();
		if(astPythonScriptOptional.isEmpty()) {
			fail("Failed to parse the model: The ASTTree is empty!");
		}
		ASTPythonScript model = astPythonScriptOptional.get();
		SIPythonMill.scopesGenitorDelegator().createFromAST(model);

		try {
			checker.checkAll(model);
		} catch (Exception e) {
			fail(e.getMessage());
		}

		//this resets the symbol table beside others, to not affect further coco checkings.
		SIPythonMill.reset();
		SIPythonMill.init();
	}
}
