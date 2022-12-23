package de.monticore.python._parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import static de.monticore.python._parser.PythonParser.addBracketsToPythonBlocks;

public class PythonParserTest {
    
    @Test
    public void testSingleLineBlock() {
        String input = "if x > 0:\n  print(x)";
        String expectedOutput = "if x > 0:{\n  print(x)\n}";
        assertEquals(expectedOutput, addBracketsToPythonBlocks(input));
    }

    @Test
    public void testMultipleLineBlock() {
        String input = "if x > 0:\n  print(x)\n  print(y)";
        String expectedOutput = "if x > 0:{\n  print(x)\n  print(y)\n}";
        assertEquals(expectedOutput, addBracketsToPythonBlocks(input));
    }

    @Test
    public void testNestedBlocks() {
        String input = "if x > 0:\n  if y > 0:\n    print(x)\n    print(y)";
        String expectedOutput = "if x > 0:{\n  if y > 0:{\n    print(x)\n    print(y)\n  }\n}";
        assertEquals(expectedOutput, addBracketsToPythonBlocks(input));
    }

    @Test
    public void testIndentedBlocks() {
        String input = "if x > 0:\n  print(x)\n  if y > 0:\n    print(y)";
        String expectedOutput = "if x > 0:{\n  print(x)\n  if y > 0:{\n    print(y)\n  }\n}";
        assertEquals(expectedOutput, addBracketsToPythonBlocks(input));
    }

    @Test
    public void testMultipleBlocks() {
        String input = "if x > 0:\n  print(x)\nfor i in range(10):\n  print(i)";
        String expectedOutput = "if x > 0:{\n  print(x)\n}\nfor i in range(10):{\n  print(i)\n}";
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
        String expectedOutput = "if x > 0:{\n  print(x)\n}\nfor i in range(10):{\n  print(i)\n}\nprint('test')";
        assertEquals(expectedOutput, addBracketsToPythonBlocks(input));
    }

}
