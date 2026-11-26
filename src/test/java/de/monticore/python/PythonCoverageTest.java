/* (c) https://github.com/MontiCore/monticore */
package de.monticore.python;

import de.monticore.python._ast.ASTPythonScript;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Grammar coverage: parses every sample file under {@code src/test/resources/python-coverage/}.
 * <p>
 * Not all of Python is supported yet, so failures here are expected. These tests are excluded from
 * the normal {@code test} task and run with {@code gradlew pythonCoverage} (failures are recorded,
 * not fatal). {@code gradlew compareCoverage} runs the samples of all branches against the grammar
 * of every branch and compares the results (the samples are then read from
 * {@code pythonCoverage.root}).
 * <p>
 * The tests are grouped as chapter -> section -> file. The name of each file test is always the
 * full relative path (e.g. {@code compound_statements/try/except_star.py}), because the branch
 * comparison matches results by that name.
 */
@Tag("coverage")
public class PythonCoverageTest {
  
  /**
   * Root folder of the samples; can be overridden with the system property
   * {@code pythonCoverage.root}.
   */
  static final Path ROOT = Paths.get(System.getProperty("pythonCoverage.root",
      "src/test/resources/python-coverage"));
  
  static final Duration TIMEOUT = Duration.ofSeconds(10);
  
  /** How long a timed-out parser may take to stop before the following tests are skipped. */
  static final Duration STOP_TIMEOUT = Duration.ofSeconds(10);
  
  @TestFactory
  List<DynamicNode> pythonCoverage() throws IOException {
    List<String> files = findSampleFiles(ROOT);
    assertFalse(files.isEmpty(), "No sample files found in " + ROOT.toAbsolutePath());
    
    // chapter -> section -> files (sorted, so the order is stable)
    Map<String, Map<String, List<String>>> tree = new TreeMap<>();
    for (String file : files) {
      String[] parts = file.split("/");
      String chapter = parts.length > 1 ? parts[0] : "(root)";
      String section = parts.length > 2 ? parts[1] : "(chapter root)";
      Map<String, List<String>> sections = tree.computeIfAbsent(chapter, k -> new TreeMap<>());
      sections.computeIfAbsent(section, k -> new ArrayList<>()).add(file);
    }
    
    List<DynamicNode> chapters = new ArrayList<>();
    for (Map.Entry<String, Map<String, List<String>>> chapter : tree.entrySet()) {
      List<DynamicNode> sections = new ArrayList<>();
      for (Map.Entry<String, List<String>> section : chapter.getValue().entrySet()) {
        List<DynamicNode> tests = new ArrayList<>();
        for (String file : section.getValue()) {
          URI source = ROOT.resolve(file).toUri(); // lets the IDE open the sample from the test
          tests.add(DynamicTest.dynamicTest(file, source, () -> assertParses(file)));
        }
        sections.add(DynamicContainer.dynamicContainer(section.getKey(), tests));
      }
      chapters.add(DynamicContainer.dynamicContainer(chapter.getKey(), sections));
    }
    return chapters;
  }
  
  /** Returns all {@code .py} files below {@code root} as sorted, '/'-separated relative paths. */
  static List<String> findSampleFiles(Path root) throws IOException {
    try (
        Stream<Path> s = Files.walk(root)
    ) {
      return s.filter(Files::isRegularFile).filter(p -> p.getFileName().toString().endsWith(".py"))
          .map(p -> root.relativize(p).toString().replace('\\', '/')).sorted().toList();
    }
  }
  
  /**
   * The file whose parser thread was still running after its timeout, or null. The parser ignores
   * interrupts, so such a thread keeps running and shares the static {@code Log} and
   * {@code PythonMill} with the following tests, which would make their results random. Those tests
   * are therefore skipped (the comparison counts them as "not run", neither parsed nor lost).
   */
  static volatile String leakedParser = null;
  
  // Failures are plain AssertionErrors and not JUnit's fail() or assertTimeoutPreemptively:
  // Gradle 8.5 (used in CI) cannot map an AssertionFailedError without expected/actual values and
  // logs every error message twice.
  static void assertParses(String file) throws InterruptedException {
    if (leakedParser != null) {
      Assumptions.abort("Not run: the parser of " + leakedParser
          + " did not stop after its timeout");
    }
    Path path = ROOT.resolve(file);
    PythonMill.init();
    Log.init();
    Log.enableFailQuick(false);
    
    Optional<ASTPythonScript> ast;
    // a daemon thread, so a parser that never stops does not keep the test JVM alive
    ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
      Thread t = new Thread(r, "coverage-parser");
      t.setDaemon(true);
      return t;
    });
    try {
      Future<Optional<ASTPythonScript>> future = executor.submit(() -> PythonMill.parser().parse(
          path.toAbsolutePath().toString()));
      try {
        ast = future.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
      }
      catch (TimeoutException e) {
        future.cancel(true);
        executor.shutdownNow();
        if (!executor.awaitTermination(STOP_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)) {
          leakedParser = file;
        }
        throw new AssertionError(file + " does not parse: parsing took longer than " + TIMEOUT
            .toSeconds() + "s");
      }
      catch (ExecutionException e) {
        throw new AssertionError(file + " does not parse: the parser threw " + e.getCause(), e
            .getCause());
      }
    }
    finally {
      executor.shutdownNow();
    }
    
    String errors = Log.getFindings().stream().filter(Finding::isError).map(Finding::toString)
        .collect(Collectors.joining("\n"));
    if (Log.getErrorCount() > 0) {
      throw new AssertionError(file + " does not parse:\n" + errors);
    }
    if (ast.isEmpty()) {
      throw new AssertionError(file + " does not parse: no AST was returned");
    }
  }
  
}
