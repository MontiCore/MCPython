package de.monticore.python;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._parser.PythonAntlrLexer;
import de.monticore.python._parser.PythonParser;
import de.monticore.python._parser.PythonParserTOP;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.junit.jupiter.api.Test;

import static de.monticore.python.PythonPreprocessor.*;
import static org.junit.jupiter.api.Assertions.*;

public class PythonPreprocessorTest {

    protected ASTPythonScript parseWithPreprocessing(String s){
        try {
            return PythonMill.parser().parse_String(s).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected List<Token> tokenizeWithPreprocessing(String text) throws IOException {
        PythonAntlrLexer lexer = new PythonAntlrLexer(org.antlr.v4.runtime.CharStreams.fromReader(new StringReader(text)));
        BufferedTokenStream stream = PythonParser.getPreprocessedTokenStream(lexer);

        return readAll(stream.getTokenSource());
    }

    protected List<Token> tokenizeNoPreprocessing(String text) throws IOException {
        PythonAntlrLexer lexer = new PythonAntlrLexer(org.antlr.v4.runtime.CharStreams.fromReader(new StringReader(text)));
        return readAll(lexer).stream().filter(t -> t.getChannel() != 1).collect(Collectors.toList());
    }

    private static List<Token> readAll(TokenSource tokenSource) {
        List<Token> tokens = new ArrayList<>();

        Token curToken = tokenSource.nextToken();
        while (curToken.getType() != -1){
            tokens.add(curToken);
            curToken = tokenSource.nextToken();
        }
        return tokens;
    }

    @Test
    public void testSingleLineBlock() throws IOException {
        String input = "if x > 0:\n  print(x)";
        String expectedOutput = "if x>0:{\n  print(x);\n}".replace("{", BLOCK_START).replace("}", BLOCK_END).replace(";", STATEMENT_END);
        assertTrue(parseWithoutPreprocessing(expectedOutput).deepEquals(parseWithPreprocessing(input)));
    }

    @Test
    public void testEmptyLineAfterBlockStart() throws IOException {
        String input = "class Foo:\n\n  def bar(self):\n    print('bla')";
        String expectedOutput = "class Foo:{ def bar(self):{ print('bla'); }}".replace("{", BLOCK_START).replace("}", BLOCK_END).replace(";", STATEMENT_END);
        assertTrue(parseWithoutPreprocessing(expectedOutput).deepEquals(parseWithPreprocessing(input)));
    }

    @Test
    public void testMultipleLineBlock() throws IOException {
        String input = "if x > 0:\n  print(x)\n  print(y)";
        String expectedOutput = "if x > 0:{\n  print(x);\n  print(y);\n}".replace("{", BLOCK_START).replace("}", BLOCK_END).replace(";", STATEMENT_END);
        assertTrue(parseWithoutPreprocessing(expectedOutput).deepEquals(parseWithPreprocessing(input)));
    }

    @Test
    public void testNestedBlocks() throws IOException {
        String input = "if x > 0:\n  if y > 0:\n    print(x)\n    print(y)";
        String expectedOutput = "if x > 0:{\n  if y > 0:{\n    print(x);\n    print(y);\n  }\n}".replace("{", BLOCK_START).replace("}", BLOCK_END).replace(";", STATEMENT_END);
        assertTrue(parseWithoutPreprocessing(expectedOutput).deepEquals(parseWithPreprocessing(input)));
    }

    @Test
    public void testIndentedBlocks() throws IOException {
        String input = "if x > 0:\n  print(x)\n  if y > 0:\n    print(y)";
        String expectedOutput = "if x > 0:{\n  print(x);\n  if y > 0:{\n    print(y);\n  }\n}".replace("{", BLOCK_START).replace("}", BLOCK_END).replace(";", STATEMENT_END);
        assertTrue(parseWithoutPreprocessing(expectedOutput).deepEquals(parseWithPreprocessing(input)));
    }

    @Test
    public void testMultipleBlocks() throws IOException {
        String input = "if x > 0:\n  print(x)\nfor i in range(10):\n  print(i)";
        String expectedOutput = "if x > 0:{\n  print(x);\n}\nfor i in range(10):{\n  print(i);\n}".replace("{", BLOCK_START).replace("}", BLOCK_END).replace(";", STATEMENT_END);
        assertTrue(parseWithoutPreprocessing(expectedOutput).deepEquals(parseWithPreprocessing(input)));
    }

    @Test
    public void testNoBlocks() throws IOException {
        String input = "print('Hello World')";
        String expectedOutput = "print('Hello World');".replace("{", BLOCK_START).replace("}", BLOCK_END).replace(";", STATEMENT_END);
        assertTrue(parseWithoutPreprocessing(expectedOutput).deepEquals(parseWithPreprocessing(input)));
    }

    @Test
    public void testBlockThenNeutral() throws IOException {
        String input =
                "if x > 0:\n" +
                "  print(x)\n" +
                "for i in range(10):\n" +
                "  print(i)\n" +
                "print('test')";

        String inputOfExpected = (
                "if x > 0:{\n" +
                "  print(x);\n" +
                "}\n" +
                "for i in range(10):{\n" +
                "  print(i);\n" +
                "}\n" +
                "print('test');"
        ).replace("{", BLOCK_START).replace("}", BLOCK_END).replace(";", STATEMENT_END);

        assertTokenStreamsEqual(inputOfExpected, input);

        ASTPythonScript expected = parseWithoutPreprocessing(inputOfExpected);
        ASTPythonScript actual = parseWithPreprocessing(input);

        assertTrue(expected.deepEquals(actual));
    }

    public void assertTokenStreamsEqual(String inputOfExpected, String input) throws IOException {
        List<Token> expectedTokens = tokenizeNoPreprocessing(inputOfExpected);
        List<Token> tokensWithPreprocessing = tokenizeWithPreprocessing(input);

        for (int i = 0; i < Math.max(expectedTokens.size(), tokensWithPreprocessing.size()); i++) {
            if(i >= expectedTokens.size()){
                fail("No more expected tokens, got " + tokensWithPreprocessing.get(i));
            }

            if(i >= tokensWithPreprocessing.size()){
                fail("No more actual tokens, expected " + expectedTokens.get(i));
            }

            Token et = expectedTokens.get(i);
            Token t = tokensWithPreprocessing.get(i);

            if(!(et.getType() == t.getType() && Objects.equals(et.getText(), t.getText()))){
                fail("Tokens not identical. Expected\n" + et + " but got \n" + t);
            }
        }
    }

    public ASTPythonScript parseWithoutPreprocessing(String content) throws IOException {
        return new PythonParserTOP(){}.parse_String(content).get();
    }

    @Test
    public void testLineEnd() throws IOException {
        String input = "if x:\n  print('a')";
        List<Token> result = tokenizeWithPreprocessing(input);
        assertEquals(STATEMENT_END, result.get(result.size() - 2).getText());
    }
}
