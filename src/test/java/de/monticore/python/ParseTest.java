package de.monticore.python;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._parser.PythonParser;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.Token;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

public class ParseTest {

  @Ignore("These are still unsolved parser problems")
  @Test
  public void doTest() throws Exception{
    PythonMill.init();
    Log.init();
    Log.enableFailQuick(false);

    File file = new File("src/test/resources/tests/problems.py");
    Assert.assertTrue(file.getAbsolutePath(),file.exists());
    PythonParser p = PythonMill.parser();
    Optional<ASTPythonScript> opt = p.parse(file.getAbsolutePath());

    Log.getFindings().forEach(System.err::println);

    System.err.print("");
    System.err.print(p.currentTokenStream.getTokens());

    Assert.assertEquals(0, Log.getErrorCount());
  }

  @Ignore("Error with whitespace processing, use branch preprocessing to solve")
  @Test
  public void testTrailingCommaArguments() throws IOException {
    PythonMill.init();
    Log.init();
    Log.enableFailQuick(false);

    PythonParser p = new PythonParser();
    p.parse(new StringReader("foo.bar(\n" +
        "    1,\n" +
        ")"));

    for (Token token : p.currentTokenStream.getTokens()) {
      System.out.println(token.getText() + " ");
    }


    Log.getFindings().forEach(System.err::println);

    System.err.print("");
    System.err.print(p.currentTokenStream.getTokens());

    Assert.assertEquals(0, Log.getErrorCount());
  }
}
