package de.monticore.sipythonunits;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.sipython.SIPythonMill;
import de.monticore.sipython._cocos.SIPythonCoCoChecker;
import de.monticore.sipython._cocos.SIPythonSIUnitConversionTypeCheckCoco;
import de.monticore.siunits.SIUnitsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class SIPythonCocoTest extends AbstractTest{

	@Before
	public void init() {
		Log.init();
		Log.enableFailQuick(false);
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

	private void typeCheckCoCo(String input, boolean expectedError) {
		Log.getFindings().clear();
		ASTPythonScript model = parseModel(input);
		SIPythonMill.scopesGenitorDelegator().createFromAST(model);
		SIPythonCoCoChecker checker = new SIPythonCoCoChecker();
		checker.addCoCo(SIPythonSIUnitConversionTypeCheckCoco.getCoCo());

		try {
			checker.checkAll(model);
		} catch (Exception e) {
			fail(e.getMessage());
		}

		assertEquals(expectedError, Log.getErrorCount() > 0);
	}

	@Test
	public void parseSimplePython() {
		String model = "python/simple_python.sipy";
		typeCheckCoCo(model, false);
	}

	@Test
	public void parseSimpleSIPython() {
		String model = "unit_script.sipy";
		typeCheckCoCo(model, false);
	}

	@Test
	public void parseSimpleSkriptWithTypeCoCoError() {
		String model = "simpleSkript_WithTypeCoCoError.sipy";
		typeCheckCoCo(model, true);
	}
}
