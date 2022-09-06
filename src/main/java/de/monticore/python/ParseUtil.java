package de.monticore.python;

import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ParseUtil {
    public static void main(String[] args) throws IOException {
        Log.enableFailQuick(false);
        Files
                .walk(Paths.get(args[0]))
                .filter(p -> p.toFile().isFile())
                .filter(p -> p.toFile().getName().endsWith(".py"))
                .forEach(p -> {
                    try {
                        System.out.println("Parsing file:///" + p.toFile().toString().replace("\\", "/"));
                        PythonMill.parser().parse(p.toString());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
