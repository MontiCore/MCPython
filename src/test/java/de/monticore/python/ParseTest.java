package de.monticore.python;

import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._ast.ASTStringLiteralPython;
import de.monticore.python._parser.PythonParser;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ParseTest {

  @Test
  public void doTest() throws Exception{
    PythonMill.init();
    Log.init();
    Log.enableFailQuick(false);

    File file = new File("src/test/resources/tests/problems.py");
    assertTrue(file.exists(), "File does not exist: " + file.getAbsolutePath());
    PythonParser p = PythonMill.parser();
    Optional<ASTPythonScript> opt = p.parse(file.getAbsolutePath());

    Log.getFindings().forEach(System.err::println);

    System.err.print("");
    System.err.print(p.currentTokenStream.getTokens());

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testEmptyFileParses() throws IOException {
    PythonMill.init();
    Log.init();
    Log.enableFailQuick(false);


    PythonParser p = PythonMill.parser();
    assertNotNull(p.parse_String("").get());
    assertNotNull(p.parse_String("\n").get());
  }

  @Test
  public void testTrailingCommaArguments() throws IOException {
    PythonMill.init();
    Log.init();
    Log.enableFailQuick(false);

    PythonParser p = new PythonParser();
    p.parse(new StringReader("foo.bar(\n" +
        "    1,\n" +
        ")"));

    Log.getFindings().forEach(System.err::println);

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testStringModifiers() throws IOException {
    PythonMill.init();
    Log.init();
    Log.enableFailQuick(false);

    PythonParser p = new PythonParser();
    ASTPythonScript ast = p.parse_String("rf'someString'").get();
    assertNotNull(ast);
  }
}
