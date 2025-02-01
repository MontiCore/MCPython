package de.monticore.python;

import de.monticore.python._ast.ASTPythonScript;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class PrettyPrinterTest {

  @Test
  public void testPrettyPrinterDoesNotCrash() throws IOException {
    PythonMill.init();

    ASTPythonScript ast = PythonMill.parser().parse("src/test/resources/samples/speed_camera.py").get();
    String prettyPrinted = PythonMill.prettyPrint(ast, true);
    assertNotNull(prettyPrinted);
  }
}
