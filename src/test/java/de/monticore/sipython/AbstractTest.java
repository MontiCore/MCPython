package de.monticore.sipython;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.sipython._parser.SIPythonParser;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class AbstractTest {

	public int parseModelAndReturnErrors(String modelFileName) {
		Log.getFindings().clear();
		SIPythonParser parser = new SIPythonParser();
		try {
			parser.parsePythonScript("src/test/resources/" + modelFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return (int)Log.getErrorCount();
	}

	public void parseModelAndExpectErrors(String modelFileName, int expectedErrorCount) {
		assertEquals(expectedErrorCount, parseModelAndReturnErrors(modelFileName));
	}

	public void parseModelAndExpectSuccess(String modelFileName) {
		parseModelAndExpectErrors(modelFileName, 0);
	}

	public ASTPythonScript parseModel(String input) {
		SIPythonParser parser = new SIPythonParser();
		Optional<ASTPythonScript> res = Optional.empty();
		try {
			res = parser.parsePythonScript("src/test/resources/" + input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue(res.isPresent());
		return res.get();
	}
}
