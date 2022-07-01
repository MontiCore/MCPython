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
        Generator.generate("src/test/resources","unit_script.sipy","target/generate");
    }
}
