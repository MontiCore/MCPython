package de.monticore.sipython;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.sipython._parser.SIPythonParser;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AbstractTest {

	public int parseModelFromFileAndReturnErrorsCount(String modelFileName) {
		Log.getFindings().clear();
		parseModelFromFileAndReturnASTPythonScript(modelFileName);
		int errorCount = (int)Log.getErrorCount();
		Log.clearFindings();
		return errorCount;
	}

	public void parseModelFromFileAndExpectErrors(String modelFileName, int expectedErrorCount) {
		assertEquals(expectedErrorCount, parseModelFromFileAndReturnErrorsCount(modelFileName));
	}

	public void parseModelFromFileAndExpectSuccess(String modelFileName) {
		parseModelFromFileAndExpectErrors(modelFileName, 0);
	}

	public Optional<ASTPythonScript> parseModelFromFileAndReturnASTPythonScript(String input) {
		SIPythonParser parser = new SIPythonParser();
		Optional<ASTPythonScript> res = Optional.empty();
		try {
			res = parser.parsePythonScript("src/test/resources/" + input);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(res.isPresent()) {
			SIPythonTool tool = new SIPythonTool();
			tool.runDefaultCoCos(res.get());
		}

		return res;
	}

	public int parseModelFromStringAndReturnErrorCount(String codeString) {
		Log.getFindings().clear();
		SIPythonParser siPythonParser = new SIPythonParser();
		Optional<ASTPythonScript> res = Optional.empty();
		try {
			res = siPythonParser.parse(new StringReader(codeString + "\n"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(res.isPresent()) {
			SIPythonTool tool = new SIPythonTool();
			tool.runDefaultCoCos(res.get());
		}

		return (int)Log.getErrorCount();
	}

	public void parseModelFromStringAndExpectErrorCount(String codeString, int expectedErrorCount) {
		assertEquals(expectedErrorCount, parseModelFromStringAndReturnErrorCount(codeString));
	}

	public void parseModelFromStringAndExpectSuccess(String codeString) {
		parseModelFromStringAndExpectErrorCount(codeString,0);
	}

	public void parseModelFromStringAndExpectFail(String codeString) {
		assertTrue("Expected some errors here, but no occurred!", parseModelFromStringAndReturnErrorCount(codeString) > 0);
	}
}
