package de.monticore.python;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

}
