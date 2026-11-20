/* (c) https://github.com/MontiCore/monticore */
package de.monticore.python;

import de.monticore.python._ast.ASTPythonScript;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Grammar coverage: parses every sample file under {@code src/test/resources/python-coverage/}.
 * <p>
 * Not all of Python is supported yet, so failures here are expected. These tests are excluded from the
 * normal {@code test} task and run with {@code gradlew pythonCoverage} (failures are recorded, not fatal).
 * {@code gradlew compareCoverage} compares the results with the {@code dev} branch.
 * <p>
 * The tests are grouped as chapter -> section -> file. The name of each file test is always the full
 * relative path (e.g. {@code compound_statements/try/except_star.py}), because the branch comparison
 * matches results by that name.
 */
@Tag("coverage")
public class PythonCoverageTest {

  /** Root folder of the samples; can be overridden with the system property {@code pythonCoverage.root}. */
  static final Path ROOT = Paths.get(System.getProperty("pythonCoverage.root", "src/test/resources/python-coverage"));

  static final Duration TIMEOUT = Duration.ofSeconds(10);

  @TestFactory
  Stream<DynamicNode> pythonCoverage() throws IOException {
    List<String> files = findSampleFiles(ROOT);
      assertFalse(files.isEmpty(), "No sample files found in " + ROOT.toAbsolutePath());

    // chapter -> section -> files (sorted, so the order is stable)
    Map<String, Map<String, List<String>>> tree = new TreeMap<>();
    for (String file : files) {
      String[] parts = file.split("/");
      String chapter = parts.length > 1 ? parts[0] : "(root)";
      String section = parts.length > 2 ? parts[1] : "(chapter root)";
      tree.computeIfAbsent(chapter, k -> new TreeMap<>())
          .computeIfAbsent(section, k -> new ArrayList<>())
          .add(file);
    }

    return tree.entrySet().stream().map(chapter ->
        DynamicContainer.dynamicContainer(chapter.getKey(), chapter.getValue().entrySet().stream().map(section ->
            DynamicContainer.dynamicContainer(section.getKey(), section.getValue().stream().map(file ->
                DynamicTest.dynamicTest(file, ROOT.resolve(file).toUri(), () -> assertParses(file)))))));
  }

  /** Returns all {@code .py} files below {@code root} as sorted, '/'-separated relative paths. */
  static List<String> findSampleFiles(Path root) throws IOException {
    try (Stream<Path> s = Files.walk(root)) {
      return s.filter(Files::isRegularFile)
          .filter(p -> p.getFileName().toString().endsWith(".py"))
          .map(p -> root.relativize(p).toString().replace('\\', '/'))
          .sorted()
          .collect(Collectors.toList());
    }
  }

  // Failures are plain AssertionErrors and not JUnit's fail(): Gradle 8.5 (used in CI) cannot map an
  // AssertionFailedError without expected/actual values and logs every error message twice.
  static void assertParses(String file) {
    Path path = ROOT.resolve(file);
    PythonMill.init();
    Log.init();
    Log.enableFailQuick(false);

    Optional<ASTPythonScript> ast;
    try {
      ast = assertTimeoutPreemptively(TIMEOUT, () -> PythonMill.parser().parse(path.toAbsolutePath().toString()),
          "Parsing took longer than " + TIMEOUT.toSeconds() + "s");
    } catch (AssertionError e) {
      throw e;
    } catch (Throwable t) {
      throw new AssertionError(file + " does not parse: the parser threw " + t, t);
    }

    String errors = Log.getFindings().stream()
        .filter(Finding::isError)
        .map(Finding::toString)
        .collect(Collectors.joining("\n"));
    if (Log.getErrorCount() > 0) {
      throw new AssertionError(file + " does not parse:\n" + errors);
    }
    if (ast.isEmpty()) {
      throw new AssertionError(file + " does not parse: no AST was returned");
    }
  }
}
