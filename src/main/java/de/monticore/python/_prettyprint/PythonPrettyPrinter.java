package de.monticore.python._prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.python._ast.ASTImportStatement;
import de.monticore.python._ast.ASTModuleWithOptionalAlias;

public class PythonPrettyPrinter extends PythonPrettyPrinterTOP {
  public PythonPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  public void handle(ASTImportStatement node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    if (!node.isEmptyLeadingDots() || node.isPresentModule()) {
      getPrinter().print("from ");
    }

    for (String s : node.getLeadingDotsList()) {
      getPrinter().print(".");
    }

    if (node.isPresentModule()) {
      node.getModule().accept(getTraverser());
      getPrinter().print(" ");
    }

    getPrinter().print("import ");
    if (node.isPresentStar()) {
      printer.print("*");
    } else {
      boolean notFirst = false;
      for (ASTModuleWithOptionalAlias module : node.getModuleWithOptionalAliasList()) {
        if (notFirst) {
          getPrinter().print(", ");
        }
        module.accept(getTraverser());
        notFirst = true;
      }
    }
  }
}