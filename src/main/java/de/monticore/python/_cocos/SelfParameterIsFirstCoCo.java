package de.monticore.python._cocos;

import de.monticore.python._ast.ASTClassFunctionParameters;
import de.se_rwth.commons.logging.Log;

public class SelfParameterIsFirstCoCo implements PythonASTClassFunctionParametersCoCo {
  @Override
  public void check(ASTClassFunctionParameters node) {
    for (int i = 0; i < node.getFunctionParameterList().size(); i++) {
      var c = node.getFunctionParameter(i);
      if(c.getName().equals("self") && i != 0){
        Log.error("Parameter 'self' must be the first parameter", c.get_SourcePositionStart(), c.get_SourcePositionEnd());
      }
    }
  }
}
