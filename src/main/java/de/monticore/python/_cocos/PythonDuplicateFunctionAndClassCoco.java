/* (c) https://github.com/MontiCore/monticore */
package de.monticore.python._cocos;

import de.monticore.python._ast.*;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PythonDuplicateFunctionAndClassCoco implements PythonASTPythonScriptCoCo,
    PythonASTStatementBlockCoCo, PythonASTClassStatementBlockCoCo {
  
  @Override
  public void check(ASTPythonScript node) {
    this.check(node.getStatementList());
  }
  
  @Override
  public void check(ASTStatementBlock node) {
    // one-line blocks (e.g. "except E: pass") have a single Statement instead of a body
    if (node.isPresentStatementBlockBody()) {
      this.check(node.getStatementBlockBody().getStatementList());
    }
  }
  
  @Override
  public void check(ASTClassStatementBlock node) {
    // one-line classes (e.g. "class A: pass") have a single ClassStatement instead of a body
    if (node.isPresentClassStatementBlockBody()) {
      this.check(node.getClassStatementBlockBody().getClassStatementList());
    }
  }
  
  private void check(List<? extends ASTPythonNode> statements) {
    Set<String> functionNames = new HashSet<>();
    Set<String> classNames = new HashSet<>();
    
    for (ASTPythonNode statement : statements) {
      if (statement instanceof ASTFunctionDeclaration) {
        if (!functionNames.add(((ASTFunctionDeclaration) statement).getName())) {
          Log.error("Duplicate function '" + ((ASTFunctionDeclaration) statement).getName() + "' "
              + statement.get_SourcePositionStart());
        }
      }
      if (statement instanceof ASTClassDeclaration) {
        if (!classNames.add(((ASTClassDeclaration) statement).getName())) {
          Log.error("Duplicate class '" + ((ASTClassDeclaration) statement).getName() + "' "
              + statement.get_SourcePositionStart());
        }
      }
    }
  }
  
}
