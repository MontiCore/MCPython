package de.monticore.siphytonunits.generator;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.sipython.SIPythonMill;
import de.monticore.sipython._parser.SIPythonParser;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;

public class Generator {
    public static String generate(String modelPath, String fullName, String outputPath) {
        ASTPythonScript astPythonScript = parseModel(modelPath, fullName);

        try {
            SIPythonMill.scopesGenitorDelegator().createFromAST(astPythonScript);
        } catch (Exception e) {
            e.printStackTrace();
            Log.error("0xE6548322 Cannot build symbol table");
        }

        return PrintAsSIPythonScript.printAsSIPythonScript(astPythonScript);
    }

    private static ASTPythonScript parseModel(String modelPath, String name) {
        SIPythonParser parser = new SIPythonParser();
        Optional<ASTPythonScript> res = Optional.empty();
        try {
            res = parser.parsePythonScript(modelPath + "/" + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (res.isEmpty()) {
            Log.error("0xE6548321 Cannot find or parse class " + modelPath + "/" + name);
        }
        return res.get();
    }
}
