package de.monticore.python._parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.Pair;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class WhitespacePreprocessingTokenSource implements TokenSource {
  private final Lexer delegate;
  private final Token eolTokenProto;
  private final Token incIndentTokenProto;
  private final Token decIndentTokenProto;
  protected int lastLine = 1;
  protected int lastIndent = 0;
  protected Token lastToken;
  protected Pair<TokenSource, CharStream> source;
  protected Set<Token> alreadyProcessedEol = new HashSet<>();
  protected Set<Token> alreadyProcessedBlockStart = new HashSet<>();
  protected Set<Token> alreadyProcessedBlockEnd = new HashSet<>();
  LinkedList<Token> queue = new LinkedList<>();
  private int indentDepth = 0;
  private int parenDepth = 0;

  public WhitespacePreprocessingTokenSource(Lexer delegate, Token eolTokenProto, Token incIndentTokenProto, Token decIndentTokenProto) {
    this.delegate = delegate;
    this.eolTokenProto = eolTokenProto;
    this.incIndentTokenProto = incIndentTokenProto;
    this.decIndentTokenProto = decIndentTokenProto;
    source = new Pair<>(this, delegate.getInputStream());
  }

  private boolean isWhitespaceSensitive() {
    return parenDepth == 0;
  }

  @Override
  public Token nextToken() {
    Token res = null;

    if (!queue.isEmpty()) {
      res = queue.peek();
    }

    if (res == null || mustBeProcessed(res)) {
      Token token;
      if (res == null) {
        token = delegate.nextToken();
        queue.add(0, token);
        res = token;
      } else {
        token = res;
      }

      if (isOpeningParen(token)) {
        parenDepth++;
      } else if (isClosingParen(token)) {
        parenDepth--;
      } else {
        if (isWhitespaceSensitive()) {
          if (indentIncreased(token)) {
            indentDepth++;
            indentStops.push(getIndent(token));
            alreadyProcessedBlockStart.add(token);
            queue.add(0, createToken(incIndentTokenProto));
          } else if (indentDecreased(token)) {
            int curIndent = getIndent(token);
            while ((curIndent == 0 && !indentStops.isEmpty()) || (!indentStops.isEmpty() && indentStops.peek() > curIndent)){
              indentDepth--;
              queue.add(0, createToken(decIndentTokenProto));
              indentStops.pop();
            }

            alreadyProcessedBlockEnd.add(token);
          }
        }
        if (needsEol(token)) {
          alreadyProcessedEol.add(token);
          queue.add(0, createToken(eolTokenProto));
        }
      }
    }

    if (!queue.isEmpty()) {
      res = queue.poll();
    }

    if (isNewLine(res) && parenDepth == 0) {
      lastIndent = getIndent(res);
    }
    lastLine = res.getLine();
    lastToken = res;
    alreadyProcessedEol.add(res);
    return res;
  }

  private boolean mustBeProcessed(Token t) {
    return !alreadyProcessedEol.contains(t) || alreadyProcessedBlockEnd.contains(t);
  }

  private Token createToken(Token proto) {
    int stopIndex = lastToken != null ? lastToken.getStopIndex() : 0;
    int charPositionInLine = lastToken != null ? lastToken.getCharPositionInLine() + lastToken.getText().length() : 0;
    return getTokenFactory().create(source, proto.getType(), proto.getText(), delegate.getChannel(), stopIndex, stopIndex + 1, lastLine, charPositionInLine);
  }

  protected boolean needsEol(Token token) {
    if(parenDepth > 0){
      return false;
    }

    if (alreadyProcessedEol.contains(token)) {
      return false;
    }

    if (
        lastToken != null && (
            lastToken.getText().equals(":")
                || lastToken.getType() == eolTokenProto.getType()
                || lastToken.getType() == incIndentTokenProto.getType()
                || lastToken.getType() == decIndentTokenProto.getType()
        )
    ) {
      return false;
    }
    return token.getType() == -1 || isNewLine(token);
  }

  private boolean isNewLine(Token token) {
    return lastLine != token.getLine();
  }

  protected boolean indentIncreased(Token token) {
    if (alreadyProcessedBlockStart.contains(token)) {
      return false;
    }

    return isNewLine(token) && getIndent(token) > lastIndent;
  }

  Stack<Integer> indentStops = new Stack<>();

  // TODO: multiple decreases in on go possible
  // TODO: save current stops in stack
  protected boolean indentDecreased(Token token) {
    if (alreadyProcessedBlockEnd.contains(token)) {
      return false;
    }

    return isNewLine(token) && getIndent(token) < lastIndent;
  }

  protected int getIndent(Token token) {
    return token.getCharPositionInLine();
  }

  protected boolean isClosingParen(Token token) {
    return List.of(")", "}", "]").contains(token.getText());
  }

  protected boolean isOpeningParen(Token token) {
    return List.of("(", "{", "[").contains(token.getText());
  }

  @Override
  public int getLine() {
    if (!queue.isEmpty()) {
      return queue.peek().getLine();
    }

    return delegate.getLine();
  }

  @Override
  public int getCharPositionInLine() {
    if (!queue.isEmpty()) {
      return queue.peek().getCharPositionInLine();
    }

    return delegate.getCharPositionInLine();
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
  public TokenFactory<?> getTokenFactory() {
    return delegate.getTokenFactory();
  }

  @Override
  public void setTokenFactory(TokenFactory<?> factory) {
    delegate.setTokenFactory(factory);
  }
}
