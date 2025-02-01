package de.monticore.python._parser;

public class PreprocessingTokens {
    final int continueLineTokenType;
    final int wsTokenType;
    final int lparenTokenType;
    final int rparenTokenType;
    final int lCurlyParenTokenType;
    final int rCurlyParenTokenType;
    final int lSquareParenTokenType;
    final int rSquareParenTokenType;
    final int statementEndTokenType;
    final int blockStartTokenType;
    final int blockEndTokenType;

    public PreprocessingTokens(
            int continueLineTokenType,
            int wsTokenType,
            int lparenTokenType,
            int rparenTokenType,
            int lCurlyParenTokenType,
            int rCurlyParenTokenType,
            int lSquareParenTokenType,
            int rSquareParenTokenType,
            int statementEndTokenType,
            int blockStartTokenType,
            int blockEndTokenType
    ) {
        this.continueLineTokenType = continueLineTokenType;
        this.wsTokenType = wsTokenType;

        this.lparenTokenType = lparenTokenType;
        this.rparenTokenType = rparenTokenType;
        this.lCurlyParenTokenType = lCurlyParenTokenType;
        this.rCurlyParenTokenType = rCurlyParenTokenType;
        this.lSquareParenTokenType = lSquareParenTokenType;
        this.rSquareParenTokenType = rSquareParenTokenType;

        this.statementEndTokenType = statementEndTokenType;
        this.blockStartTokenType = blockStartTokenType;
        this.blockEndTokenType = blockEndTokenType;
    }
}
