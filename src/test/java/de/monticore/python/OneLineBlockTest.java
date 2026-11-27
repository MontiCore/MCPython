/* (c) https://github.com/MontiCore/monticore */
package de.monticore.python;

import de.monticore.python._ast.ASTClassDeclaration;
import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._cocos.*;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * One-line blocks ("class A: pass", "except E: pass") have a single statement instead of a block
 * body.
 */
public class OneLineBlockTest {
  
  private static final String MODEL = "class A: pass\n" + "class E(Exception): ...\n" + "try:\n"
      + "    x = 1\n" + "except E: pass\n";
  
  @BeforeEach
  public void init() {
    PythonMill.reset();
    PythonMill.init();
    Log.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSymbolTableForOneLineClasses() throws IOException {
    ASTPythonScript ast = PythonMill.parser().parse_String(MODEL).get();
    PythonMill.scopesGenitorDelegator().createFromAST(ast);
    assertEquals(0, Log.getErrorCount());
  }
  
  @Test
  public void testSymbolTableForOneLineInit() throws IOException {
    ASTPythonScript ast = PythonMill.parser().parse_String(
        "class A:\n    def __init__(self): self.x = 1\n").get();
    PythonMill.scopesGenitorDelegator().createFromAST(ast);
    assertEquals(0, Log.getErrorCount());
    ASTClassDeclaration classA = (ASTClassDeclaration) ast.getStatement(0);
    assertTrue(classA.getSpannedScope().resolveFieldLocally("x").isPresent());
  }
  
  @Test
  public void testDeclarationInStatementBlockCocoOnOneLineBlocks() throws IOException {
    ASTPythonScript ast = PythonMill.parser().parse_String(
        "if x: pass\nelse: pass\nwhile x: pass\nfor i in x: pass\n").get();
    PythonMill.scopesGenitorDelegator().createFromAST(ast);
    
    PythonFunctionOrClassDeclarationInStatementBlockCoco coco =
        new PythonFunctionOrClassDeclarationInStatementBlockCoco();
    PythonCoCoChecker checker = new PythonCoCoChecker();
    checker.addCoCo((PythonASTIfStatementCoCo) coco);
    checker.addCoCo((PythonASTWhileStatementCoCo) coco);
    checker.addCoCo((PythonASTForStatementCoCo) coco);
    checker.checkAll(ast);
    
    assertEquals(0, Log.getErrorCount());
  }
  
  @Test
  public void testDuplicateCocoOnOneLineBlocks() throws IOException {
    ASTPythonScript ast = PythonMill.parser().parse_String(MODEL).get();
    PythonMill.scopesGenitorDelegator().createFromAST(ast);
    
    PythonDuplicateFunctionAndClassCoco coco = new PythonDuplicateFunctionAndClassCoco();
    PythonCoCoChecker checker = new PythonCoCoChecker();
    checker.addCoCo((PythonASTPythonScriptCoCo) coco);
    checker.addCoCo((PythonASTStatementBlockCoCo) coco);
    checker.addCoCo((PythonASTClassStatementBlockCoCo) coco);
    checker.checkAll(ast);
    
    assertEquals(0, Log.getErrorCount());
  }
  
}
