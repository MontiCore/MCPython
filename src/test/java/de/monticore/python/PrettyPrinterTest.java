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

  @Test
  public void testPrettyPrintStrings() throws IOException {
    String printed = assertPrettyPrintReparses(
        "x = 'a' in y\n" +
        "x = f'ab' in y\n" +
        "x = f'''a {b}\nc'''\n" +
        "x = f\"\"\"a {b}\nc\"\"\"\n");
    assertTrue(printed.contains("f'ab' in y"), printed);
    assertTrue(printed.contains("f'''a {b}\nc'''"), printed);
  }

  // the comma after an assignment is only printed if it was in the source
  @Test
  public void testPrettyPrintAssignmentTrailingComma() throws IOException {
    String printed = assertPrettyPrintReparses(
        "print(a, sep='', end='x')\n" +
        "x = 1\n" +
        "a.y = 1,\n" +
        "z: int = 1\n");
    assertFalse(printed.contains(",,"), printed);
    assertFalse(printed.contains(",)"), printed);
    assertTrue(printed.contains("x=1 "), printed);
    // `y = 1,` would be a MultiVariableDeclaration, which has its own optional-comma printer issue
    assertTrue(printed.contains("a.y=1,"), printed);
    assertTrue(printed.contains("z:int=1 "), printed);
  }

  private String assertPrettyPrintReparses(String model) throws IOException {
    PythonMill.init();
    Log.init();
    Log.enableFailQuick(false);

    ASTPythonScript ast = PythonMill.parser().parse_String(model).get();
    String prettyPrinted = PythonMill.prettyPrint(ast, true);

    Optional<ASTPythonScript> reparsed = PythonMill.parser().parse_String(prettyPrinted);
    assertTrue(reparsed.isPresent(), "Pretty printed model does not parse:\n" + prettyPrinted);
    assertEquals(0, Log.getErrorCount());
    return prettyPrinted;
  }
}
