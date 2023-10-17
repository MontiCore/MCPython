package de.monticore.python;

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ParseUtil {
    public static void main(String[] args) throws IOException {
        Log.enableFailQuick(false);
        Files
                .walk(Paths.get(args[0]))
                .filter(p -> p.toFile().isFile())
                .filter(p -> p.toFile().getName().endsWith(".py"))
                .forEach(p -> {
                    try {
                        Log.getFindings().clear();
                        System.out.println("Parsing file:///" + p.toFile().toString().replace("\\", "/"));
                        PythonMill.parser().parse(p.toString());
                      List<Finding> errors = Log.getFindings().stream().filter(Finding::isError).collect(Collectors.toList());
                      if(errors.size() > 0){
                        for (Finding error : errors) {
                          System.out.println(error);
                        }
                          System.out.println(errors + " errors while parsing");
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
