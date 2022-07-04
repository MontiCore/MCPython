package de.monticore.sipython.generator;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.sipython.SIPythonMill;
import de.monticore.sipython.SIPythonTool;
import de.monticore.sipython._parser.SIPythonParser;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class Generator {

    public static void generate(String modelPath, String fullName, String outputPath) {
        String name = fullName
            .substring(0, fullName.lastIndexOf("."))
            .replace(".","/")
            .replace("\\","/")
            + ".sipy";
        ASTPythonScript ast = parseModel(modelPath, name);

        SIPythonTool tool = new SIPythonTool();

        tool.runDefaultCoCos(ast);

        try {
            SIPythonMill.scopesGenitorDelegator().createFromAST(ast);
        } catch (Exception e) {
            e.printStackTrace();
            Log.error("0xE6548322 Cannot build symbol table");
        }

        String print = PrintAsSIPythonScript.printAsSIPythonScript(ast);

        String filePath = outputPath;
        String scriptName;
        if (fullName.contains("/")) {
            scriptName = fullName.substring(fullName.lastIndexOf("/") + 1, fullName.lastIndexOf("."));
            filePath += "/" + fullName.substring(0, fullName.lastIndexOf("/"));
        } else {
            scriptName = fullName.substring(0, fullName.lastIndexOf("."));
        }

        File pathFile = new File(filePath);
        if (!pathFile.exists())
            pathFile.mkdirs();

        try {
            FileWriter writer = new FileWriter(filePath + "/" + scriptName + ".py");
            writer.write(print);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            Log.error("0xE6548321 Cannot find or parse script " + modelPath + "/" + name);
        }
        return res.get();
    }
}
