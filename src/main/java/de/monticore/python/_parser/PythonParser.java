package de.monticore.python._parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import de.monticore.python.PythonPreprocessor;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CommonToken;

public class PythonParser extends PythonParserTOP {
  // TMP: Used to inspect preprocessing
  public BufferedTokenStream currentTokenStream;

  @Override
  protected PythonAntlrParser create(Reader reader) throws IOException {
    PythonAntlrLexer lexer = new PythonAntlrLexer(org.antlr.v4.runtime.CharStreams.fromReader(reader));

    WhitespacePreprocessingTokenSource tokensWithPreprocessing = new WhitespacePreprocessingTokenSource(
        lexer,
        new CommonToken(PythonAntlrLexer.STATEMENT_END, PythonPreprocessor.STATEMENT_END),
        new CommonToken(PythonAntlrLexer.BLOCK_START, PythonPreprocessor.BLOCK_START),
        new CommonToken(PythonAntlrLexer.BLOCK_END, PythonPreprocessor.BLOCK_END),
        PythonAntlrLexer.CONTINUE_LINE_TOKEN
    );
    BufferedTokenStream stream = new BufferedTokenStream(tokensWithPreprocessing);
    currentTokenStream = stream;
    PythonAntlrParser parser = new PythonAntlrParser(stream);
    lexer.setMCParser(parser);
    lexer.removeErrorListeners();
    lexer.addErrorListener(new de.monticore.antlr4.MCErrorListener(parser));
    parser.setFilename("StringReader");
    setError(false);
    return parser;
  }

  @Override
  protected PythonAntlrParser create(String fileName) throws IOException {
    PythonAntlrLexer lexer = new PythonAntlrLexer(org.antlr.v4.runtime.CharStreams.fromReader(
        new FileReader(fileName, StandardCharsets.UTF_8)));

    WhitespacePreprocessingTokenSource tokensWithPreprocessing = new WhitespacePreprocessingTokenSource(
        lexer,
        new CommonToken(PythonAntlrLexer.STATEMENT_END, PythonPreprocessor.STATEMENT_END),
        new CommonToken(PythonAntlrLexer.BLOCK_START, PythonPreprocessor.BLOCK_START),
        new CommonToken(PythonAntlrLexer.BLOCK_END, PythonPreprocessor.BLOCK_END),
        PythonAntlrLexer.CONTINUE_LINE_TOKEN
    );
    BufferedTokenStream stream = new BufferedTokenStream(tokensWithPreprocessing);
    currentTokenStream = stream;
    PythonAntlrParser parser = new PythonAntlrParser(stream);
    lexer.setMCParser(parser);
    lexer.removeErrorListeners();
    lexer.addErrorListener(new de.monticore.antlr4.MCErrorListener(parser));
    parser.setFilename(fileName);
    setError(false);
    return parser;
  }
}
