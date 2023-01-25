package de.monticore.sipython;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.sipython._parser.SIPythonParser;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AbstractTest {

	private static final SIPythonTool siPythonTool = new SIPythonTool();

	private static void runCocos(ASTPythonScript astPythonScript) {
		siPythonTool.runDefaultCoCos(astPythonScript);
	}

	public Optional<ASTPythonScript> parseModelFromFileAndReturnASTPythonScript(String modelFileName) {
		Log.getFindings().clear();
		SIPythonParser parser = new SIPythonParser();
		Optional<ASTPythonScript> astPythonScriptOptional = Optional.empty();
		try {
			astPythonScriptOptional = parser.parsePythonScript("src/test/resources/" + modelFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return astPythonScriptOptional;
	}

	public int parseModelFromFileAndReturnErrorCount(String modelFileName) {
		parseModelFromFileAndReturnASTPythonScript(modelFileName);
		return (int)Log.getErrorCount();
	}

	public void parseModelFromFileAndExpectErrors(String modelFileName, int expectedErrorCount) {
		assertEquals(expectedErrorCount, parseModelFromFileAndReturnErrorCount(modelFileName));
	}

	public void parseModelFromFileAndExpectSuccess(String modelFileName) {
		parseModelFromFileAndExpectErrors(modelFileName, 0);
	}

	public Optional<ASTPythonScript> parseModelFromStringAndReturnASTPythonScript(String codeString) {
		Log.getFindings().clear();
		SIPythonParser siPythonParser = new SIPythonParser();
		Optional<ASTPythonScript> astPythonScriptOptional = Optional.empty();
		try {
			astPythonScriptOptional = siPythonParser.parse(new StringReader(codeString));
		} catch (IOException e) {
			e.printStackTrace();
		}

		//astPythonScriptOptional.ifPresent(AbstractTest::runCocos);
		return astPythonScriptOptional;
	}

	public int parseModelFromStringAndReturnErrorCount(String codeString) {
		parseModelFromStringAndReturnASTPythonScript(codeString);

		return (int)Log.getErrorCount();
	}

	public void parseModelFromStringAndExpectErrorCount(String codeString, int expectedErrorCount) {
		assertEquals(expectedErrorCount, parseModelFromStringAndReturnErrorCount(codeString));
	}

	public void parseModelFromStringAndExpectSuccess(String codeString) {
		parseModelFromStringAndExpectErrorCount(codeString,0);
	}

	public void parseModelFromFileAndExpectFail(String codeString) {
		try {
			var errorCount = parseModelFromFileAndReturnErrorCount(codeString);
			assertTrue("Expected some errors here, but no occurred!", errorCount > 0);
		} catch(Exception e) {
			assertNotNull(e);
		}
	}

	public void parseModelFromStringAndExpectFail(String codeString) {
		try {
			var errorCount = parseModelFromStringAndReturnErrorCount(codeString);
			assertTrue("Expected some errors here, but no occurred!", errorCount > 0);
		} catch(Exception e) {
			assertNotNull(e);
		}
	}
}
