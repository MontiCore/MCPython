package de.monticore.python;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Stack;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Ordering;

public class PythonPreprocessor {

    public static String process(String python) {
        String formatted = formatPython(python);
        String singleLineStrings = multilineStringToSingleLine(formatted);
        String withSemicolons = addStatementEnds(singleLineStrings);
        return addBracketsToPythonBlocks(withSemicolons);
    }

    public static String formatPython(String python) {
        Process formatProcess;
        try {
            formatProcess = new ProcessBuilder("python", "-m", "yapf", "--style", "src/main/resources/formatter/.style.yapf").start();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        var in = formatProcess.getInputStream();
        var bufferedIn = new BufferedInputStream(in);
        var reader = new Scanner(bufferedIn, Charset.forName("UTF-8"));

        var out = formatProcess.getOutputStream();
        var bufferedOut = new BufferedOutputStream(out);
        var writer = new PrintWriter(bufferedOut);

        python.lines().forEach(writer::println);
        writer.flush();
        writer.close();

        var result = new StringBuilder();

        while (reader.hasNextLine()) {
            var line = reader.nextLine();
            result.append(line + "\n");
        }
        reader.close();

        formatProcess.onExit().join();

        if (formatProcess.exitValue() != 0) {
            throw new RuntimeException("The formatter exited with code " + formatProcess.exitValue() + ".");
        }

        return result.toString();
    }

    public static String multilineStringToSingleLine(String python) {
        var regex = "('''[\\s\\S]*?''')|(\\\"\\\"\\\"[\\s\\S]*?\\\"\\\"\\\")";

        var pattern = Pattern.compile(regex, Pattern.MULTILINE);
        var matcher = pattern.matcher(python);

        var results = matcher.results().collect(Collectors.toList());

        // reverse sort by match start
        Comparator<MatchResult> comp = (a, b) -> -Integer.compare(a.start(), b.start());
        var sortedResults = Ordering.from(comp).sortedCopy(results);

        var sb = new StringBuilder(python);
        for (var result : sortedResults) {
            var start = result.start() + 3;
            var end = result.end() - 3;
            var inner = sb.substring(start, end);
            var innerNoNewLine = inner.replace("\r\n", "\\r\\n");
            innerNoNewLine = innerNoNewLine.replace("\n", "\\n");
            sb.replace(start, end, innerNoNewLine);
        }

        return sb.toString();
    }

    public static final String STATEMENT_END = "\u204f";

    public static String addStatementEnds(String python) {
        Function<String, String> mapper = s -> {
            if (s.isBlank()) return s;
            if (s.endsWith(":")) return s;
            return s + STATEMENT_END;
        };

        var result = python.lines().map(mapper).collect(Collectors.joining("\n"));
        return result;
    }

    public static final String BLOCK_START = "\u2983";
    public static final String BLOCK_END = "\u2984";
    
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
                lastLine += BLOCK_START;
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
                    result.add(" ".repeat(nextBlock) + BLOCK_END);
                } while (nextBlock > currentBlock);

                // Add the current block only if not already on the stack
                if (currentBlock != nextBlock)
                    blocks.push(currentBlock);

                // Add the current line
                result.add(line);
            }
        }

        // Close all remaining blocks
        Integer nextBlock = blocks.peek();
        if (nextBlock != 0) do {
            blocks.pop();
            nextBlock = blocks.peek();

            result.add(" ".repeat(nextBlock) + BLOCK_END);
        } while (nextBlock > 0);

        // Concatenate result with line breaks
        return result.stream().collect(Collectors.joining("\n"));
    }

}
