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
		parseModel(model, false);
	}

	@Test
	public void parsePythonWithIndentError() {
		String model = "python/python_IndentError.sipy";
		parseModel(model, true);
	}

}
