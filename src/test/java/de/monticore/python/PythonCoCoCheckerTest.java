/* (c) https://github.com/MontiCore/monticore */
package de.monticore.python;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._cocos.PythonCoCoChecker;
import de.monticore.python._cocos.PythonExpressionCoco;
import de.monticore.python._cocos.PythonVariableOrFunctionOrClassExistsCoco;
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
  
  @Test
  public void namesFromAliasesAndTypeAliasesExist() throws IOException {
    LogStub.init();
    Log.enableFailQuick(false);
    String model = "type X = int\n" + "isinstance(v, X)\n" + "try:\n" + "    f()\n"
        + "except E as e:\n" + "    print(e)\n" + "match p:\n" + "    case Point() as y:\n"
        + "        print(y)\n";
    List<String> unknown = runNameExistsCocoOnString(model).stream().filter(Finding::isError).map(
        Finding::getMsg).filter(m -> m.startsWith("Variable, Function or Class")).toList();
    // only the names that are really not defined in the model
    assertTrue(unknown.stream().noneMatch(m -> m.contains(" X ") || m.contains(" e ") || m.contains(
        " y ")), unknown.toString());
    assertTrue(unknown.stream().anyMatch(m -> m.contains(" v ")), unknown.toString());
  }
  
  @Test
  public void namesBoundSeveralTimesExist() throws IOException {
    LogStub.init();
    Log.enableFailQuick(false);
    // valid Python that binds the same function, class or alias name twice must not make the CoCo
    // throw
    List<String> models = List.of(
        "try:\n    f()\nexcept A as e:\n    try:\n        g()\n    except B as e:\n        print(e)\n",
        "match p:\n    case (A() as q) | (B() as q):\n        print(q)\n",
        "type X = int\ntype X = str\nprint(X)\n",
        "@overload\ndef o(a: int): ...\n@overload\ndef o(a: str): ...\no(1)\n",
        "class C:\n    @property\n    def x(self): return 1\n    @x.setter\n    def x(self, v): pass\n",
        "class K: pass\nclass K: pass\nK()\n");
    for (String model : models) {
      List<Finding> findings = assertDoesNotThrow(() -> runNameExistsCocoOnString(model), model);
      List<String> names = List.of(" e ", " q ", " X ", " o ", " x ", " K ");
      assertTrue(findings.stream().noneMatch(f -> names.stream().anyMatch(n -> f.getMsg().contains(
          n))), findings.toString());
    }
  }
  
  private List<Finding> runNameExistsCocoOnString(String model) throws IOException {
    PythonMill.reset();
    PythonMill.init();
    Log.clearFindings();
    ASTPythonScript ast = PythonMill.parser().parse_String(model).get();
    PythonMill.scopesGenitorDelegator().createFromAST(ast);
    PythonCoCoChecker checker = new PythonCoCoChecker();
    checker.addCoCo(new PythonVariableOrFunctionOrClassExistsCoco());
    checker.checkAll(ast);
    return new ArrayList<>(Log.getFindings());
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
