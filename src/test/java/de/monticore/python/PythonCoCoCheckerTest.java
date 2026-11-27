/* (c) https://github.com/MontiCore/monticore */
package de.monticore.python;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._cocos.PythonCoCoChecker;
import de.monticore.python._cocos.PythonExpressionCoco;
import de.monticore.python._parser.PythonParser;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PythonCoCoCheckerTest {
  
  @Test
  public void parseInvalidExpressionsFromComponentGrammar() throws IOException {
    LogStub.init();
    expectCoCoFailOnString("i++\n");
    expectCoCoFailOnString("i--\n");
    expectCoCoFailOnString("--i\n");
    expectCoCoFailOnString("++i\n");
  }
  
  // PythonExpressionCoco is not part of the default CoCos, so it is run on its own
  @Test
  public void invalidVariableDeclaration() throws IOException {
    LogStub.init();
    Log.enableFailQuick(false);
    assertTrue(runExpressionCocoOnString("1 = x\n").stream().anyMatch(f -> f.isError() && f.getMsg()
        .startsWith("Invalid variable declaration")));
    assertTrue(runExpressionCocoOnString("x = 1\n").stream().noneMatch(Finding::isError));
    assertTrue(runExpressionCocoOnString("1 += x\n").stream().noneMatch(Finding::isError));
  }
  
  private List<Finding> runExpressionCocoOnString(String model) throws IOException {
    Log.clearFindings();
    ASTPythonScript ast = new PythonParser().parse_String(model).get();
    PythonCoCoChecker checker = new PythonCoCoChecker();
    checker.addCoCo(new PythonExpressionCoco());
    checker.checkAll(ast);
    return new ArrayList<>(Log.getFindings());
  }
  
  public List<Finding> runCoCosOnString(String model) throws IOException {
    Log.clearFindings();
    PythonTool tool = new PythonTool();
    var parser = new PythonParser();
    ASTPythonScript ast = parser.parse_String(model).get();
    tool.runDefaultCoCos(ast);
    return new ArrayList<>(Log.getFindings());
  }
  
  public void expectCoCoFailOnString(String model) throws IOException {
    List<Finding> findings = runCoCosOnString(model);
    assertFalse(findings.isEmpty());
  }
  
}
