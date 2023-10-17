package de.monticore.sipython._parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import de.monticore.python.PythonPreprocessor;
import de.monticore.python._parser.WhitespacePreprocessingTokenSource;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.TokenStream;

public class SIPythonParser extends SIPythonParserTOP {

    @Override
    protected SIPythonAntlrParser create(Reader reader) throws IOException {
        SIPythonAntlrLexer lexer = new SIPythonAntlrLexer(org.antlr.v4.runtime.CharStreams.fromReader(reader));
        TokenStream stream = getTokensWithPreprocessing(lexer);

        SIPythonAntlrParser parser = new SIPythonAntlrParser(stream);
        lexer.setMCParser(parser);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new de.monticore.antlr4.MCErrorListener(parser));
        parser.setFilename("StringReader");
        setError(false);
        return parser;
    }

    @Override
    protected SIPythonAntlrParser create(String fileName) throws IOException {
        SIPythonAntlrLexer lexer = new SIPythonAntlrLexer(org.antlr.v4.runtime.CharStreams.fromFileName(fileName, StandardCharsets.UTF_8));
        TokenStream stream = getTokensWithPreprocessing(lexer);

        SIPythonAntlrParser parser = new SIPythonAntlrParser(stream);
        lexer.setMCParser(parser);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new de.monticore.antlr4.MCErrorListener(parser));
        parser.setFilename(fileName);
        setError(false);
        return parser;
    }

    public static TokenStream getTokensWithPreprocessing(SIPythonAntlrLexer lexer){
        WhitespacePreprocessingTokenSource ws = new WhitespacePreprocessingTokenSource(
            lexer,
            new CommonToken(SIPythonAntlrLexer.STATEMENT_END, PythonPreprocessor.STATEMENT_END),
            new CommonToken(SIPythonAntlrLexer.BLOCK_START, PythonPreprocessor.BLOCK_START),
            new CommonToken(SIPythonAntlrLexer.BLOCK_END, PythonPreprocessor.BLOCK_END)
        );
        return new BufferedTokenStream(ws);
    }
}
