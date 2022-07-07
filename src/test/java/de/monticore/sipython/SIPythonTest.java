package de.monticore.sipython;

import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SIPythonTest extends AbstractTest {

	@Before
	public void init() {
		Log.init();
		Log.enableFailQuick(false);
	}

//	---------------------------------------------------------------
//	Tests for whole scripts from files.
//	---------------------------------------------------------------

	@Test
	public void parseSimpleSIPython() {
		String model = "unit_script.sipy";
		parseModelAndExpectSuccess(model);
	}

	@Test
	public void parseSyntaxError() {
		String model = "tests/textSyntaxErrors.sipy";
		parseModelAndExpectErrors(model, 3);
	}

	@Test
	public void parseSyntaxNoError() {
		String model = "tests/textSyntaxNoErrors.sipy";
		parseModelAndExpectSuccess(model);
	}

	@Test
	public void parseFunctions() {
		String model = "tests/funct.sipy";
		parseModelAndExpectSuccess(model);
	}

	@Test
	public void parsePrints() {
		String model = "tests/prints.sipy";
		parseModelAndExpectSuccess(model);
	}

//	---------------------------------------------------------------
//	Tests for single code snippets from strings.
//	---------------------------------------------------------------

	@Test
	public void parseValidUnit() {
		parseCodeStringAndExpectSuccess("3 km/h\n");
	}

	@Test
	public void parseInvalidUnit() {
		parseCodeStringAndExpectFail("3 xxx\n");
		parseCodeStringAndExpectFail("3 y/h\n");
	}

	@Test
	public void parseValidVariableAssignment() {
		parseCodeStringAndExpectSuccess("velocity = 5 dm/h\n");
	}

	@Test
	public void parseInvalidAssignment() {
		parseCodeStringAndExpectFail("velocity = \n");
	}

	@Test
	public void parseValidSIUnitConversion() {
		parseCodeStringAndExpectSuccess("km/h(5 dm/h)\n");
	}
	@Test
	public void parseInvalidSIUnitConversion() {
		parseCodeStringAndExpectFail("km/h(5 dm/h\n");
		parseCodeStringAndExpectFail("km/h5 dm/h)\n");
	}
}
