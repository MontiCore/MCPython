package de.monticore.python;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.python._parser.PythonAntlrLexer;
import de.monticore.python._parser.PythonAntlrParser;
import de.monticore.python._parser.PythonParser;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.DecisionInfo;
import org.antlr.v4.runtime.atn.DecisionState;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static de.monticore.python._parser.PythonParser.getPreprocessedTokenStream;

public class ParseUtil {
  public static void main(String[] args) throws IOException {
    boolean debugPerformance = false;
    boolean failAfterFirstErrorFile = false;
    Log.enableFailQuick(false);
    AtomicInteger overallErrors = new AtomicInteger(0);
    AtomicInteger fileCounter = new AtomicInteger(0);
    AtomicInteger errorLessFileCounter = new AtomicInteger(0);
    AtomicBoolean atnPrinted = new AtomicBoolean(false);

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
            parser.debugPerformance = debugPerformance;
            if(!atnPrinted.get()){
              atnPrinted.set(true);
            }
            parser.parse(p.toString());

            String c = visPreprocessedContent(parser);
            File f = new File("build/preprocessing/" + p.toFile().getName() + ".pre");
            f.getParentFile().mkdirs();
            FileUtils.write(f, c, StandardCharsets.UTF_8);

            String fullContent = "";
            List<Finding> errors = Log.getFindings().stream().filter(Finding::isError).collect(Collectors.toList());
            if(!errors.isEmpty()){
              List<Token> tokens = visualizeAllTokens(p);
              fullContent = visPreprocessedContent(tokens);
            }

            for (Finding error : errors) {
              System.out.println(error);
            }
            System.out.println(errors.size() + " errors while parsing");
            overallErrors.addAndGet(errors.size());

            if(debugPerformance){
              printParserProfiling(parser.currentParser);
            }

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

  private static List<Token> visualizeAllTokens(Path p) throws IOException {
    PythonAntlrLexer lexer = new PythonAntlrLexer(org.antlr.v4.runtime.CharStreams.fromReader(new FileReader(p.toFile())));
    CommonTokenStream stream = getPreprocessedTokenStream(lexer);

    List<Token> tokens = new ArrayList<>();
    Token token;
    int i = 1;
    stream.seek(i);
    while ((token = stream.get(i - 1)).getType() != Token.EOF) {
      stream.seek(i++);
      tokens.add(token);
    }
    return tokens;
  }

  public static String visPreprocessedContent(List<Token> tokens) {
    return tokensToReadableString(tokens);
  }

  public static String visPreprocessedContent(PythonParser parser) {
    return tokensToReadableString(parser.currentTokenStream.getTokens());
  }

  public static String tokensToReadableString(List<Token> tokens) {
    IndentPrinter ip = new IndentPrinter();
    List<String> linebreaks = List.of("⦃", "⁏", "⦄");
    List<String> incIndent = List.of("⦃");
    List<String> decIndent = List.of("⦄");

    for (Token t : tokens) {
      String text = t.getText();
      ip.print(text);
      if (decIndent.contains(text)) {
        ip.unindent();
      }
      if (linebreaks.contains(text)) {
        ip.println();
      } else {
        ip.print(" ");
      }
      if (incIndent.contains(text)) {
        ip.indent();
      }
    }
    return ip.getContent();
  }

  public static void printParserProfiling(PythonAntlrParser parser) {
    System.out.print(String.format("%-" + 35 + "s", "rule"));
    System.out.print(String.format("%-" + 15 + "s", "time"));
    System.out.print(String.format("%-" + 15 + "s", "invocations"));
    System.out.print(String.format("%-" + 15 + "s", "lookahead"));
    System.out.print(String.format("%-" + 15 + "s", "lookahead(max)"));
    System.out.print(String.format("%-" + 15 + "s", "ambiguities"));
    System.out.println(String.format("%-" + 15 + "s", "errors"));

    var infos = Arrays.stream(parser.getParseInfo().getDecisionInfo())
        .sorted(Comparator.comparing(di -> di.timeInPrediction)).collect(Collectors.toList());
    for (DecisionInfo decisionInfo : infos) {
      DecisionState ds = parser.getATN().getDecisionState(decisionInfo.decision);
      String rule = parser.getRuleNames()[ds.ruleIndex];

      if (decisionInfo.timeInPrediction > 0) {
        System.out.print(String.format("%-" + 35 + "s", rule));
        System.out.print(String.format("%-" + 15 + "s", decisionInfo.timeInPrediction));
        System.out.print(String.format("%-" + 15 + "s", decisionInfo.invocations));
        System.out.print(String.format("%-" + 15 + "s", decisionInfo.SLL_TotalLook));
        System.out.print(String.format("%-" + 15 + "s", decisionInfo.SLL_MaxLook));
        System.out.print(String.format("%-" + 15 + "s", decisionInfo.ambiguities));
        System.out.println(String.format("%-" + 15 + "s", decisionInfo.errors));
      }
    }
  }


}
