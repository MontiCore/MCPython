package de.monticore.sipython;

import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._parser.PythonParser;
import de.monticore.sipython._parser.SIPythonParser;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AbstractTest {

	public int parseModelAndReturnErrorsCount(String modelFileName) {
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
		assertEquals(expectedErrorCount, parseModelAndReturnErrorsCount(modelFileName));
	}

	public void parseModelAndExpectSuccess(String modelFileName) {
		parseModelAndExpectErrors(modelFileName, 0);
	}

	public ASTPythonScript parseModelAndReturnASTPythonScript(String input) {
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

	public int parseCodeStringAndReturnErrorCount(String codeString) {
		SIPythonParser siPythonParser = new SIPythonParser();
		try {
			siPythonParser.parse(new StringReader(codeString));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (int)Log.getErrorCount();
	}

	public void parseCodeStringAndExpectErrorCount(String codeString, int expectedErrorCount) {
		assertEquals(expectedErrorCount, parseCodeStringAndReturnErrorCount(codeString));
	}

	public void parseCodeStringAndExpectSuccess(String codeString) {
		parseCodeStringAndExpectErrorCount(codeString,0);
	}
}
