package de.monticore.python;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.python._parser.PythonParser;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.BufferedTokenStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ParseUtil {
  public static void main(String[] args) throws IOException {

    boolean failAfterFirstErrorFile = false;
    Log.enableFailQuick(false);
    AtomicInteger overallErrors = new AtomicInteger(0);
    AtomicInteger fileCounter = new AtomicInteger(0);
    AtomicInteger errorLessFileCounter = new AtomicInteger(0);
    Files
        .walk(Paths.get(args[0]))
        .filter(p -> p.toFile().isFile())
        .filter(p -> p.toFile().getName().endsWith(".py"))
        .forEach(p -> {
          fileCounter.incrementAndGet();
          try {
            Log.getFindings().clear();
            System.out.println("Parsing file:///" + p.toFile().toString().replace("\\", "/"));
            PythonParser parser = PythonMill.parser();
            parser.parse(p.toString());

            String c = visPreprocessedContent(parser);

            List<Finding> errors = Log.getFindings().stream().filter(Finding::isError).collect(Collectors.toList());
            for (Finding error : errors) {
              System.out.println(error);
            }
            System.out.println(errors.size() + " errors while parsing");
            overallErrors.addAndGet(errors.size());
            if (failAfterFirstErrorFile && !errors.isEmpty()) {
              throw new RuntimeException("Found errors and failAfterFirstErrorFile is enabled");
            }
            if(errors.isEmpty()){
              errorLessFileCounter.incrementAndGet();
            }
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });

    System.out.println("Parsed " + fileCounter.intValue() + " file(s) with " + overallErrors.intValue() + " errors");
    double successQuote = 100 * errorLessFileCounter.intValue() / fileCounter.doubleValue();
    String x = String.format("Without errors: %d of %d(%.2f%%)", errorLessFileCounter.intValue(), fileCounter.intValue(), successQuote);
    System.out.println(x);
    System.out.println((overallErrors.intValue() / (double) fileCounter.intValue()) + " errors per file");
  }

  public static String visPreprocessedContent(PythonParser parser) {
    IndentPrinter ip = new IndentPrinter();

    BufferedTokenStream ts = parser.currentTokenStream;
    List<String> linebreaks = List.of("⦃", "⁏", "⦄");
    List<String> incIndent = List.of("⦃");
    List<String> decIndent = List.of("⦄");
    for (int i = 0; i < ts.size(); i++) {
      String text = ts.get(i).getText();
      ip.print(text);
      if(decIndent.contains(text)){
        ip.unindent();
      }
      if (linebreaks.contains(text)) {
        ip.println();
      } else {
        ip.print(" ");
      }
      if(incIndent.contains(text)){
        ip.indent();
      }
    }
    return ip.getContent();
  }
}
