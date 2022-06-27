package de.monticore.sipythonunits;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.sipython.SIPythonMill;
import de.monticore.sipython._ast.ASTSIPythonScript;
import de.monticore.sipython._cocos.SIPythonCoCoChecker;
import de.monticore.sipython._cocos.SIPythonTypeCheckCoco;
import de.monticore.sipython._parser.SIPythonParser;
import de.monticore.siunits.SIUnitsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

public class SIPythonCocoTest {

	@Before
	public void init() {
		Log.init();
		Log.enableFailQuick(true);
		SIPythonMill.reset();
		SIPythonMill.init();
		SIUnitsMill.initializeSIUnits();
		SIPythonMill.globalScope().add(SIPythonMill.typeSymbolBuilder()
				.setName("si")
				.setEnclosingScope(SIPythonMill.globalScope())
				.setSpannedScope(SIPythonMill.scope())
				.build()
		);
	}

	private ASTSIPythonScript parseModel(String input) {
		SIPythonParser parser = new SIPythonParser();
		Optional<ASTSIPythonScript> res = Optional.empty();
		try {
			res = parser.parseSIPythonScript("src/test/resources/" + input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue(res.isPresent());
		return res.get();
	}

	private void typeCheckCoCo(String input, boolean expectedError) {
		Log.getFindings().clear();
		ASTSIPythonScript model = parseModel(input);
		SIPythonMill.scopesGenitorDelegator().createFromAST(model);
		SIPythonCoCoChecker checker = new SIPythonCoCoChecker();
		checker.addCoCo(SIPythonTypeCheckCoco.getCoCo());

		try {
			checker.checkAll(model);
		} catch (Exception e) {
			fail(e.getMessage());
		}

		assertEquals(expectedError, Log.getErrorCount() > 0);
	}

	@Test
	public void parseSimplePython() {
		String model = "simple_python.sipy";
		typeCheckCoCo(model, false);
	}

	@Test
	public void parseSimpleSIPython() {
		String model = "unit_script.sipy";
		typeCheckCoCo(model, false);
	}
}
