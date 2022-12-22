package de.monticore.python._parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Stack;
import java.util.stream.Collectors;

public class PythonParser extends PythonParserTOP {

    @Override
    protected PythonAntlrParser create(Reader reader) throws IOException {
        // Convert reader to String joined by \n
        var asString = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));

        // Then, convert to C-language style python
        var cPython = addBracketsToPythonBlocks(asString);

        // Finally, return the original parser
        return super.create(new StringReader(cPython));
    }

    @Override
    protected PythonAntlrParser create(String fileName) throws IOException {
        return this.create(new FileReader(fileName));
    }
    
    /**
     * Replaces indentation (w/ spaces, no tabs) based python blocks by C-style bracketed blocks.
     * @param python A python script
     * @return The python script with C-style blocks
     */
    public static String addBracketsToPythonBlocks(String python) {
        // Keep track of current block indentation
        var blocks = new Stack<Integer>();
        blocks.push(0);

        // Retrieve all lines of python code
        var lines = python.lines().collect(Collectors.toList());

        // Create a list for resulting lines
        var result = new ArrayList<String>(lines.size());

        // Loop over lines
        for (var line: lines) {
            if (line.isBlank()) {
                // Add empty lines directly
                result.add(line);
                continue;
            }

            // Count the current line indentation
            var currentBlock = 0;
            while (line.charAt(currentBlock) == ' ')
                currentBlock++;
            
            // Get the previous line indentation
            var lastBlock = blocks.peek();

            // Compare the line indentations
            if (lastBlock == currentBlock) {
                // Still in the same block => simply add the result
                result.add(line);
            } else if (lastBlock < currentBlock) {
                // Current block is more indented
                // Push current block indent size on stack
                blocks.push(currentBlock);
                // Add an opening bracket to the previous line
                var lastLine = result.remove(result.size() - 1);
                lastLine += "{";
                result.add(lastLine);
                // Add the current line
                result.add(line);
            } else {
                // Current block is less indented
                // Close all previous blocks until current level is reached
                Integer nextBlock;
                do {
                    blocks.pop();
                    nextBlock = blocks.peek();

                    // Add closing bracket at the level of the next block
                    result.add(" ".repeat(nextBlock) + "}");
                } while (nextBlock > currentBlock);

                // Add the current block only if not already on the stack
                if (currentBlock != nextBlock)
                    blocks.push(currentBlock);

                // Add the current line
                result.add(line);
            }
        }

        // Close all remaining blocks
        Integer nextBlock;
        do {
            blocks.pop();
            nextBlock = blocks.peek();

            result.add(" ".repeat(nextBlock) + "}");
        } while (nextBlock > 0);

        // Concatenate result with line breaks
        return result.stream().collect(Collectors.joining("\n"));
    }

}
