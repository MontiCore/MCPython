/* (c) https://github.com/MontiCore/monticore */
package de.monticore.siphytonunits.generator.prettyprint;

import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.python._ast.*;
import de.monticore.python._visitor.PythonHandler;
import de.monticore.python._visitor.PythonTraverser;
import de.monticore.python._visitor.PythonVisitor2;
import de.monticore.sipython._ast.ASTSIInit;
import de.monticore.sipython._ast.ASTSIUnitParse;
import de.monticore.sipython._visitor.SIPythonHandler;
import de.monticore.sipython._visitor.SIPythonTraverser;
import de.monticore.sipython._visitor.SIPythonVisitor2;
import de.monticore.siunits.utility.Converter;
import de.monticore.types.check.DeriveSymTypeOfTestSIJava;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.TypeCalculator;

import javax.measure.converter.UnitConverter;
import javax.measure.unit.Unit;

public class SIPythonPrettyPrinter implements SIPythonHandler, SIPythonVisitor2, PythonHandler, PythonVisitor2 {

    protected SIPythonTraverser siPythonTraverser;
    protected PythonTraverser pythonTraverser;

    @Override
    public SIPythonTraverser getTraverser() {
        return siPythonTraverser;
    }

    @Override
    public void setTraverser(SIPythonTraverser siPythonTraverser) {
        this.siPythonTraverser = siPythonTraverser;
    }

    @Override
    public void setTraverser(PythonTraverser traverser) {
        this.pythonTraverser = traverser;
    }

    IndentPrinter printer;

    public SIPythonPrettyPrinter(IndentPrinter printer) {
        this.printer = printer;
    }

    @Override
    public void traverse(ASTPythonScript node) {
        CommentPrettyPrinter.printPreComments(node, printer);

        for( ASTStatement astStatement: node.getStatementList()) {
            if(astStatement instanceof ASTLocalVariableDeclarationStatement) {
                astStatement.accept(getTraverser());
                printer.println(";");
            } else if(astStatement instanceof ASTIfStatement) {
                astStatement.accept(getTraverser());
                printer.println(";");
            } else if(astStatement instanceof ASTForStatement) {
                astStatement.accept(getTraverser());
                printer.println(";");
            } else if(astStatement instanceof ASTWhileStatement) {
                astStatement.accept(getTraverser());
                printer.println(";");
            } else if(astStatement instanceof ASTFunctionDeclaration) {
                astStatement.accept(getTraverser());
                printer.println(";");
            } else if(astStatement instanceof ASTReturnStatement) {
                astStatement.accept(getTraverser());
                printer.println(";");
            } else if(astStatement instanceof ASTFunctionCall) {
                astStatement.accept(getTraverser());
                printer.println(";");
            } else if(astStatement instanceof ASTExpressionStatement) {
                astStatement.accept(getTraverser());
                printer.println(";");
            }
        }
        CommentPrettyPrinter.printPostComments(node, printer);
    }

    @Override
    public void traverse(ASTLocalVariableDeclarationStatement node) {
        CommentPrettyPrinter.printPreComments(node, printer);
        printer.print(node.getLocalVariableDeclaration().getVariableDeclarator().getDeclarator().getName());
        printer.print(" = ");
        ASTVariableInit astVariableInit = node.getLocalVariableDeclaration().getVariableDeclarator().getVariableInit();
        astVariableInit.accept(getTraverser());
        CommentPrettyPrinter.printPostComments(node, printer);
    }

    @Override
    public void traverse(ASTSIUnitParse node) {
        CommentPrettyPrinter.printPreComments(node, printer);
        node.getSIUnit().accept(getTraverser());

        printer.print("(");

        printer.print(")");

        CommentPrettyPrinter.printPostComments(node, printer);
    }
}
