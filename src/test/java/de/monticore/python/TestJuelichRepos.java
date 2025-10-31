package de.monticore.python;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._parser.PythonParser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TestJuelichRepos {

  @BeforeEach
  public void setUp() {
    PythonMill.init();
    Log.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testParsingBletlProject() throws IOException {

    File file = new File("build/testRepos/bletl");
    try (var s = Files.walk(file.toPath())) {
      for (var testPath : s.collect(Collectors.toList())) {
        File testFile = testPath.toFile();
        if (!testFile.getName().endsWith(".py")) {
          continue;
        } else {
          System.out.println("Parsing " + testFile);
        }

        assertTrue(testFile.exists(), "File does not exist: " + testFile.getAbsolutePath());
        PythonParser p = PythonMill.parser();
        Optional<ASTPythonScript> opt = p.parse(testFile.getAbsolutePath());

        Log.getFindings().forEach(System.err::println);
        System.err.print("");
        // System.err.print(p.currentTokenStream.getTokens());
      }
    }

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testParsingCalibr8Project() throws IOException {

    File file = new File("build/testRepos/calibr8");
    try (var s = Files.walk(file.toPath())) {
      for (var testPath : s.collect(Collectors.toList())) {
        File testFile = testPath.toFile();
        if (!testFile.getName().endsWith(".py")) {
          continue;
        } else {
          System.out.println("Parsing " + testFile);
        }

        assertTrue(testFile.exists(), "File does not exist: " + testFile.getAbsolutePath());
        PythonParser p = PythonMill.parser();
        Optional<ASTPythonScript> opt = p.parse(testFile.getAbsolutePath());

        Log.getFindings().forEach(System.err::println);
        System.err.print("");
        // System.err.print(p.currentTokenStream.getTokens());
      }
    }

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testParsingMurefiProject() throws IOException {

    File file = new File("build/testRepos/murefi");
    try (var s = Files.walk(file.toPath())) {
      for (var testPath : s.collect(Collectors.toList())) {
        File testFile = testPath.toFile();
        if (!testFile.getName().endsWith(".py")) {
          continue;
        } else {
          System.out.println("Parsing " + testFile);
        }

        assertTrue(testFile.exists(), "File does not exist: " + testFile.getAbsolutePath());
        PythonParser p = PythonMill.parser();
        Optional<ASTPythonScript> opt = p.parse(testFile.getAbsolutePath());

        Log.getFindings().forEach(System.err::println);
        System.err.print("");
        // System.err.print(p.currentTokenStream.getTokens());
      }
    }

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testParsingRobotoolsProject() throws IOException {

    File file = new File("build/testRepos/robotools");
    try (var s = Files.walk(file.toPath())) {
      for (var testPath : s.collect(Collectors.toList())) {
        File testFile = testPath.toFile();
        if (!testFile.getName().endsWith(".py")) {
          continue;
        } else {
          System.out.println("Parsing " + testFile);
        }

        assertTrue(testFile.exists(), "File does not exist: " + testFile.getAbsolutePath());
        PythonParser p = PythonMill.parser();
        Optional<ASTPythonScript> opt = p.parse(testFile.getAbsolutePath());

        Log.getFindings().forEach(System.err::println);
        System.err.print("");
        // System.err.print(p.currentTokenStream.getTokens());
      }
    }

    assertEquals(0, Log.getErrorCount());
  }
}
