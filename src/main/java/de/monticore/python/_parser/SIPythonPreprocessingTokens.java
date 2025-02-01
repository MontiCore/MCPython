package de.monticore.python._parser;

import de.monticore.sipython._parser.SIPythonAntlrLexer;

public class SIPythonPreprocessingTokens extends PreprocessingTokens {
    public SIPythonPreprocessingTokens() {
        super(
                SIPythonAntlrLexer.CONTINUE_LINE_TOKEN,
                SIPythonAntlrLexer.WS,
                SIPythonAntlrLexer.LPAREN,
                SIPythonAntlrLexer.RPAREN,
                SIPythonAntlrLexer.LCURLY,
                SIPythonAntlrLexer.RCURLY,
                SIPythonAntlrLexer.LBRACK,
                SIPythonAntlrLexer.RBRACK,
                SIPythonAntlrLexer.STATEMENT_END,
                SIPythonAntlrLexer.BLOCK_START,
                SIPythonAntlrLexer.BLOCK_END
        );
    }
}
