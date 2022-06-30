package de.monticore.sipython;

import de.monticore.sipython.generator.Generator;
import org.junit.Before;
import org.junit.Test;

public class GeneratorTest {

    @Before
    public void init() {
        SIPythonMill.init();
    }

    @Test
    public void simpleGeneratorTest() {
        Generator.generate("src/test/resources/","unit_script.sipy","target/generate");
    }
}
