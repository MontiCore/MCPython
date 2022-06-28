package de.monticore.sipythonunits;

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
}
