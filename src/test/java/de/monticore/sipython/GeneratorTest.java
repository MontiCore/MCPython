package de.monticore.sipython;

import de.monticore.sipython.generator.Generator;
import de.monticore.siunits.SIUnitsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void Test() {
        generateModelFromFileAndExpectSuccess("src/test/resources", "target/generate", "tests/simple_python.sipy");
    }

    @Test
    public void simpleGeneratorTest() {
        generateModelFromFileAndExpectSuccess("src/test/resources", "target/generate", "tests/unit_script.sipy");
    }

    @Test
    public void enhancedGeneratorTest() {
        generateModelFromFileAndExpectSuccess("src/test/resources","target/generate", "tests/unit_script.sipy", "tests/unit_script_1.sipy");
    }

    @Test
    public void generateSpeedCameraSample() {
        generateModelFromFileAndExpectSuccess("src/test/resources", "target/generate", "samples/speed_camera.sipy");
    }

    public int generateModelFromFileAndReturnErrorsCount(String modelPath, String outputPath, String... fullNames) {
        Generator.generate(modelPath, outputPath, fullNames);
        return (int) Log.getErrorCount();
    }

    public void generateModelFromFileAndExpectErrors(int expectedErrorCount, String modelPath, String outputPath, String... fullNames) {
        assertEquals(expectedErrorCount, generateModelFromFileAndReturnErrorsCount(modelPath, outputPath, fullNames));
    }

    public void generateModelFromFileAndExpectSuccess(String modelPath, String outputPath, String... fullNames) {
        generateModelFromFileAndExpectErrors(0, modelPath, outputPath, fullNames);
    }

    public void generateModelFromFileAndExpectFail(String modelPath, String outputPath, String... fullNames) {
        assertTrue("Expected some errors here, but no occurred!", generateModelFromFileAndReturnErrorsCount(modelPath, outputPath, fullNames) > 0);
    }
}
