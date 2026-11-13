package de.monticore.python;

import de.monticore.python._ast.ASTPythonScript;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PrettyPrinterTest {

  @Test
  public void testPrettyPrinterDoesNotCrash() throws IOException {
    PythonMill.init();

    ASTPythonScript ast = PythonMill.parser().parse("src/test/resources/samples/speed_camera.py").get();
    String prettyPrinted = PythonMill.prettyPrint(ast, true);
    assertNotNull(prettyPrinted);
  }

  @Test
  public void testPrettyPrintTryExcept() throws IOException {
    assertPrettyPrintReparses(
        "try:\n" +
        "    x = 1\n" +
        "except (ValueError, TypeError) as e:\n" +
        "    pass\n" +
        "except KeyError: pass\n" +
        "finally:\n" +
        "    pass\n");
  }

  @Test
  public void testPrettyPrintMatch() throws IOException {
    assertPrettyPrintReparses(
        "match x:\n" +
        "    case 1 | 2:\n" +
        "        pass\n" +
        "    case [1, 2] as y:\n" +
        "        pass\n" +
        "    case _: pass\n");
  }

  private void assertPrettyPrintReparses(String model) throws IOException {
    PythonMill.init();
    Log.init();
    Log.enableFailQuick(false);

    ASTPythonScript ast = PythonMill.parser().parse_String(model).get();
    String prettyPrinted = PythonMill.prettyPrint(ast, true);

    Optional<ASTPythonScript> reparsed = PythonMill.parser().parse_String(prettyPrinted);
    assertTrue(reparsed.isPresent(), "Pretty printed model does not parse:\n" + prettyPrinted);
    assertEquals(0, Log.getErrorCount());
  }
}
