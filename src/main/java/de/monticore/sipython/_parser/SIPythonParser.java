package de.monticore.sipython._parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.stream.Collectors;

import de.monticore.python.PythonPreprocessor;

public class SIPythonParser extends SIPythonParserTOP {

    @Override
    protected SIPythonAntlrParser create(Reader reader) throws IOException {
        // Convert reader to String joined by \n
        var asString = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));

        // Then, convert to C-language style python
        var cPython = PythonPreprocessor.process(asString);

        // Finally, return the original parser
        return super.create(new StringReader(cPython));
    }

    @Override
    protected SIPythonAntlrParser create(String fileName) throws IOException {
        return this.create(new FileReader(fileName));
    }

}
