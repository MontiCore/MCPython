package de.monticore.python;

import de.monticore.python._parser.PythonParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import de.monticore.python._ast.ASTPythonScript;


import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class TestJuelichRepos {

  @Before
  public void setUp() {
    PythonMill.init();
    Log.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testParsingBletlProject() throws IOException {

    File file = new File("src/test/resources/preprocessd_juelich_examples/bletl");
    for (File testFile : file.listFiles()) {
      System.out.println("Parsing " + testFile);
      Assert.assertTrue(testFile.getAbsolutePath(),testFile.exists());
      PythonParser p = PythonMill.parser();
      Optional<ASTPythonScript> opt = p.parse(testFile.getAbsolutePath());

      Log.getFindings().forEach(System.err::println);
      System.out.println(opt);

      System.err.print("");
      //System.err.print(p.currentTokenStream.getTokens());
      Assert.assertEquals(0, Log.getErrorCount());
    }
  }

  @Test
  public void testParsingCalibr8Project() throws IOException {

    File file = new File("src/test/resources/preprocessd_juelich_examples/calibr8");
    for (File testFile : file.listFiles()) {
      Assert.assertTrue(testFile.getAbsolutePath(),testFile.exists());
      PythonParser p = PythonMill.parser();
      Optional<ASTPythonScript> opt = p.parse(testFile.getAbsolutePath());

      Log.getFindings().forEach(System.err::println);

      System.err.print("");
      //System.err.print(p.currentTokenStream.getTokens());
    }

    Assert.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testParsingMurefiProject() throws IOException {

    File file = new File("src/test/resources/preprocessd_juelich_examples/murefi");
    for (File testFile : file.listFiles()) {
      Assert.assertTrue(testFile.getAbsolutePath(),testFile.exists());
      PythonParser p = PythonMill.parser();
      Optional<ASTPythonScript> opt = p.parse(testFile.getAbsolutePath());

      Log.getFindings().forEach(System.err::println);

      System.err.print("");
      //System.err.print(p.currentTokenStream.getTokens());
    }

    Assert.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testParsingRobotoolsProject() throws IOException {

    File file = new File("src/test/resources/preprocessd_juelich_examples/robotools");
    for (File testFile : file.listFiles()) {
      Assert.assertTrue(testFile.getAbsolutePath(),testFile.exists());
      PythonParser p = PythonMill.parser();
      Optional<ASTPythonScript> opt = p.parse(testFile.getAbsolutePath());

      Log.getFindings().forEach(System.err::println);

      System.err.print("");
      //System.err.print(p.currentTokenStream.getTokens());
    }

    Assert.assertEquals(0, Log.getErrorCount());
  }
}
