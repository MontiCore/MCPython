/* (c) https://github.com/MontiCore/monticore */
package de.monticore.pythonbasis._prettyprint;

import de.monticore.pythonbasis._ast.ASTArgument;
import de.monticore.pythonbasis._ast.ASTStringLiteralPython;
import java.util.Iterator;

// Generated code that was fixed manually, reoved generated comments.
public class PythonBasisPrettyPrinter extends PythonBasisPrettyPrinterTOP {
  
  public PythonBasisPrettyPrinter(de.monticore.prettyprint.IndentPrinter printer,
      boolean printComments) {
    super(printer, printComments);
  }
  
  @Override
  public void handle(de.monticore.pythonbasis._ast.ASTFStringPython node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    if (node.isPresentFSQStringPython()) {
      getPrinter().print("f" + node.getFSQStringPython() + "\'" + " ");
    }
    else if (node.isPresentFDQStringPython()) {
      getPrinter().print("f" + node.getFDQStringPython() + "\" ");
    }
    else if (node.isPresentChar()) {
      getPrinter().print("f\'" + node.getChar() + "\'" + " ");
      
    }
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
  
  @Override
  public void handle(de.monticore.pythonbasis._ast.ASTStringLiteralPython node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    boolean hasSource = hasSource(node);
    if (hasSource) {
      
      if (node.isPresentStringModifier()) {
        node.getStringModifier().accept(getTraverser());
      }
      if (node.isPresentSourceStrPy()) {
        getPrinter().print("\'" + node.getSourceStrPy() + "\'" + " ");
        
      }
      else if (node.isPresentSourceStr()) {
        getPrinter().print("\"" + node.getSourceStr() + "\"" + " ");
        
      }
      else if (node.isPresentSourceChar()) {
        getPrinter().print("\'" + node.getSourceChar() + "\'" + " ");
      }
    }
    else if (node.isPresentFsource() && !(node.isPresentStringModifier() || hasSource)) {
      node.getFsource().accept(getTraverser());
      
    }
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
    
  }
  
  private boolean hasSource(ASTStringLiteralPython node) {
    return node.isPresentSourceStrPy() || node.isPresentSourceStr() || node.isPresentSourceChar();
  }
  
  @Override
  public void handle(de.monticore.pythonbasis._ast.ASTArguments node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    Iterator<ASTArgument> iter_argument = node.getArgumentList().iterator();
    getPrinter().stripTrailing();
    getPrinter().print("(");
    
    if (iter_argument.hasNext()) {
      iter_argument.next().accept(getTraverser());
      while (iter_argument.hasNext()) {
        getPrinter().stripTrailing();
        getPrinter().print(",");
        iter_argument.next().accept(getTraverser());
      }
    }
    getPrinter().stripTrailing();
    getPrinter().print(")");
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
    
  }
  
}
