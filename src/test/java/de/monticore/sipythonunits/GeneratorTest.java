package de.monticore.sipythonunits;

import static org.junit.Assert.assertNotEquals;

import de.monticore.siphytonunits.generator.Generator;
import org.junit.Test;

public class GeneratorTest {
    @Test
    public void simpleGeneratorTest() {
        String generatedScript = Generator.generate("src/test/resources","unit_script.sipy","target/generate");
        System.out.println("generatedScript: " + generatedScript);
        assertNotEquals("", generatedScript);
    }
}
