package de.monticore.sipython;

import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

public class PythonTest extends AbstractTest {

	@Before
	public void init() {
		Log.init();
		Log.enableFailQuick(false);
	}

	@Test
	public void parseSimplePython() {
		String model = "python/simple_python.sipy";
		parseModelAndExpectSuccess(model);
	}

	/**
	 * In some cases the parser only recognises only one intendtation error at once (although the file contains multiple).
	 * However, if the recognized error is fixed, the next error is detected.
	 * Thus, the test cases at least for indentation errors must include only one error.
	 */
	@Test
	public void parsePythonWithIndentError() {
		String model = "python/python_IndentError.sipy";
		parseModelAndExpectErrors(model, 1);
	}

}
