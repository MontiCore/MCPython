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

	@Test
	public void parseSimpleSIPython() {
		String model = "unit_script.sipy";
		parseModel(model);
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
}
