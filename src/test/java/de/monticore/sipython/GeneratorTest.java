package de.monticore.sipython;

import de.monticore.sipython.generator.Generator;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore
public class GeneratorTest {

    @Before
    public void init() {
        Log.init();
        Log.enableFailQuick(false);
        SIPythonMill.reset();
        SIPythonMill.init();
    }

    @Test
    public void Test() throws IOException {
        generateModelFromFileAndCompare("src/test/resources", "target/generate", "src/test/resources",
            "tests/simple_python.sipy");
    }

    @Test
    public void simpleGeneratorTest() throws IOException {
        generateModelFromFileAndCompare("src/test/resources", "target/generate", "src/test/resources",
            "tests/unit_script.sipy");
    }

    @Test
    public void enhancedGeneratorTest() throws IOException {
        generateModelFromFileAndCompare("src/test/resources","target/generate", "src/test/resources",
            "tests/unit_script.sipy", "tests/unit_script_1.sipy");
    }

    @Test
    public void generateSpeedCameraSample() throws IOException {
        generateModelFromFileAndCompare("src/test/resources", "target/generate", "src/test/resources",
            "samples/speed_camera.sipy");
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

    public int generateModelFromFileAndCompare(String modelPath, String outputPath, String comparePath, String... fullNames) throws IOException {
        Generator.generate(modelPath, outputPath, fullNames);
        if (Log.getErrorCount() > 0) {
            return ((int) Log.getErrorCount());
        }

        for (String fullName : fullNames) {
            String pythonModelName = fullName.replace(".sipy", ".py");
            File generatedFile = new File(comparePath + "/" + pythonModelName);
            File compareFile = new File(comparePath + "/" + pythonModelName);
            assert (generatedFile.exists());
            Assert.assertEquals ( FileUtils.readFileToString(compareFile, "utf-8").replace("\r", ""),
                FileUtils.readFileToString(generatedFile, "utf-8").replace("\r", ""));
        }
        return 0;
    }
}
