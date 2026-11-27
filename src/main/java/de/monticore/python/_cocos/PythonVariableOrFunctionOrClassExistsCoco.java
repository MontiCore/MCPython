/* (c) https://github.com/MontiCore/monticore */
package de.monticore.python._cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisASTNameExpressionCoCo;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.python._symboltable.IPythonScope;
import de.se_rwth.commons.logging.Log;

public class PythonVariableOrFunctionOrClassExistsCoco implements
    ExpressionsBasisASTNameExpressionCoCo {
  
  @Override
  public void check(ASTNameExpression node) {
    IExpressionsBasisScope es = node.getEnclosingScope();
    if (!(es instanceof IPythonScope)) {
      return;
    }
    
    IPythonScope s = (IPythonScope) es;
    
    String name = node.getName();
    // Also names from "except E as e" / "case ... as y" and type aliases ("type X = int").
    // resolve...Many, because valid Python can bind a function, class or alias name more than once
    // (@overload, @property with @x.setter, nested excepts, "case (A() as q) | (B() as q)", a
    // redefined type alias), and the single resolve throws then.
    boolean exists = s.resolveVariable(name).isPresent() || !s.resolveFunctionMany(name).isEmpty();
    exists = exists || !s.resolvePythonClassMany(name).isEmpty();
    exists = exists || !s.resolveAliasMany(name).isEmpty();
    exists = exists || !s.resolveTypeRuleStatementMany(name).isEmpty();
    if (!exists) {
      String position = node.get_SourcePositionStart().toString();
      Log.error("Variable, Function or Class " + name + " does not exist " + position);
    }
  }
  
}
