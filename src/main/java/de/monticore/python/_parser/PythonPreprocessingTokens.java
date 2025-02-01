package de.monticore.python._parser;

public class PythonPreprocessingTokens extends PreprocessingTokens {
    public PythonPreprocessingTokens() {
        super(
                PythonAntlrLexer.CONTINUE_LINE_TOKEN,
                PythonAntlrLexer.WS,
                PythonAntlrLexer.LPAREN,
                PythonAntlrLexer.RPAREN,
                PythonAntlrLexer.LCURLY,
                PythonAntlrLexer.RCURLY,
                PythonAntlrLexer.LBRACK,
                PythonAntlrLexer.RBRACK,
                PythonAntlrLexer.STATEMENT_END,
                PythonAntlrLexer.BLOCK_START,
                PythonAntlrLexer.BLOCK_END
        );
    }
}
