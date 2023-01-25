package de.monticore.python;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import static de.monticore.python.PythonPreprocessor.*;

public class PythonPreprocessorTest {
    
    @Test
    public void testSingleLineBlock() {
        String input = "if x > 0:\n  print(x)";
        String expectedOutput = "if x > 0:{\n  print(x)\n}".replace("{", BLOCK_START).replace("}", BLOCK_END);
        assertEquals(expectedOutput, addBracketsToPythonBlocks(input));
    }

    @Test
    public void testMultipleLineBlock() {
        String input = "if x > 0:\n  print(x)\n  print(y)";
        String expectedOutput = "if x > 0:{\n  print(x)\n  print(y)\n}".replace("{", BLOCK_START).replace("}", BLOCK_END);
        assertEquals(expectedOutput, addBracketsToPythonBlocks(input));
    }

    @Test
    public void testNestedBlocks() {
        String input = "if x > 0:\n  if y > 0:\n    print(x)\n    print(y)";
        String expectedOutput = "if x > 0:{\n  if y > 0:{\n    print(x)\n    print(y)\n  }\n}".replace("{", BLOCK_START).replace("}", BLOCK_END);
        assertEquals(expectedOutput, addBracketsToPythonBlocks(input));
    }

    @Test
    public void testIndentedBlocks() {
        String input = "if x > 0:\n  print(x)\n  if y > 0:\n    print(y)";
        String expectedOutput = "if x > 0:{\n  print(x)\n  if y > 0:{\n    print(y)\n  }\n}".replace("{", BLOCK_START).replace("}", BLOCK_END);
        assertEquals(expectedOutput, addBracketsToPythonBlocks(input));
    }

    @Test
    public void testMultipleBlocks() {
        String input = "if x > 0:\n  print(x)\nfor i in range(10):\n  print(i)";
        String expectedOutput = "if x > 0:{\n  print(x)\n}\nfor i in range(10):{\n  print(i)\n}".replace("{", BLOCK_START).replace("}", BLOCK_END);
        assertEquals(expectedOutput, addBracketsToPythonBlocks(input));
    }

    @Test
    public void testNoBlocks() {
        String input = "print('Hello World')";
        String expectedOutput = "print('Hello World')";
        assertEquals(expectedOutput, addBracketsToPythonBlocks(input));
    }

    @Test
    public void testBlockThenNeutral() {
        String input = "if x > 0:\n  print(x)\nfor i in range(10):\n  print(i)\nprint('test')";
        String expectedOutput = "if x > 0:{\n  print(x)\n}\nfor i in range(10):{\n  print(i)\n}\nprint('test')".replace("{", BLOCK_START).replace("}", BLOCK_END);
        assertEquals(expectedOutput, addBracketsToPythonBlocks(input));
    }

    @Test
    public void testFormatting() {
        String input = "print('x')";
        String result = formatPython(input);
        assertNotNull(result);
    }

    @Test
    public void testFormattingIf() {
        String input = "if x: print('x')";
        String result = formatPython(input);
        assertTrue(result, result.lines().count() == 2);
    }

    @Test
    public void testFormattingFor() {
        String input = "for x in y: print('x')";
        String result = formatPython(input);
        assertTrue(result, result.lines().count() == 2);
    }

    @Test
    public void testFormattingDict() {
        String input = "x = {\n  a\n}";
        String result = formatPython(input);
        assertTrue(result, result.lines().count() == 1);
    }
    
    @Test
    public void testFormattingLargeSet() {
        String elements = "element,\n".repeat(30);
        String input = "x = {\n  " + elements + "\n}";
        String result = formatPython(input);
        assertTrue(result, result.lines().count() == 1);
    }
    
    @Test
    public void testFormattingLargeArray() {
        String elements = "element,\n".repeat(30);
        String input = "x = [\n  " + elements + "\n]";
        String result = formatPython(input);
        assertTrue(result, result.lines().count() == 1);
    }
    
    @Test
    public void testFormattingLargeDictSingleLine() {
        String elements = "'element': abc,\n".repeat(30);
        String input = "x = {\n  " + elements + "\n}";
        String result = formatPython(input);
        assertTrue(result, result.lines().count() == 1);
    }
    
    @Test
    public void testFormattingLargeDictMultiLine() {
        String elements = "'element':\n  abc,\n".repeat(30);
        String input = "x = {\n  " + elements + "\n}";
        String result = formatPython(input);
        assertTrue(result, result.lines().count() == 1);
    }

    @Test
    public void testFormattingShortMultilineString() {
        String input = "\'".repeat(3) + "abc\ndef" + "\'".repeat(3);
        String result = multilineStringToSingleLine(input);
        assertTrue(result, result.lines().count() == 1);
        assertEquals(result, "\'".repeat(3) + "abc\\ndef" + "\'".repeat(3));
    }

    @Test
    public void testFormattingBigMultilineString() {
        String elements = "abcdefghijklmnop\n".repeat(2);
        String input = "\"".repeat(3) + elements + "\"".repeat(3);
        String result = multilineStringToSingleLine(input);
        String expectedElements = "abcdefghijklmnop\\n".repeat(2);
        String expected = "\"".repeat(3) + expectedElements + "\"".repeat(3);
        assertTrue(result, result.lines().count() == 1);
        assertEquals(result, expected);
    }

    @Test
    public void testLineEnd() {
        String input = "if x:\n  print('a')";
        String result = addStatementEnds(input);
        assertTrue(result.endsWith(STATEMENT_END));
        assertFalse(result.substring(0, result.length() - 1).contains(STATEMENT_END));
    }

}
