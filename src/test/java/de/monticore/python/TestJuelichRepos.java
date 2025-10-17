package de.monticore.python;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._parser.PythonParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestJuelichRepos {

  @Before
  public void setUp() {
    PythonMill.init();
    Log.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testParsingBletlProject() throws IOException {

    File file = new File("target/testRepos/bletl");
    try (var s = Files.walk(file.toPath())) {
      for (var testPath : s.collect(Collectors.toList())) {
        File testFile = testPath.toFile();
        if (!testFile.getName().endsWith(".py")) {
          continue;
        } else {
          System.out.println("Parsing " + testFile);
        }

        Assert.assertTrue(testFile.getAbsolutePath(), testFile.exists());
        PythonParser p = PythonMill.parser();
        Optional<ASTPythonScript> opt = p.parse(testFile.getAbsolutePath());

        Log.getFindings().forEach(System.err::println);
        System.err.print("");
        // System.err.print(p.currentTokenStream.getTokens());
      }
    }

    Assert.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testParsingCalibr8Project() throws IOException {

    File file = new File("target/testRepos/calibr8");
    try (var s = Files.walk(file.toPath())) {
      for (var testPath : s.collect(Collectors.toList())) {
        File testFile = testPath.toFile();
        if (!testFile.getName().endsWith(".py")) {
          continue;
        } else {
          System.out.println("Parsing " + testFile);
        }

        Assert.assertTrue(testFile.getAbsolutePath(), testFile.exists());
        PythonParser p = PythonMill.parser();
        Optional<ASTPythonScript> opt = p.parse(testFile.getAbsolutePath());

        Log.getFindings().forEach(System.err::println);
        System.err.print("");
        // System.err.print(p.currentTokenStream.getTokens());
      }
    }

    Assert.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testParsingMurefiProject() throws IOException {

    File file = new File("target/testRepos/murefi");
    try (var s = Files.walk(file.toPath())) {
      for (var testPath : s.collect(Collectors.toList())) {
        File testFile = testPath.toFile();
        if (!testFile.getName().endsWith(".py")) {
          continue;
        } else {
          System.out.println("Parsing " + testFile);
        }

        Assert.assertTrue(testFile.getAbsolutePath(), testFile.exists());
        PythonParser p = PythonMill.parser();
        Optional<ASTPythonScript> opt = p.parse(testFile.getAbsolutePath());

        Log.getFindings().forEach(System.err::println);
        System.err.print("");
        // System.err.print(p.currentTokenStream.getTokens());
      }
    }

    Assert.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testParsingRobotoolsProject() throws IOException {

    File file = new File("target/testRepos/robotools");
    try (var s = Files.walk(file.toPath())) {
      for (var testPath : s.collect(Collectors.toList())) {
        File testFile = testPath.toFile();
        if (!testFile.getName().endsWith(".py")) {
          continue;
        } else {
          System.out.println("Parsing " + testFile);
        }

        Assert.assertTrue(testFile.getAbsolutePath(), testFile.exists());
        PythonParser p = PythonMill.parser();
        Optional<ASTPythonScript> opt = p.parse(testFile.getAbsolutePath());

        Log.getFindings().forEach(System.err::println);
        System.err.print("");
        // System.err.print(p.currentTokenStream.getTokens());
      }
    }

    Assert.assertEquals(0, Log.getErrorCount());
  }
}
