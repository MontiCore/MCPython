package de.monticore.python._parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Processes tokens when outside any parentheses (whitespace/indentation is significant).
 */
public class WhitespaceSensitiveProcessor {
  private final Stack<Integer> indentStack = new Stack<>();
  private final List<Integer> indents;
  private final List<Integer> emptyLines;
  private final Token eolTokenProto;
  private final Token incIndentTokenProto;
  private final Token decIndentTokenProto;
  private final PreprocessingTokens preprocessingTokens;
  private final Pair<TokenSource, CharStream> source;
  private final TokenFactory<?> tokenFactory;

  public WhitespaceSensitiveProcessor(
      Pair<TokenSource, CharStream> source,
      List<Integer> indents,
      List<Integer> emptyLines,
      TokenFactory<?> tokenFactory,
      Token eolTokenProto,
      Token incIndentTokenProto,
      Token decIndentTokenProto,
      PreprocessingTokens preprocessingTokens
  ) {
    this.source = source;
    this.indents = indents;
    this.emptyLines = emptyLines;
    this.tokenFactory = tokenFactory;
    this.eolTokenProto = eolTokenProto;
    this.incIndentTokenProto = incIndentTokenProto;
    this.decIndentTokenProto = decIndentTokenProto;
    this.preprocessingTokens = preprocessingTokens;
  }

  /**
   * Processes a token when whitespace sensitive.
   * @param token The current raw token of the original lexer.
   * @param lastToken The previous token processed (needed for context and positioning).
   * @param lastEmittedToken The last non-hidden token emitted (needed for context).
   * @return A list of tokens to emit, where block start, block end, and statement end tokens are injected.
   *         Also line continuation tokens are removed.
   */
  public List<Token> process(Token token, Token lastToken, Token lastEmittedToken) {
    if (emptyLines.contains(token.getLine()) && token.getType() != -1) {
      return List.of(); // Skip tokens on lines marked as empty (comments, etc.)
    }

    if (token.getType() == preprocessingTokens.continueLineTokenType) {
      // Line continuation character '\' just before newline, ignore it
      // The actual newline handling happens when the WS token arrives
      return List.of();
    }

    if (token.getType() == preprocessingTokens.wsTokenType && "\n".equals(token.getText())) {
      if (lastToken != null && lastToken.getType() == preprocessingTokens.continueLineTokenType) {
        // Newline after a line continuation '\', treat as simple whitespace
        return List.of(token);
      } else {
        // Handle meaningful newline (indentation check)
        int indentChange = updateIndent(token);
        int lastLine = getLastLine(lastToken);
        int charPos = getCharPositionInLine(lastToken);

        if (indentChange > 0) {
          // Indent
          return List.of(createToken(incIndentTokenProto, lastToken, lastLine, charPos), token);
        } else if (indentChange < 0) {
          // Dedent
          List<Token> res = new ArrayList<>();
          // Emit EOL only if the last emitted token wasn't already a DEDENT (which implies EOL)
          if (lastEmittedToken == null || lastEmittedToken.getType() != decIndentTokenProto.getType()) {
            res.add(createToken(eolTokenProto, lastToken, lastLine, charPos));
          }
          // Keep original newline token
          res.add(token);
          // Add DEDENT tokens
          for (int i = 0; i < -indentChange; i++) {
            // Position DEDENTs logically after the newline's effects
            res.add(createToken(decIndentTokenProto, lastToken, lastLine, charPos));
          }
          return res;
        } else {
          // No change in indentation
          List<Token> res = new ArrayList<>();
          // Emit EOL only if the last emitted token wasn't already a DEDENT
          if (lastEmittedToken == null || lastEmittedToken.getType() != decIndentTokenProto.getType()) {
            res.add(createToken(eolTokenProto, lastToken, lastLine, charPos));
          }
          // Keep original newline token
          res.add(token);
          return res;
        }
      }
    } else if (token.getType() == -1) {
      Token eof = token;
      List<Token> res = new ArrayList<>();
      int lastLine = getLastLine(lastToken);
      int charPos = getCharPositionInLine(lastToken);

      // Ensure final statement ends if not already ended by DEDENT
      if (lastEmittedToken == null || lastEmittedToken.getType() != decIndentTokenProto.getType()) {
        res.add(createToken(eolTokenProto, lastToken, lastLine, charPos));
      }

      // Close all open indentation levels
      while (!indentStack.isEmpty()) {
        indentStack.pop();
        res.add(createToken(decIndentTokenProto, lastToken, lastLine, charPos));
      }

      res.add(eof);

      return res;
    } else {
      // Any other token is just emitted
      return List.of(token);
    }
  }

  protected int updateIndent(Token token) {
    int oldIndent = indentStack.isEmpty() ? 0 : indentStack.peek();
    int curLine = token.getLine();

    while (emptyLines.contains(curLine + 1)) {
      curLine++;
    }

    int newIndent = (indents.size() > curLine && curLine >= 0) ? indents.get(curLine) : 0;


    if (oldIndent > newIndent) {
      int dedents = 0;
      while (!indentStack.isEmpty()) {
        if (indentStack.peek() == newIndent) {
          break; // Found matching indent level
        } else if (indentStack.peek() < newIndent) {
          // Error condition: Dedented past a previous level
          System.err.println("Warning: Inconsistent dedent detected at line " + token.getLine());
          break;
        } else {
          indentStack.pop();
          dedents++;
        }
      }
      if(indentStack.isEmpty() && newIndent != 0) {
        System.err.println("Warning: Dedent does not match any outer indentation level at line " + token.getLine());
      }
      return -dedents;
    } else if (oldIndent < newIndent) {
      indentStack.push(newIndent);
      return 1;
    } else {
      return 0;
    }
  }

  private Token createToken(Token proto, Token lastToken, int line, int charPos) {
    int startIndex = (lastToken != null) ? lastToken.getStopIndex() + 1 : 0;
    int stopIndex = startIndex;

    return tokenFactory.create(source, proto.getType(), proto.getText(), Token.DEFAULT_CHANNEL, startIndex, stopIndex, line, charPos);
  }

  private int getLastLine(Token lastToken) {
    return lastToken != null ? lastToken.getLine() : 1;
  }

  private int getCharPositionInLine(Token lastToken) {
    return lastToken != null ? lastToken.getCharPositionInLine() + lastToken.getText().length() : 0;
  }
}