package de.monticore.sipython;

import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

public class SIPythonTest extends AbstractTest {

	@Before
	public void init() {
		Log.init();
		Log.enableFailQuick(false);
	}

//	---------------------------------------------------------------
//	Tests for single code snippets from strings.
//	---------------------------------------------------------------

	@Test
	public void parseValidUnit() {
		parseModelFromStringAndExpectSuccess("3 km/h\n");
	}

	@Test
	public void parseInvalidUnit() {
		parseModelFromStringAndExpectFail("3 xxx\n");
		parseModelFromStringAndExpectFail("3 y/h\n");
	}

	@Test
	public void parseValidVariableAssignment() {
		parseModelFromStringAndExpectSuccess("velocity = 5 dm/h\n");
	}

	@Test
	public void parseInvalidAssignment() {
		parseModelFromStringAndExpectFail("velocity = \n");
	}

	@Test
	public void parseValidSIUnitConversion() {
		parseModelFromStringAndExpectSuccess("km/h(5 dm/h)\n");
	}

	@Test
	public void parseInvalidSIUnitConversion() {
		parseModelFromStringAndExpectFail("km/h(5 dm/h\n");
		parseModelFromStringAndExpectFail("km/h5 dm/h)\n");
	}

//	---------------------------------------------------------------
//	Tests for whole scripts from files.
//	---------------------------------------------------------------

	@Test
	public void parseSimpleSIPython() {
		String model = "tests/unit_script.sipy";
		parseModelFromFileAndExpectSuccess(model);
	}

}
