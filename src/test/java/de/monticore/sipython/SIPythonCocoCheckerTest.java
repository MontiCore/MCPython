package de.monticore.sipython;

import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTBooleanNotExpressionCoCo;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTPlusExpressionCoCo;
import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._cocos.*;
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
						"    x+=1\n" +
						"calcVelocity(4)\n"
		);

		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"def calcVelocity(x, y=1):\n" +
						"    x+=1\n" +
						"calcVelocity(4)\n"
		);

		// too few arguments
		parseCodeStringAndCheckCoCosAndExpectError(
				"def calcVelocity(x):\n" +
						"    x+=1\n" +
						"calcVelocity()\n"
		);

		// too many arguments
		parseCodeStringAndCheckCoCosAndExpectError(
				"def calcVelocity(x):\n" +
						"    x+=1\n" +
						"calcVelocity(4,5)\n"
		);

		// too many arguments
		parseCodeStringAndCheckCoCosAndExpectError(
				"def calcVelocity(x, y=1):\n" +
						"    x+=1\n" +
						"calcVelocity(4,5,6)\n"
		);
	}

	@Test
	public void checkCallExpressionBeforeDeclarationCoco() {
		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"def calcVelocity(x):\n" +
						"    x+=1\n" +
						"calcVelocity(4)\n"
		);

		parseCodeStringAndCheckCoCosAndExpectError(
				"calcVelocity(4)\n" +
						"def calcVelocity(x):\n" +
						"    x+=1\n"
		);
	}

	@Test
	public void checkPythonFunctionDeclarationInStatementBlockCheck() {
		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"x = 1\n" +
						"if(x > 5):\n" +
						"    x+=1\n" +
						"def calcVelocity(x):\n" +
						"    x+=1\n"
		);

		parseCodeStringAndCheckCoCosAndExpectError(
				"x = 1\n" +
						"if(x > 5):\n" +
						"    def calcVelocity(x):\n" +
						"        x+=1\n"
		);
	}

	@Test
	public void checkPythonFunctionDuplicateParameterNameCoco() {
		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"def calcVelocity(x,y):\n" +
						"    x+=1\n"
		);

		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"class Calculator():\n" +
						"    def calc(x, y):\n" +
						"        x += y\n"
		);

		parseCodeStringAndCheckCoCosAndExpectError(
				"def calcVelocity(x,x):\n" +
						"    x+=1\n"
		);

		parseCodeStringAndCheckCoCosAndExpectError(
				"class Calculator():\n" +
						"    def calc(x, x):\n" +
						"        x += 1\n"
		);
	}

	@Test
	public void checkPythonDuplicateFunctionNameCoco() {
		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"def calcVelocity(x,y):\n" +
						"    x+=1\n" +
						"def calcVelocity2(x,y):\n" +
						"    x+=1\n"
		);


		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"class Calculator():\n" +
						"    def calc(x, y):\n" +
						"        x += 1\n" +
						"    def calc2(x, y):\n" +
						"        x += 1\n"
		);

		parseCodeStringAndCheckCoCosAndExpectError(
				"def calcVelocity(x,y):\n" +
						"    x+=1\n" +
						"def calcVelocity(x,y):\n" +
						"    x+=1\n"
		);

		parseCodeStringAndCheckCoCosAndExpectError(
				"class Calculator():\n" +
						"    def calc(x, y):\n" +
						"        x += 1\n" +
						"    def calc(x, y):\n" +
						"        x += 1\n"
		);
	}

	@Test
	public void checkPythonLambdaDuplicateParameterNameCoco() {
		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"z = lambda x: x\n"
		);


		parseCodeStringAndCheckCoCosAndExpectError(
				"z = lambda x, x: x\n"
		);
	}

	@Test
	public void checkPythonVariableOrFunctionOrClassExistsCoco() {
		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"x = 1\n" +
						"x+=1\n"
		);

		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"def calc():\n" +
						"    return 1\n" +
						"calc()\n"
		);

		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"class Calculator:\n" +
						"    def calc(self):\n" +
						"        return 1\n" +
						"c = Calculator()\n" +
						"c.calc()\n"
		);

		// x not exists
		parseCodeStringAndCheckCoCosAndExpectError(
				"if(x > 5):\n" +
						"    x+=1\n"
		);

		// function calc not exists
		parseCodeStringAndCheckCoCosAndExpectError(
				"calc()\n"
		);

		// class Calculator not exists
		parseCodeStringAndCheckCoCosAndExpectError(
				"c = Calculator()\n" +
						"c.calc()\n"
		);
	}

	@Test
	public void checkInvalidBooleanPython() {
		parseCodeStringAndCheckCoCosAndExpectError("True = True\n");

	}

	@Test
	public void checkInvalidJavaBooleanOpPython() {
		parseCodeStringAndCheckCoCosAndExpectError("x = 1 == 1 && 2 == 2\n");
		parseCodeStringAndCheckCoCosAndExpectError("x = 1 == 1 || 2 == 2\n");
		parseCodeStringAndCheckCoCosAndExpectError("x = !(1 == 1)\n");

	}

	/*
	@Test
	public void checkSIPythonCommonExpressionsTypeCheckCoco() {
		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"y = 0\n" +
						"x = y + 5 dm/h + 3 km/h\n + y\n"
		);

		parseCodeStringAndCheckCoCosAndExpectError(
				"x = 5 dm/h + 3 °C\n"
		);

		//checks that all distinct pairs of base units are not allowed in a common expression together
		performCocoChecksForAllBaseUnits(this::parseCommonExpressionsWithTwoUnitsAndCheckCoCosAndExpectError);
	}

	 */

	@Test
	public void checkSIPythonSIUnitConversionTypeCheckCoco() {
		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"x = dm/h(3 km/h)\n"
		);

		parseCodeStringAndCheckCoCosAndExpectError(
				"x = dm/h(3 °C)\n"
		);

		//checks that all distinct pairs of base units are not allowed in a unit conversion expression together
		performCocoChecksForAllBaseUnits(this::parseSIUnitConversionTypeCheckAndCheckCoCosAndExpectError);
	}


	@Test
	public void checkPythonExpressionCoco() {
		parseCodeStringAndCheckCoCosAndExpectSuccess(
				"x = 1 if x==6 else 0\n"
		);

		//ternary operator should be assigned to a variable
		parseCodeStringAndCheckCoCosAndExpectWarn(
				"y = 2\n" +
						"1 if y==6 else 0\n"
		);
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

	private void parseCodeStringAndCheckCoCosAndExpectWarn(String codeString) {
		Optional<ASTPythonScript> astPythonScriptOptional = parseModelFromStringAndReturnASTPythonScript(codeString);
		checkCoCos(astPythonScriptOptional);
		assertTrue(Log.getFindingsCount() > 0);
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
		if (astPythonScriptOptional.isEmpty()) {
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
		SIPythonCoCoChecker checker = new SIPythonCoCoChecker();
		checker.addCoCo(new PythonExpressionCoco());
		checker.addCoCo(SIPythonSIUnitConversionTypeCheckCoco.getCoCo());
		checker.addCoCo((PythonASTWhileStatementCoCo) new PythonFunctionDeclarationInStatementBlockCoco());
		checker.addCoCo(new PythonFunctionDuplicateParameterNameCoco());
		checker.addCoCo(((PythonASTPythonScriptCoCo) new PythonDuplicateFunctionAndClassCoco()));
		checker.addCoCo(new PythonFunctionArgumentSizeCoco());
		checker.addCoCo(new PythonVariableOrFunctionOrClassExistsCoco());
		checker.addCoCo(new PythonLambdaDuplicateParameterNameCoco());
		checker.addCoCo(new CallExpressionAfterFunctionDeclarationCoco());
		//checker.addCoCo((CommonExpressionsASTPlusExpressionCoCo) SIPythonCommonExpressionsTypeCheckCoco.getCoco());
		checker.addCoCo(((CommonExpressionsASTBooleanNotExpressionCoCo) new JavaBooleanExpressionCoco()));

		return checker;
	}

	/**
	 * This method executes the given CoCoCheckWithTwoUnits on each possible distinct pair of si base units.
	 */
	private void performCocoChecksForAllBaseUnits(CoCoCheckWithTwoUnits coCoCheckWithTwoUnits) {
		String[] unitArray = {"s", "m", "kg", "A", "K", "mol", "cd"};

		Arrays.stream(unitArray).forEach(unit1 -> Arrays.stream(unitArray).forEach(unit2 -> {
			if (!unit1.equals(unit2)) {
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
				"x = 5 " + unit1 + " + 3 " + unit2 + "\n"
		);
	}

	private void parseSIUnitConversionTypeCheckAndCheckCoCosAndExpectError(String unit1, String unit2) {
		parseCodeStringAndCheckCoCosAndExpectError(
				"x = " + unit1 + "( 3 " + unit2 + ")\n"
		);
	}
}
