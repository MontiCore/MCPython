package de.monticore.python._parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class StateBasedWhitespacePreprocessingTokenSource {
    protected final Lexer delegate;
    protected final Token eolTokenProto;
    protected final Token incIndentTokenProto;
    protected final Token decIndentTokenProto;
    protected final PreprocessingTokens preprocessingTokens;

    Stack<Character> parenStack = new Stack<>();
    Stack<Integer> indentStack = new Stack<>();
    Token lastToken;
    Token lastEmittedToken;
    List<Integer> indents;
    List<Integer> emptyLines;

    private final List<Integer> openingParens;
    private final List<Integer> closingParens;
    private final Pair<TokenSource, CharStream> source;

    public StateBasedWhitespacePreprocessingTokenSource(
            Pair<TokenSource, CharStream> source,
            List<Integer> indents,
            List<Integer> emptyLines,
            Lexer delegate,
            Token eolTokenProto,
            Token incIndentTokenProto,
            Token decIndentTokenProto,
            PreprocessingTokens preprocessingTokens
    ){
        this.source = source;
        this.indents = indents;
        this.emptyLines = emptyLines;
        this.delegate = delegate;
        this.eolTokenProto = eolTokenProto;
        this.incIndentTokenProto = incIndentTokenProto;
        this.decIndentTokenProto = decIndentTokenProto;
        this.preprocessingTokens = preprocessingTokens;

        openingParens = List.of(preprocessingTokens.lparenTokenType,
                preprocessingTokens.lCurlyParenTokenType,
                preprocessingTokens.lSquareParenTokenType);

        closingParens = List.of(preprocessingTokens.rparenTokenType,
                preprocessingTokens.rCurlyParenTokenType,
                preprocessingTokens.rSquareParenTokenType);
    }

    public List<Token> process(Token token){
        List<Token> res = new ArrayList<>();

        if(parenStack.isEmpty()){
            res.addAll(processNoParen(token));
        }else{
            res.addAll(processInParen(token));
        }
        lastToken = token;
        if(!res.isEmpty()){
            lastEmittedToken = res.get(res.size() - 1);
        }
        return res;
    }

    protected List<Token> processNoParen(Token token) {
        if(emptyLines.contains(token.getLine()) && token.getType() != -1){
            return List.of();
        }

        if(token.getType() == preprocessingTokens.continueLineTokenType){
            lastToken = token;
            return List.of();
        }

        if(openingParens.contains(token.getType())){
            parenStack.push(token.getText().charAt(0));
            return List.of(token);
        }else if(token.getType() == preprocessingTokens.wsTokenType && "\n".equals(token.getText())){
            if(lastToken != null && lastToken.getType() == preprocessingTokens.continueLineTokenType){
                // Continue line after \
                return List.of(token);
            }else {
                // Handle newline
                int indentChange = updateIndent(token);
                if (indentChange > 0) {
                    return List.of(createToken(incIndentTokenProto), token);
                } else if (indentChange < 0) {
                    List<Token> res = new ArrayList<>();
                    if(lastEmittedToken == null || lastEmittedToken.getType() != decIndentTokenProto.getType()) {
                        res.add(createToken(eolTokenProto));
                    }
                    res.add(token);
                    for(int i = 0; i < -indentChange; i++){
                        res.add(createToken(decIndentTokenProto));
                    }
                    return res;
                } else {
                    List<Token> res = new ArrayList<>();
                    if(lastEmittedToken == null || lastEmittedToken.getType() != decIndentTokenProto.getType()) {
                        res.add(createToken(eolTokenProto));
                    }
                    res.add(token);
                    return res;
                }
            }
        } else if(token.getType() == -1) {
            // close all scopes @ EOF
            List<Token> res = new ArrayList<>();
            if(lastEmittedToken == null || lastEmittedToken.getType() != decIndentTokenProto.getType()) {
                res.add(createToken(eolTokenProto));
            }
            for (Integer i : indentStack) {
                res.add(createToken(decIndentTokenProto));
            }
            res.add(token);

            // statement end
            res.add(createToken(eolTokenProto));

            return res;
        } else {
            return List.of(token);
        }
    }

    protected List<Token> processInParen(Token token){
        if(openingParens.contains(token.getType())){
            parenStack.push(token.getText().charAt(0));
            return List.of(token);
        }else if(closingParens.contains(token.getType())){
            parenStack.pop();
            return List.of(token);
        } else if(openingParens.contains(token.getType())){
          parenStack.push(token.getText().charAt(0));
          return List.of(token);
        } else{
            return List.of(token);
        }
    }

    protected int updateIndent(Token token){
        // TODO: continue here: last line might be further away, since indents in parenthesis or after line continuations are not counted
        int oldIndent = indentStack.isEmpty() ? 0 : indentStack.peek();
        int curLine = token.getLine();
        while (emptyLines.contains(curLine + 1)){
            curLine++;
        }

        int newIndent = indents.size() > curLine ? indents.get(curLine) : 0;

        if(oldIndent > newIndent){
            int i = 0;
            while(!indentStack.isEmpty()){
                if(indentStack.peek() == newIndent){
                    break;
                }else{
                    indentStack.pop();
                    i--;
                }
            }
            return i;
        }else if(oldIndent < newIndent){
            indentStack.push(newIndent);
            return 1;
        }else{
            return 0;
        }
    }

    private Token createToken(Token proto) {
        int stopIndex = lastToken != null ? lastToken.getStopIndex() : 0;
        int charPositionInLine = lastToken != null ? lastToken.getCharPositionInLine() + lastToken.getText().length() : 0;
        return getTokenFactory().create(source, proto.getType(), proto.getText(), Token.DEFAULT_CHANNEL, stopIndex, stopIndex + 1, getLastLine(), charPositionInLine);
    }

    private int getLastLine() {
        return lastToken != null ? lastToken.getLine() : 1;
    }

    public TokenFactory<?> getTokenFactory() {
        return delegate.getTokenFactory();
    }
}
