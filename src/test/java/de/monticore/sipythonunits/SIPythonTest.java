package de.monticore.sipythonunits;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.sipython._parser.SIPythonParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class SIPythonTest {

	@Before
	public void init() {
		Log.init();
		Log.enableFailQuick(true);
	}

	private ASTPythonScript parseModel(String input) {
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

	@Test
	public void parseSimplePython() {
		String model = "simple_python.sipy";
		parseModel(model);
	}

	@Test
	public void parseSimpleSIPython() {
		String model = "unit_script.sipy";
		parseModel(model);
	}
}
