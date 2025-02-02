package de.monticore.python._parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class PreprocessingTokenSource implements TokenSource {
    Queue<Token> tokens = new ArrayDeque<>();
    StateBasedWhitespacePreprocessingTokenSource preprocessor;
    final TokenSource delegate;
    Token curToken;

    public PreprocessingTokenSource(
            Lexer delegate,
            Token eolTokenProto,
            Token incIndentTokenProto,
            Token decIndentTokenProto,
            PreprocessingTokens preprocessingTokens
    ) {
        this.delegate = delegate;
        CharStream is = delegate.getInputStream();

        String[] lines = getLines(is);
        List<Integer> indents = calculateIndents(lines);
        List<Integer> emptyLines = calculateEmptyLines(lines);

        preprocessor = new StateBasedWhitespacePreprocessingTokenSource(
            new Pair<>(delegate, is),
            indents,
            emptyLines,
            delegate,
            eolTokenProto,
            incIndentTokenProto,
            decIndentTokenProto,
            preprocessingTokens
        );
    }

    private static String[] getLines(CharStream is) {
        int marker = is.mark();
        String content = is.getText(new Interval(0, is.size()));
        is.release(marker);
        String[] lines = content.split("\r?\n");
        return lines;
    }

    protected List<Integer> calculateIndents(String[] lines) {
        List<Integer> indents = new ArrayList<>();
        for (String line : lines) {
            int i;
            for (i = 0; i < line.length(); i++) {
                if(!Character.isWhitespace(line.charAt(i))){
                    break;
                }
            }

            indents.add(i);
        }
        return indents;
    }

    protected List<Integer> calculateEmptyLines(String[] lines) {
        List<Integer> res = new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if(line.isBlank() || line.matches("\\s*#.*")){
                res.add(i + 1);
            }
        }

        return res;
    }

    @Override
    public Token nextToken() {
        Token token = _nextToken();
        while (token.getChannel() == Token.HIDDEN_CHANNEL){
            token = _nextToken();
        }
        return token;
    }

    List<Token> history = new ArrayList<>();
    protected Token _nextToken(){
        if(tokens.isEmpty()){
            Token token = delegate.nextToken();
            List<Token> tList = preprocessor.process(token);
            // Empty lines are ignored => maybe no return value
            while(tList.isEmpty()){
                tList = preprocessor.process(delegate.nextToken());
            }

            this.tokens.addAll(tList);
        }
        Token poll = tokens.poll();
        history.add(poll);
        //System.out.println(poll);
        return poll;
    }

    @Override
    public int getLine() {
        return curToken != null ? curToken.getLine() : 0;
    }

    @Override
    public int getCharPositionInLine() {
        return curToken != null ? curToken.getCharPositionInLine() : 0;
    }

    @Override
    public CharStream getInputStream() {
        return delegate.getInputStream();
    }

    @Override
    public String getSourceName() {
        return delegate.getSourceName();
    }

    @Override
    public void setTokenFactory(TokenFactory<?> factory) {
        delegate.setTokenFactory(factory);
    }

    @Override
    public TokenFactory<?> getTokenFactory() {
        return delegate.getTokenFactory();
    }
}
