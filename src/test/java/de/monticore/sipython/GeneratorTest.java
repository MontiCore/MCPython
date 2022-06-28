package de.monticore.sipython;

import static org.junit.Assert.assertNotEquals;

import de.monticore.sipython.generator.Generator;
import org.junit.Test;

public class GeneratorTest {
    @Test
    public void simpleGeneratorTest() {
        Generator.generate("src/test/resources/python/","simple_python.sipy","target/generate");

    }
}
