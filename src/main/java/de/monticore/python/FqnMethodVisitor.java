package de.monticore.python;

import de.monticore.python._ast.ASTClassDeclaration;
import de.monticore.python._ast.ASTFunctionDeclaration;
import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._visitor.PythonVisitor2;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class FqnMethodVisitor implements PythonVisitor2 {
  protected final String moduleName;
  protected Stack<String> fqnStack = new Stack<>();
  protected Map<String, ASTFunctionDeclaration> result = new HashMap<>();

  public FqnMethodVisitor(String moduleName) {
    this.moduleName = moduleName;
    fqnStack.push(moduleName);
  }

  @Override
  public void visit(ASTClassDeclaration node) {
    fqnStack.push(node.getName());
  }

  @Override
  public void endVisit(ASTClassDeclaration node) {
    fqnStack.pop();
  }

  @Override
  public void visit(ASTFunctionDeclaration node) {
    fqnStack.push(node.getName());
    result.put(String.join(".", fqnStack), node);
  }

  @Override
  public void endVisit(ASTFunctionDeclaration node) {
    fqnStack.pop();
  }

  public static Map<String, ASTFunctionDeclaration> getFqnToFunctionDecl(String moduleName, ASTPythonScript node){
    var v = new FqnMethodVisitor(moduleName);
    var t = PythonMill.inheritanceTraverser();
    t.add4Python(v);
    node.accept(t);

    return v.result;
  }
}
