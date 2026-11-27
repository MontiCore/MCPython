/* (c) https://github.com/MontiCore/monticore */
package de.monticore.python._cocos;

import de.monticore.python._ast.*;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class PythonFunctionOrClassDeclarationInStatementBlockCoco implements
    PythonASTIfStatementCoCo, PythonASTWhileStatementCoCo, PythonASTForStatementCoCo {
  
  protected void checkStatementBlock(ASTStatementBlock node) {
    // one-line blocks (e.g. "if x: pass") have a single Statement instead of a body
    List<ASTStatement> statements;
    if (node.isPresentStatementBlockBody()) {
      statements = node.getStatementBlockBody().getStatementList();
    }
    else {
      statements = List.of(node.getStatement());
    }
    for (ASTStatement statement : statements) {
      if (statement instanceof ASTFunctionDeclaration || statement instanceof ASTClassDeclaration) {
        Log.error("Function/Class declarations are not allowed in statement block at " + statement
            .get_SourcePositionStart());
      }
    }
  }
  
  @Override
  public void check(ASTIfStatement node) {
    this.checkStatementBlock(node.getThenStatement());
    
    for (ASTStatementBlock statementBlock : node.getElifStatementList()) {
      this.checkStatementBlock(statementBlock);
    }
    
    if (node.isPresentElseStatementPart()) {
      this.checkStatementBlock(node.getElseStatementPart().getStatementBlock());
    }
  }
  
  @Override
  public void check(ASTWhileStatement node) {
    this.checkStatementBlock(node.getStatementBlock());
  }
  
  @Override
  public void check(ASTForStatement node) {
    this.checkStatementBlock(node.getStatementBlock());
  }
  
}
