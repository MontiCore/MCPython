package de.monticore.sipython;

import de.monticore.sipython.generator.Generator;
import de.monticore.siunits.SIUnitsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

public class GeneratorTest {

    @Before
    public void init() {
        Log.init();
        Log.enableFailQuick(false);
        SIPythonMill.reset();
        SIPythonMill.init();
        SIUnitsMill.initializeSIUnits();
    }

    @Test
    public void simpleGeneratorTest() {
        Generator.generate("src/test/resources", "target/generate", "unit_script.sipy");
    }

    @Test
    public void enhancedGeneratorTest() {
        Generator.generate("src/test/resources","target/generate",  "unit_script.sipy", "unit_script_1.sipy");
    }
}
