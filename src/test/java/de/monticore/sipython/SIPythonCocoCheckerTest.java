package de.monticore.sipython;

import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTPlusExpressionCoCo;
import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._cocos.PythonASTFunctionDeclarationCoCo;
import de.monticore.sipython._cocos.*;
import de.monticore.siunits.SIUnitsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

public class SIPythonCocoCheckerTest extends AbstractTest {

	@Before
	public void init() {
		Log.init();
		Log.enableFailQuick(false);
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

	@Test
	public void checkSIPythonCommonExpressionsTypeCheckCoco() {
		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"x = 5 dm/h + 3 km/h"
		);

		parseCodeStringAndCheckCoCosAndExpectError(
				"x = 5 dm/h + 3 °C"
		);

		//checks that all distinct pairs of base units are not allowed in a common expression together
		performCocoChecksForAllBaseUnits(this::parseCommonExpressionsWithTwoUnitsAndCheckCoCosAndExpectError);
	}

	@Test
	public void checkSIPythonSIUnitConversionTypeCheckCoco() {
		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"x = dm/h(3 km/h)"
		);

		parseCodeStringAndCheckCoCosAndExpectError(
				"x = dm/h(3 °C)"
		);

		//checks that all distinct pairs of base units are not allowed in a unit conversion expression together
		performCocoChecksForAllBaseUnits(this::parseSIUnitConversionTypeCheckAndCheckCoCosAndExpectError);
	}

//	---------------------------------------------------------------
//	Tests for scripts from files.
//	---------------------------------------------------------------

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

	private void parseFromModelFileAndCheckCoCosAndExpectFail(String modelFileName) {
		Optional<ASTPythonScript> astPythonScriptOptional = parseModelFromFileAndReturnASTPythonScript(modelFileName);
		checkCoCos(astPythonScriptOptional);
		assertTrue(Log.getErrorCount() > 0);
	}

	private void checkCoCos(Optional<ASTPythonScript> astPythonScriptOptional) {
		SIPythonMill.reset();
		SIPythonMill.init();
		SIUnitsMill.initializeSIUnits();

		Log.getFindings().clear();
		if(astPythonScriptOptional.isEmpty()) {
			fail("Failed to parse the model: The ASTTree is empty!");
		}
		ASTPythonScript model = astPythonScriptOptional.get();
		SIPythonMill.scopesGenitorDelegator().createFromAST(model);

		try {
			createSIPythonCoCoChecker().checkAll(model);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	private SIPythonCoCoChecker createSIPythonCoCoChecker() {
		SIPythonCoCoChecker siPythonCoCoChecker = new SIPythonCoCoChecker();
		siPythonCoCoChecker.addCoCo(SIPythonSIUnitConversionTypeCheckCoco.getCoCo());
		siPythonCoCoChecker.addCoCo((PythonASTFunctionDeclarationCoCo) new PythonFunctionDeclarationInStatementBlockCheck());
		siPythonCoCoChecker.addCoCo(new PythonFunctionParameterDuplicateNameCoco());
		siPythonCoCoChecker.addCoCo(new PythonFunctionArgumentSizeCoco());
		siPythonCoCoChecker.addCoCo(new PythonVariableOrFunctionExistsCoco());
		siPythonCoCoChecker.addCoCo((CommonExpressionsASTPlusExpressionCoCo) SIPythonCommonExpressionsTypeCheckCoco.getCoco());
		return siPythonCoCoChecker;
	}

	/**
	 * This method executes the given CoCoCheckWithTwoUnits on each possible distinct pair of si base units.
	 */
	private void performCocoChecksForAllBaseUnits(CoCoCheckWithTwoUnits coCoCheckWithTwoUnits) {
		String[] unitArray = {"s", "m", "kg", "A", "K", "mol", "cd"};

		Arrays.stream(unitArray).forEach(unit1 -> Arrays.stream(unitArray).forEach(unit2 -> {
			if(!unit1.equals(unit2)) {
				coCoCheckWithTwoUnits.check(unit1, unit2);
			}
		}));
	}

	/**
	 * This interface allows to specify lambda expressions for coco checking for two si unit strings.
	 */
	interface CoCoCheckWithTwoUnits {
		void check(String unit1, String unit2);
	}

	private void parseCommonExpressionsWithTwoUnitsAndCheckCoCosAndExpectError(String unit1, String unit2) {
		parseCodeStringAndCheckCoCosAndExpectError(
				"x = 5 " + unit1 + " + 3 " + unit2
		);
	}

	private void parseSIUnitConversionTypeCheckAndCheckCoCosAndExpectError(String unit1, String unit2) {
		parseCodeStringAndCheckCoCosAndExpectError(
				"x = " + unit1 + "( 3 " + unit2 + ")"
		);
	}
}
