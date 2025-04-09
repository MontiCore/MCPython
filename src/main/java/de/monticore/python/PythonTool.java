package de.monticore.python;

import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTDecPrefixExpressionCoCo;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTDecSuffixExpressionCoCo;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTIncPrefixExpressionCoCo;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTIncSuffixExpressionCoCo;
import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._cocos.NoInvalidExpressionUsedCoCo;
import de.monticore.python._cocos.PythonCoCoChecker;

public class PythonTool extends PythonToolTOP {
    public void runDefaultCoCos(ASTPythonScript ast){
      PythonCoCoChecker checker = new PythonCoCoChecker();
      checker.addCoCo((AssignmentExpressionsASTDecPrefixExpressionCoCo) new NoInvalidExpressionUsedCoCo());
      checker.addCoCo((AssignmentExpressionsASTIncPrefixExpressionCoCo) new NoInvalidExpressionUsedCoCo());
      checker.addCoCo((AssignmentExpressionsASTDecSuffixExpressionCoCo) new NoInvalidExpressionUsedCoCo());
      checker.addCoCo((AssignmentExpressionsASTIncSuffixExpressionCoCo) new NoInvalidExpressionUsedCoCo());

      checker.checkAll(ast);
    }
}