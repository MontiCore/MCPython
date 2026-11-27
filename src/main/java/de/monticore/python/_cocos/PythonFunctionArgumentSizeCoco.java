/* (c) https://github.com/MontiCore/monticore */
package de.monticore.python._cocos;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTCallExpressionCoCo;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.pythonbasis._ast.ASTArgument;
import de.monticore.pythonbasis._ast.ASTArguments;
import de.monticore.pythonbasis._ast.ASTOptionalFunctionParameter;
import de.monticore.python._symboltable.IPythonScope;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

public class PythonFunctionArgumentSizeCoco implements CommonExpressionsASTCallExpressionCoCo {
  
  @Override
  public void check(ASTCallExpression node) {
    List<ASTArgument> args = ((ASTArguments) node.getArguments()).getArgumentList();
    Optional<FunctionSymbol> optionalFunctionSymbol = getFunctionSymbol(node.getExpression());
    if (optionalFunctionSymbol.isPresent()) {
      FunctionSymbol symbol = optionalFunctionSymbol.get();
      
      // can not check parameter size for builtin functions
      if (symbol.getEnclosingScope().getName().equals("__builtin__")) {
        return;
      }
      
      int required = 0;
      for (VariableSymbol parameter : symbol.getParameterList()) {
        if (!(parameter.getAstNode() instanceof ASTOptionalFunctionParameter)) {
          required++;
        }
      }
      String position = " " + node.get_SourcePositionStart();
      if (args.size() < required) {
        Log.error("Too few arguments for function " + symbol.getName() + position);
      }
      
      if (args.size() > symbol.getParameterList().size()) {
        Log.error("Too many arguments for function " + symbol.getName() + position);
      }
    }
  }
  
  private Optional<FunctionSymbol> getFunctionSymbol(ASTExpression node) {
    if (node instanceof ASTNameExpression nameExpression) {
      return ((IPythonScope) node.getEnclosingScope()).resolveFunction(nameExpression.getName());
    }
    else if (node instanceof ASTFieldAccessExpression fieldAccess) {
      return ((IPythonScope) node.getEnclosingScope()).resolveFunction(fieldAccess.getName());
    }
    return Optional.empty();
  }
  
}
