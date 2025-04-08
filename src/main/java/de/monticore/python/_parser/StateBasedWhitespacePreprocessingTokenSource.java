package de.monticore.python._parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * A TokenSource wrapper that preprocesses whitespace based on state
 * (inside or outside parentheses) to handle Python's indentation
 * and emit synthetic INDENT/DEDENT/EOL tokens.
 */
public class StateBasedWhitespacePreprocessingTokenSource {
  private final WhitespaceSensitiveProcessor sensitiveProcessor;

  private final Stack<Character> parenStack = new Stack<>();
  private final List<Integer> openingParens;
  private final List<Integer> closingParens;
  private Token lastToken;
  private Token lastEmittedToken;

  public StateBasedWhitespacePreprocessingTokenSource(
      Pair<TokenSource, CharStream> source,
      List<Integer> indents,
      List<Integer> emptyLines,
      Lexer delegate,
      Token eolTokenProto,
      Token incIndentTokenProto,
      Token decIndentTokenProto,
      PreprocessingTokens preprocessingTokens
  ) {
    this.openingParens = List.of(preprocessingTokens.lparenTokenType,
        preprocessingTokens.lCurlyParenTokenType,
        preprocessingTokens.lSquareParenTokenType);

    this.closingParens = List.of(preprocessingTokens.rparenTokenType,
        preprocessingTokens.rCurlyParenTokenType,
        preprocessingTokens.rSquareParenTokenType);

    this.sensitiveProcessor = new WhitespaceSensitiveProcessor(
        source, indents, emptyLines, delegate.getTokenFactory(),
        eolTokenProto, incIndentTokenProto, decIndentTokenProto,
        preprocessingTokens
    );

    this.lastToken = null;
    this.lastEmittedToken = null;
  }

  public List<Token> process(Token token) {
    List<Token> res;
    boolean stackWasEmpty = parenStack.isEmpty();

    // Update parenStack
    if (openingParens.contains(token.getType())) {
      parenStack.push(token.getText().charAt(0));
    } else if (closingParens.contains(token.getType())) {
      if (!parenStack.isEmpty()) {
        parenStack.pop();
      }
    }

    // the changed stack has no impact on the current token
    if (stackWasEmpty) {
      // whitespace sensitive
      res = sensitiveProcessor.process(token, lastToken, lastEmittedToken);
    } else {
      // whitespace insensitive
      return List.of(token);
    }

    // bookkeeping of emitted tokens
    lastToken = token;
    if (res != null && !res.isEmpty()) {
      for (int i = res.size() - 1; i >= 0; i--) {
        Token emitted = res.get(i);
        if (emitted != null && emitted.getChannel() != Token.HIDDEN_CHANNEL) {
          lastEmittedToken = emitted;
          break;
        }
      }
    }
    return (res != null) ? res : new ArrayList<>();
  }
}
