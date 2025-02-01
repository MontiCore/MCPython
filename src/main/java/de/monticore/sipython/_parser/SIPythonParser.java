package de.monticore.sipython._parser;

import de.monticore.python.PythonPreprocessor;
import de.monticore.python._parser.PreprocessingTokenSource;
import de.monticore.python._parser.SIPythonPreprocessingTokens;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class SIPythonParser extends SIPythonParserTOP {
    public CommonTokenStream currentTokenStream;

    @Override
    protected SIPythonAntlrParser create(Reader reader) throws IOException {
        SIPythonAntlrLexer lexer = new SIPythonAntlrLexer(org.antlr.v4.runtime.CharStreams.fromReader(reader));
        CommonTokenStream stream = getTokensWithPreprocessing(lexer);
        currentTokenStream = stream;

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
        CommonTokenStream stream = getTokensWithPreprocessing(lexer);
        currentTokenStream = stream;

        SIPythonAntlrParser parser = new SIPythonAntlrParser(stream);
        lexer.setMCParser(parser);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new de.monticore.antlr4.MCErrorListener(parser));
        parser.setFilename(fileName);
        setError(false);
        return parser;
    }

    public static CommonTokenStream getTokensWithPreprocessing(SIPythonAntlrLexer lexer){
        PreprocessingTokenSource ws = new PreprocessingTokenSource(
                lexer,
                new CommonToken(SIPythonAntlrLexer.STATEMENT_END, PythonPreprocessor.STATEMENT_END),
                new CommonToken(SIPythonAntlrLexer.BLOCK_START, PythonPreprocessor.BLOCK_START),
                new CommonToken(SIPythonAntlrLexer.BLOCK_END, PythonPreprocessor.BLOCK_END),
                new SIPythonPreprocessingTokens()
        );
        return new CommonTokenStream(ws);
    }
}
