package de.monticore.python._parser;

import de.monticore.python.PythonPreprocessor;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.atn.PredictionMode;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class PythonParser extends PythonParserTOP {
  // TMP: Used to inspect preprocessing
  public CommonTokenStream currentTokenStream;
  public PythonAntlrParser currentParser;
  public boolean debugPerformance = false;

  @Override
  protected PythonAntlrParser create(Reader reader) throws IOException {
    PythonAntlrLexer lexer = new PythonAntlrLexer(org.antlr.v4.runtime.CharStreams.fromReader(reader));
    CommonTokenStream stream = getPreprocessedTokenStream(lexer);
    PythonAntlrParser parser = new PythonAntlrParser(stream);
    lexer.setMCParser(parser);
    lexer.removeErrorListeners();
    lexer.addErrorListener(new de.monticore.antlr4.MCErrorListener(parser));

    currentTokenStream = stream;

    parser.setFilename("StringReader");
    setError(false);
    return parser;
  }

  public static CommonTokenStream getPreprocessedTokenStream(PythonAntlrLexer lexer) {
    PreprocessingTokenSource tokensWithPreprocessing = new PreprocessingTokenSource(
            lexer,
        new CommonToken(PythonAntlrLexer.STATEMENT_END, PythonPreprocessor.STATEMENT_END),
        new CommonToken(PythonAntlrLexer.BLOCK_START, PythonPreprocessor.BLOCK_START),
        new CommonToken(PythonAntlrLexer.BLOCK_END, PythonPreprocessor.BLOCK_END),
        new PythonPreprocessingTokens()
    );
    // Use CommonTokenStream to hide the hidden channel again
    return new CommonTokenStream(tokensWithPreprocessing);
  }

  @Override
  protected PythonAntlrParser create(String fileName) throws IOException {
    PythonAntlrLexer lexer = new PythonAntlrLexer(org.antlr.v4.runtime.CharStreams.fromReader(
        new FileReader(fileName, StandardCharsets.UTF_8)));

    CommonTokenStream stream = getPreprocessedTokenStream(lexer);
    currentTokenStream = stream;
    PythonAntlrParser parser = new PythonAntlrParser(stream);
    currentParser = parser;
    lexer.setMCParser(parser);
    lexer.removeErrorListeners();
    lexer.addErrorListener(new de.monticore.antlr4.MCErrorListener(parser));
    parser.setFilename(fileName);

    if(debugPerformance) {
      // TODO: Tmp for performance analysis
      parser.addErrorListener(new DiagnosticErrorListener());
      parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
      parser.setProfile(true);
    }

    setError(false);
    return parser;
  }
}
