package de.monticore.python;

import de.monticore.python._ast.ASTFunctionDeclaration;
import de.monticore.python._parser.PythonParser;
import de.se_rwth.commons.SourcePosition;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class SplitIntoSnippetsUtil {

  public static void main(String[] args) throws IOException {
    Path base = Paths.get(args[0]);
    File target = Paths.get(args[1]).toFile();

    if (target.exists()) {
      if (!target.isDirectory()) {
        System.out.println("Given target must be a directory!");
        System.exit(1);
      }
    } else {
      target.mkdirs();
    }

    Files
        .walk(base)
        .filter(p -> p.toFile().isFile())
        .filter(p -> p.toFile().getName().endsWith(".py"))
        .forEach(p -> {
          try {
            PythonParser parser = PythonMill.parser();
            List<String> lines = FileUtils.readLines(p.toFile(), StandardCharsets.UTF_8);
            String content = String.join("\n", lines);
            var ast = parser.parse_String(content).get();
            var relativePath = base.relativize(p);

            // Functions
            String moduleName = relativePath.toString().replaceAll(".py$", "");
            Map<String, ASTFunctionDeclaration> res = FqnMethodVisitor.getFqnToFunctionDecl(moduleName, ast);
            res.forEach((name, func) -> {
              String fqn = name.replace("\\", ".");
              SourcePosition startPos = func.get_SourcePositionStart();
              SourcePosition endPos = func.get_SourcePositionEnd();

              StringBuilder funcContent = new StringBuilder();
              for (int i = startPos.getLine(); i <= endPos.getLine(); i++) {
                funcContent.append(lines.get(i - 1)).append("\n");
              }

              String snippetContent = "FQN: " + fqn + "\n" + funcContent;
              File snippetFile = target.toPath().resolve(name + ".snippet").toFile();
              try {
                FileUtils.write(snippetFile, snippetContent, StandardCharsets.UTF_8);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });

            // Scripts(only top level statements. No classes, functions, ...)
            if(res.isEmpty()){
              SourcePosition startPos = ast.get_SourcePositionStart();
              SourcePosition endPos = ast.get_SourcePositionEnd();
              String fqn = moduleName.replace("\\", ".");

              StringBuilder funcContent = new StringBuilder();
              for (int i = startPos.getLine(); i <= endPos.getLine(); i++) {
                if(i < lines.size()) {
                  funcContent.append(lines.get(i - 1)).append("\n");
                }
              }

              String snippetContent = "FQN: " + fqn + "\n" + funcContent;
              File snippetFile = target.toPath().resolve(moduleName + ".snippet").toFile();
              try {
                FileUtils.write(snippetFile, snippetContent, StandardCharsets.UTF_8);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            }
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
  }

}
