package de.monticore.python;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._parser.PythonParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
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
}
