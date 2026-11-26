/* (c) https://github.com/MontiCore/monticore */
package de.monticore.pythonbasis._ast;

/**
 * PythonBasis overrides BooleanLiteral with "True" | "False", so the source is stored as a
 * constant of ASTConstantsPythonBasis. The inherited getValue() compares it with the constant
 * of MCCommonLiterals, which has a different number, and would return false for True.
 */
public class ASTBooleanLiteral extends ASTBooleanLiteralTOP {

  @Override
  public boolean getValue() {
    return getSource() == ASTConstantsPythonBasis.TRUE;
  }
}
