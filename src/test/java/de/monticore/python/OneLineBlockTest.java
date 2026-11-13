package de.monticore.python;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._cocos.*;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * One-line blocks ("class A: pass", "except E: pass") have a single statement instead of a block body.
 */
public class OneLineBlockTest {

  private static final String MODEL =
      "class A: pass\n" +
      "class E(Exception): ...\n" +
      "try:\n" +
      "    x = 1\n" +
      "except E: pass\n";

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
